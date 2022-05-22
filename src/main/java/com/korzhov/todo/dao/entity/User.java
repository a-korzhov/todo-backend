package com.korzhov.todo.dao.entity;

import com.korzhov.todo.enumeration.user.UserRoleEnum;
import com.korzhov.todo.enumeration.user.UserStatusEnum;
import com.korzhov.todo.util.patch.Patchable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@NamedEntityGraphs({
    @NamedEntityGraph(
        name = User.USER_WITH_ONE_TO_ONE_JOINS,
        attributeNodes = {
            @NamedAttributeNode("userVerification"),
        }
    )
})
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Builder(toBuilder = true)
@Getter
public class User extends BaseEntity implements Patchable {

  public static final String USER_WITH_ONE_TO_ONE_JOINS = "oneToOneJoins";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "encoded_password")
  @Setter
  private String password;

  @Column(name = "email")
  @Setter
  private String email;

  @Column(name = "phone")
  @Setter
  private String phone;

  @Column(name = "image_name")
  @Setter
  private String imageName;

  @Column(name = "last_logged_in_time")
  @Setter
  private OffsetDateTime lastLoggedInTime;

  @Column(name = "role", updatable = false)
  @Enumerated(EnumType.STRING)
  private UserRoleEnum role;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private UserStatusEnum status;

  @OneToOne(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      mappedBy = "user"
  )
  @ToString.Exclude
  private UserVerification userVerification;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
  @ToString.Exclude
  private final List<Task> taskList = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    User user = (User) o;
    return Objects.equals(id, user.id)
        && Objects.equals(password, user.password)
        && Objects.equals(email, user.email)
        && Objects.equals(imageName, user.imageName)
        && Objects.equals(lastLoggedInTime, user.lastLoggedInTime)
        && role == user.role
        && status == user.status
        && Objects.equals(taskList, user.taskList)
        && Objects.equals(userVerification, user.userVerification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(),
        id, password, email, imageName, lastLoggedInTime,
        role, status, userVerification, taskList);
  }

  @PrePersist
  public void initStatus() {
    this.status = UserStatusEnum.INACTIVE;
  }

  public void activateUser() {
    this.status = UserStatusEnum.ACTIVE;
  }

  public void blockUser() {
    this.status = UserStatusEnum.BLOCKED;
  }


}
