package com.korzhov.todo.dao.repository;

import com.korzhov.todo.dao.entity.User;
import com.korzhov.todo.dao.repository.projection.UserDetailsProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  Optional<UserDetailsProjection> findUserDetailsByEmail(String email);

  @EntityGraph(value = User.USER_WITH_ONE_TO_ONE_JOINS, type = EntityGraph.EntityGraphType.FETCH)
  Optional<User> findByEmail(String email);

  @EntityGraph(value = User.USER_WITH_ONE_TO_ONE_JOINS, type = EntityGraph.EntityGraphType.FETCH)
  User getUserById(Long id);
}
