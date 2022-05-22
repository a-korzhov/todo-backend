package com.korzhov.todo.config.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request,
                     HttpServletResponse response,
                     AccessDeniedException accessDeniedException)
      throws IOException {
    // send a 403 Forbidden response when user tries
    // to access a secured REST resource without the necessary authorization
    log.error(accessDeniedException.getMessage());
    response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
  }
}
