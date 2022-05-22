package com.korzhov.todo.controller;

import static com.korzhov.todo.util.patch.PatchConstants.PATCH_MEDIA_TYPE;

import com.fasterxml.jackson.databind.JsonNode;
import com.korzhov.todo.dto.user.UserDto;
import com.korzhov.todo.service.UserService;
import com.korzhov.todo.util.validators.annotation.PatchConstraint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

  private final UserService userService;

  @GetMapping("/search")
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<UserDto> getUser(
      @RequestParam(name = "id", required = false) Optional<Long> id
  ) {
    log.info("Searching user with ID: {}", id);
    if (id.isPresent()) {
      return ResponseEntity.ok(userService.getUserById(id.get()));
    } else {
      throw new IllegalArgumentException("Set one of the query params: id or name");
    }
  }

  @PatchMapping(consumes = PATCH_MEDIA_TYPE)
  @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<List<UserDto>> patchUser(
      @RequestBody @PatchConstraint JsonNode patchRequestBody) {
    return ResponseEntity.accepted()
        .body(userService.patchUsers(patchRequestBody));
  }
}
