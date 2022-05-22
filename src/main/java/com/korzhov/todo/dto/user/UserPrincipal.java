package com.korzhov.todo.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.korzhov.todo.enumeration.user.UserRoleEnum;
import com.korzhov.todo.enumeration.user.UserStatusEnum;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Value
@Builder
@Jacksonized
public class UserPrincipal implements UserDetails {

  Long id;

  @JsonIgnore
  String password;

  String email;

  String imageName;

  Set<UserRoleEnum> userRoleEnumSet;

  UserStatusEnum status;

  public String getEmail() {
    return email;
  }

  public UserStatusEnum getStatus() {
    return status;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.userRoleEnumSet;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  @Deprecated
  public String getUsername() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.status != UserStatusEnum.BLOCKED;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.status == UserStatusEnum.ACTIVE;
  }

  public boolean isInactive() {
    return this.status == UserStatusEnum.INACTIVE;
  }

  public Set<UserRoleEnum> getUserRoleEnumSet() {
    return userRoleEnumSet;
  }

  public String getImageName() {
    return imageName;
  }

  public Long getId() {
    return id;
  }
}
