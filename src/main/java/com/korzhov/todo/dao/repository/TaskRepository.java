package com.korzhov.todo.dao.repository;

import com.korzhov.todo.dao.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findAllByUserId(Long id);

}
