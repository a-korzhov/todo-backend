package com.korzhov.todo.config;

import com.korzhov.todo.config.auth.handler.JwtAccessDeniedHandler;
import com.korzhov.todo.config.auth.handler.JwtAuthenticationEntryPoint;
import com.korzhov.todo.config.auth.jwt.JwtConfigurer;
import com.korzhov.todo.config.auth.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CorsFilter corsFilter;
  private final JwtAuthenticationEntryPoint authenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  public WebSecurityConfig(
      JwtTokenProvider jwtTokenProvider,
      @Qualifier(value = "corsFilterV1") CorsFilter corsFilter,
      JwtAuthenticationEntryPoint authenticationEntryPoint,
      JwtAccessDeniedHandler jwtAccessDeniedHandler
  ) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.corsFilter = corsFilter;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
          .csrf().disable()
          .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
          .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
          .accessDeniedHandler(jwtAccessDeniedHandler)
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .authorizeRequests()
          .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
          .antMatchers(
            "/",
            "/*.html",
            "/favicon.ico",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js").permitAll()
          .antMatchers(HttpMethod.POST,
              "/v1/users",
              "/v1/users/authorization", "/v1/users/password").permitAll()
          .antMatchers(HttpMethod.PUT,
            "/v1/users/activation", "/v1/users/password").permitAll()
          .anyRequest().authenticated()
        .and()
          .apply(jwtConfigurerAdapter());
//    http.requiresChannel().anyRequest().requiresInsecure();
    // @formatter:on
  }

  private JwtConfigurer jwtConfigurerAdapter() {
    return new JwtConfigurer(jwtTokenProvider);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
