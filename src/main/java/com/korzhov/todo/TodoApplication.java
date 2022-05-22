package com.korzhov.todo;

import com.korzhov.todo.dao.entity.User;
import com.korzhov.todo.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class TodoApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(TodoApplication.class, args);
  }

  private final UserRepository userRepository;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    List<User> users = userRepository.findAll();
    users.forEach(user -> user.setImageName(null));
  }
}
