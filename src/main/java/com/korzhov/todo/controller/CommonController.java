package com.korzhov.todo.controller;

import com.korzhov.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CommonController {

  private final UserService userService;

  @GetMapping("/users-count")
  public ResponseEntity<Long> countUsers() {
    return ResponseEntity.ok(userService.getUsersCount());
  }

}
