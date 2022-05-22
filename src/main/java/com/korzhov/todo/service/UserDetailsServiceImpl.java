package com.korzhov.todo.service;

import com.korzhov.todo.dto.user.UserPrincipal;
import com.korzhov.todo.dao.repository.UserRepository;
import com.korzhov.todo.dao.repository.projection.UserDetailsProjection;
import com.korzhov.todo.enumeration.user.UserRoleEnum;
import com.korzhov.todo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    log.info("Loading user with email = {}", email);
    return buildPrincipalUser(userRepository.findUserDetailsByEmail(email).orElseThrow(
        () -> new ResourceNotFoundException("User with %s email is not found", email)));
  }

  private UserPrincipal buildPrincipalUser(UserDetailsProjection account) {
    Set<UserRoleEnum> authorities = Collections.singleton(account.getRole());
    log.info("Building UserPrincipal with ID = {}", account.getId());
    return UserPrincipal.builder()
        .id(account.getId())
        .password(account.getPassword())
        .email(account.getEmail())
        .imageName(account.getImageName())
        .userRoleEnumSet(authorities)
        .status(account.getStatus())
        .build();
  }
}
