package com.korzhov.todo.config.auth.permission;

import com.korzhov.todo.dto.user.UserPrincipal;
import com.korzhov.todo.dao.entity.Task;
import com.korzhov.todo.dao.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

  private final TaskRepository taskRepository;

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object operation) {
    if (!(operation instanceof String) && !(targetDomainObject instanceof Long)) {
      return false;
    }
    if (operation.equals("read")) {
      UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
      Optional<Task> task = taskRepository.findById((Long) targetDomainObject);
      if (task.isEmpty()) {
        return false;
      }
      return principal.getId().equals(task.get().getUser().getId());
    }
    return false;
  }

  @Override
  public boolean hasPermission(Authentication auth,
                               Serializable targetId,
                               String targetType,
                               Object permission) {
    return false;
  }
}
