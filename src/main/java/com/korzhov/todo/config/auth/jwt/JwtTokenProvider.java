package com.korzhov.todo.config.auth.jwt;

import com.korzhov.todo.dto.user.UserPrincipal;
import com.korzhov.todo.enumeration.user.UserRoleEnum;
import com.korzhov.todo.enumeration.user.UserStatusEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import javax.annotation.PostConstruct;

@Component
@Slf4j
public class JwtTokenProvider {

  private static final String AUTH_KEY = "auth";
  private static final String USER_STATUS_KEY = "status";
  private static final String USER_ID_KEY = "id";

  private final String base64Secret;
  private final long tokenExpirationTimeInMs;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String base64Secret,
      @Value("${jwt.token.expiration-time-in-seconds}") long tokenExpirationTimeInSeconds
  ) {
    this.base64Secret = base64Secret;
    this.tokenExpirationTimeInMs = tokenExpirationTimeInSeconds * 1000;
  }

  private Key key;

  @PostConstruct
  public void init() {
    byte[] decode = Decoders.BASE64.decode(base64Secret);
    this.key = Keys.hmacShaKeyFor(decode);
  }

  public String createToken(UserPrincipal principal, boolean rememberMe) {
    String authority = principal.getAuthorities().stream()
        .findFirst().orElseThrow(
            () -> new IllegalArgumentException("Failed to retrieve authority")
        ).getAuthority();

    long now = new Date().getTime();
    Date validity;
    if (!rememberMe) {
      validity = new Date(now + this.tokenExpirationTimeInMs);
    } else {
      validity = new Date(now + (604800 * 1000));
    }
    log.debug("Token will expire in {} days", (validity.getTime() - now) / 86_400_000L);

    return Jwts.builder()
        .setSubject(principal.getEmail())
        .claim(USER_ID_KEY, principal.getId().toString())
        .claim(AUTH_KEY, authority)
        .claim(USER_STATUS_KEY, principal.getStatus().toString())
        .signWith(key, SignatureAlgorithm.HS256)
        .setExpiration(validity)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token).getBody();

    Set<UserRoleEnum> authorities =
        Collections.singleton(UserRoleEnum.valueOf(claims.get(AUTH_KEY).toString()));
    UserPrincipal principal = UserPrincipal.builder()
        .password("")
        .id(Long.parseLong((String) claims.get(USER_ID_KEY)))
        .email(claims.getSubject())
        .userRoleEnumSet(authorities)
        .status(UserStatusEnum.fromString((String) claims.get(USER_STATUS_KEY)))
        .build();
    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  public boolean isTokenValid(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      log.info("Invalid JWT signature.");
    } catch (ExpiredJwtException e) {
      log.info("Expired JWT token.");
    } catch (UnsupportedJwtException e) {
      log.info("Unsupported JWT token.");
    } catch (IllegalArgumentException e) {
      log.info("JWT token compact of handler are invalid.");
    }
    return false;
  }

}
