package com.korzhov.todo.config.auth.filter;

import com.korzhov.todo.config.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_VALUE = "Bearer ";
  public static final int BEARER_STRING_LENGTH = 7;

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    String jwtToken = resolveToken(httpServletRequest);
    String requestURI = httpServletRequest.getRequestURI();

    if (StringUtils.hasText(jwtToken) && jwtTokenProvider.isTokenValid(jwtToken)) {
      Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug("Set Authentication to SecurityContextHolder for '{}', uri: {}", authentication.getName(), requestURI);
    } else {
      log.debug("No valid JWT token found, uri: {}", requestURI);
    }
    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_VALUE)) {
      return bearerToken.substring(BEARER_STRING_LENGTH);
    }
    return null;
  }
}
