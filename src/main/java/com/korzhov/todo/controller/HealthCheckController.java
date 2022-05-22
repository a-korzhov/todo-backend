package com.korzhov.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health_check")
@Validated
public class HealthCheckController {

  @GetMapping
  public ResponseEntity<?> healthCheck() {
    return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
  }

}
