package com.korzhov.todo.service;

import static java.time.OffsetDateTime.now;

import com.korzhov.todo.config.auth.jwt.JwtTokenProvider;
import com.korzhov.todo.dao.entity.ForgotPassword;
import com.korzhov.todo.dao.entity.PasswordHistory;
import com.korzhov.todo.dao.entity.Task;
import com.korzhov.todo.dao.entity.User;
import com.korzhov.todo.dao.entity.UserVerification;
import com.korzhov.todo.dao.repository.ForgotPasswordRepository;
import com.korzhov.todo.dao.repository.PasswordHistoryRepository;
import com.korzhov.todo.dao.repository.UserRepository;
import com.korzhov.todo.dao.repository.UserVerificationRepository;
import com.korzhov.todo.dto.user.UserDto;
import com.korzhov.todo.dto.user.UserPrincipal;
import com.korzhov.todo.dto.user.password.ForgotPasswordDto;
import com.korzhov.todo.dto.user.request.UpdatePasswordRequest;
import com.korzhov.todo.dto.user.request.UserCreateRequest;
import com.korzhov.todo.dto.user.request.UserLoginRequest;
import com.korzhov.todo.enumeration.task.TaskStatus;
import com.korzhov.todo.enumeration.user.UserRoleEnum;
import com.korzhov.todo.enumeration.user.UserStatusEnum;
import com.korzhov.todo.exception.ResourceAlreadyExistsException;
import com.korzhov.todo.exception.ResourceNotFoundException;
import com.korzhov.todo.exception.UserCurrentStatusException;
import com.korzhov.todo.exception.VerificationTokenFailedException;
import com.korzhov.todo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  @Value("${client.base.url}")
  private String clientUrl;

  private static final String ACTIVATION_EMAIL_MESSAGE = "Hello, %s \n" +
      "Welcome to service. Please, visit this link to activate your account\n" +
      "%s/activation?token=%s";

  private static final String FORGOT_PASSWORD_EMAIL_MESSAGE = "Hello, %s \n" +
      "To change your password, please follow this link \n" +
      "%s/change-password?token=%s .\n" +
      "If you didn't request for any changes. Please ignore this email.";

  private static final String CHANGED_PASSWORD_EMAIL_MESSAGE = "Hello, %s \n" +
      "Your password has been changed successfully. \n" +
      "Please, navigate to login page: %s/login";

  private final UserRepository userRepository;
  private final UserVerificationRepository userVerificationRepository;
  private final ForgotPasswordRepository forgotPasswordRepository;
  private final PasswordHistoryRepository passwordHistoryRepository;

  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final MailService mailService;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final UserMapper userMapper;
  private final FileStorageService storageService;

  @Transactional(readOnly = true)
  public UserDto getUserWithAuthority(UserPrincipal principal) {
    User user = userRepository.findByEmail(principal.getEmail()).orElseThrow(
        () -> new ResourceNotFoundException("User not found!")
    );
    String imageName = user.getImageName();
    UserDto mappedUserDto = userMapper.toDto(user);
    List<Task> taskList = user.getTaskList();
    UserDto userDto = mappedUserDto
        .withTasksCount(taskList.size())
        .withTasksDoneCount(taskList.stream().filter(t -> t.getStatus().equals(TaskStatus.DONE)).count())
        .withTasksToDoCount(taskList.stream().filter(t -> t.getStatus().equals(TaskStatus.TODO)).count());

    return imageName != null
        ? userDto.toBuilder().imageData(storageService.getEncodedImage(imageName)).build()
        : userDto;
  }

  @Transactional
  public void createUser(UserCreateRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ResourceAlreadyExistsException("User with email '%s' already exists!", request.getEmail());
    }

    User user = userMapper.toEntity(request.withPassword(
        passwordEncoder.encode(request.getPassword())
    ));
    User savedUser = userRepository.save(user);

    UserVerification verification = buildActivation(savedUser);

    userVerificationRepository.save(verification);

    sendActivationLink(user, verification.getToken());
  }

  @Transactional
  public UserDto authorizeUser(UserLoginRequest login) {
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword());

    Authentication authentication
        = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

    checkUser(principal);

    String jwt = jwtTokenProvider.createToken(principal, isRememberMe(login));
//    String refreshToken = jwtTokenProvider.createToken(principal, isRememberMe(login));

    User user = userRepository.getUserById(principal.getId());
    user.setLastLoggedInTime(now());

    UserRoleEnum role = principal.getUserRoleEnumSet()
        .stream()
        .findFirst()
        .orElseThrow();

    log.info("Returning user data... ");
    return UserDto.builder()
        .id(principal.getId())
        .email(principal.getEmail())
        .imageData(storageService.getEncodedImage(principal.getImageName()))
        .status(principal.getStatus().toString())
        .role(role.getAuthority())
        .jwtToken(jwt)
        .build();
  }

  @Transactional
  public UserDto activateUser(String token) {
    Long decodedUserId =
        Long.parseLong(new String(Base64.getDecoder().decode(token.getBytes(StandardCharsets.UTF_8))));

    UserVerification verification =
        userVerificationRepository.findById(decodedUserId).orElseThrow(
            () -> new ResourceNotFoundException("Verification not found")
        );

    User user = userRepository.getUserById(decodedUserId);

    if (verification.getExpiryDate().isBefore(now()) ||
        user.getStatus() == UserStatusEnum.ACTIVE) {
      throw new VerificationTokenFailedException("Verification token is expired");
    }
    user.activateUser();

    userVerificationRepository.save(verification);

    return userMapper.toDto(user);
  }

  @Transactional
  public void uploadAvatar(UserPrincipal principal, MultipartFile file) {
    User user = userRepository.findByEmail(principal.getEmail()).orElseThrow(
        () -> new ResourceNotFoundException("User with EMAIL=%s not found", principal.getEmail())
    );
    storageService.deleteIfExists(user.getImageName());
    String savedFileName = storageService.save(file);
    user.setImageName(savedFileName);
  }

  @Transactional
  public void createForgotPassword(ForgotPasswordDto forgotPasswordDto) {
    ForgotPassword forgotPassword = new ForgotPassword();
    User user = userRepository.findByEmail(forgotPasswordDto.getEmail()).orElseThrow(
        () -> new ResourceNotFoundException("User with this email is not found")
    );

    forgotPassword.setRequestTime(forgotPasswordDto.getRequestTime());
    forgotPassword.setUser(user);
    ForgotPassword savedForgotPassword = forgotPasswordRepository.save(forgotPassword);
    sendForgotPasswordLink(user, savedForgotPassword.getGuid());
  }

  @Transactional
  public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {
    ForgotPassword forgotPassword = forgotPasswordRepository.findById(updatePasswordRequest.getGuid()).orElseThrow(
        () -> new ResourceNotFoundException("Session was not found to update password")
    );
    User user = forgotPassword.getUser();
    PasswordHistory history = new PasswordHistory();
    //TODO: VALIDATE if password already used.
    history.setOldPassword(user.getPassword());
    history.setUser(user);
    passwordHistoryRepository.save(history);

    user.setPassword(passwordEncoder.encode(updatePasswordRequest.getPassword()));
    userRepository.save(user);
    sendChangedPasswordMessage(user);
  }

  private boolean isRememberMe(UserLoginRequest login) {
    return login.getRememberMe() != null ? login.getRememberMe() : false;
  }

  private void checkUser(UserPrincipal principal) {
    log.info("Validating user with ID = {}", principal.getId());
    if (!principal.isEnabled() && !principal.isAccountNonLocked()) {
      throw new DisabledException(
          String.format("User with email='%s' is blocked.", principal.getEmail()));
    } else if (principal.isInactive()) {
      throw new UserCurrentStatusException(String.format(
          "User with email='%s' is not active. Please activate it before using.",
          principal.getEmail()));
    }
  }

  private UserVerification buildActivation(User user) {
    String token = new String(Base64.getEncoder()
        .encode(user.getId().toString().getBytes(StandardCharsets.UTF_8)));
    return UserVerification.builder()
        .token(token)
        .user(user)
        .build();
  }

  private void sendActivationLink(User user, String token) {
    log.info("Sending activation email...");
    if (StringUtils.hasText(user.getEmail())) {
      String message = String.format(ACTIVATION_EMAIL_MESSAGE, user.getEmail(), clientUrl, token);
      mailService.sendMessage(user.getEmail(), "Activation code", message);
    }
  }

  private void sendForgotPasswordLink(User user, String guid) {
    log.info("Sending email request for new password...");

    String message = String.format(FORGOT_PASSWORD_EMAIL_MESSAGE, user.getEmail(), clientUrl, guid);

    mailService.sendMessage(user.getEmail(), "Change Password", message);
  }

  private void sendChangedPasswordMessage(User user) {
    log.info("Sending notification about changed password...");

    String message = String.format(CHANGED_PASSWORD_EMAIL_MESSAGE, user.getEmail(), clientUrl);

    mailService.sendMessage(user.getEmail(), "Password is changed", message);
  }


}
