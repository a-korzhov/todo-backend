package com.korzhov.todo.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "forgot_password")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Immutable
public class ForgotPassword {

  @Id
  @Column(name = "guid")
  private String guid = UUID.randomUUID().toString();

  @Column(name = "request_time")
  @Setter
  private OffsetDateTime requestTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @Setter
  private User user;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ForgotPassword that = (ForgotPassword) o;
    return Objects.equals(guid, that.guid)
        && Objects.equals(requestTime, that.requestTime)
        && Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(guid, requestTime, user);
  }

  @Override
  public String toString() {
    return "ForgotPassword{" +
        "guid='" + guid + '\'' +
        ", requestTime=" + requestTime +
        ", account=" + user +
        '}';
  }
}
