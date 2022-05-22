package com.korzhov.todo.config.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException)
      throws IOException {
    // send a 401 Unauthorized response
    // when user tries to access a secured REST resource without supplying any credentials
    log.error(authException.getMessage());
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
        authException.getMessage());
  }
}
