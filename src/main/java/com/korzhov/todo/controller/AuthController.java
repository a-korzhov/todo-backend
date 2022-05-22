package com.korzhov.todo.controller;

import com.korzhov.todo.dto.user.UserPrincipal;
import com.korzhov.todo.dto.user.password.ForgotPasswordDto;
import com.korzhov.todo.dto.user.request.UpdatePasswordRequest;
import com.korzhov.todo.dto.user.request.UserCreateRequest;
import com.korzhov.todo.dto.user.request.UserLoginRequest;
import com.korzhov.todo.config.auth.filter.JwtFilter;
import com.korzhov.todo.service.AuthService;
import com.korzhov.todo.dto.success.SuccessResponse;
import com.korzhov.todo.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/users")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @GetMapping("/current")
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  @SneakyThrows
  public ResponseEntity<UserDto> getCurrentUser(
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    return new ResponseEntity<>(
        authService.getUserWithAuthority(principal),
        HttpStatus.OK
    );
  }

  @PostMapping
  public ResponseEntity<SuccessResponse<?>> createUser(@RequestBody @Valid UserCreateRequest request) {
    log.info("Creating account = '{}'", request.getEmail());
    authService.createUser(request);
    return new ResponseEntity<>(
        SuccessResponse.builder()
            .message("Successfully created user")
            .build(),
        HttpStatus.CREATED);
  }

  @PostMapping(value = "/authorization")
  @SneakyThrows
  public ResponseEntity<?> authorize(@Valid @RequestBody UserLoginRequest login) {
    log.info("Authorizing user {}", login.getEmail());
    UserDto userDto = authService.authorizeUser(login);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + userDto.getJwtToken());
    log.info("Headers added... {}", httpHeaders);
    return new ResponseEntity<>(
        userDto,
        httpHeaders,
        HttpStatus.OK);
  }

  @PutMapping("/activation")
  public ResponseEntity<SuccessResponse<UserDto>> activateUser(@RequestParam String token) {
    UserDto user = authService.activateUser(token);
    return new ResponseEntity<>(
        SuccessResponse.<UserDto>builder()
            .message("User successfully activated")
            .data(user)
            .build(),
        HttpStatus.OK);
  }

  @PostMapping(value = "/upload")
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                       @AuthenticationPrincipal UserPrincipal principal) {
    authService.uploadAvatar(principal, file);
    return ResponseEntity.ok(SuccessResponse.builder()
        .message("Successfully uploaded image")
        .build());
  }

  @PostMapping(value = "/password",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createForgotPasswordSession(@RequestBody @Valid ForgotPasswordDto forgotPasswordDto) {
    authService.createForgotPassword(forgotPasswordDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  //TODO: validate password that its not possible to update on already used password.
  @PutMapping(value = "/password")
  public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
    authService.updatePassword(updatePasswordRequest);
    return ResponseEntity.ok(SuccessResponse.builder()
        .message("Password successfully updated")
        .build());
  }

}
