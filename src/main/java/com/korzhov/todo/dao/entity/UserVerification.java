package com.korzhov.todo.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "user_verification")
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = UserVerification.JOIN_ACCOUNT_GRAPH,
        attributeNodes = @NamedAttributeNode(value = "user"))
})
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Builder(toBuilder = true)
@Getter
@Immutable
public class UserVerification extends BaseEntity {

  public static final String JOIN_ACCOUNT_GRAPH = "joinUser";
  public static final int EXPIRY_DATE_DURATION_IN_MINUTES = 30;

  @Id
  private Long userId;

  @Column(name = "token")
  private String token;

  @Column(name = "expiry_date")
  private OffsetDateTime expiryDate;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  @MapsId
  @ToString.Exclude
  private User user;

  @PrePersist
  private void setExpiryDate() {
    this.expiryDate = OffsetDateTime.now().plusMinutes(EXPIRY_DATE_DURATION_IN_MINUTES);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    UserVerification that = (UserVerification) o;
    return Objects.equals(userId, that.userId)
        && Objects.equals(token, that.token)
        && Objects.equals(expiryDate, that.expiryDate)
        && Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), userId, token, expiryDate, user);
  }
}
