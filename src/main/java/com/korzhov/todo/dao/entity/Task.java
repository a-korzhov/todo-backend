package com.korzhov.todo.dao.entity;

import com.korzhov.todo.enumeration.task.TaskPriority;
import com.korzhov.todo.enumeration.task.TaskStatus;
import com.korzhov.todo.util.patch.Patchable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = " task")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Task extends BaseEntity implements Patchable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title")
  @Setter
  private String title;

  @Column(name = "priority")
  @Enumerated(EnumType.STRING)
  @Setter
  private TaskPriority priority;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  @Setter
  private TaskStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @Setter
  private User user;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Task task = (Task) o;
    return Objects.equals(id, task.id)
        && Objects.equals(title, task.title)
        && priority == task.priority
        && status == task.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, title, priority, status, user);
  }

  @Override
  public String toString() {
    return "Task{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", priority=" + priority +
        ", status=" + status +
        '}';
  }
}
