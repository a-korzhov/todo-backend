package com.korzhov.todo.service;

import static com.korzhov.todo.util.patch.JsonPatchParser.parsePatchRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korzhov.todo.dao.entity.User;
import com.korzhov.todo.dao.repository.UserRepository;
import com.korzhov.todo.dto.user.UserDto;
import com.korzhov.todo.dto.user.request.UserPatchRequest;
import com.korzhov.todo.exception.ResourceNotFoundException;
import com.korzhov.todo.mapper.UserMapper;
import com.korzhov.todo.util.patch.PatchContainer;
import com.korzhov.todo.util.patch.PatchListResolver;
import com.korzhov.todo.util.patch.PatchResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final FileStorageService storageService;

  private final UserMapper userMapper;
  private final ObjectMapper mapper;

  @Transactional(readOnly = true)
  public UserDto getUserById(Long id) {
    User user = userRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("User with ID=%s is not found", id)
    );
    return userMapper.toDto(user)
        .withImageData(storageService.getEncodedImage(user.getImageName()));
  }

  public long getUsersCount() {
    return userRepository.count();
  }

  @Transactional
  public List<UserDto> patchUsers(JsonNode patchRequestBody) {
    Map<Long, PatchContainer<User>> idToUserContainerMap = parsePatchRequest(patchRequestBody);

    Map<Long, User> idToUserMap =
        userRepository.findAllById(idToUserContainerMap.keySet()).stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

    idToUserContainerMap.entrySet().removeIf(entry ->
        !idToUserMap.containsKey(entry.getKey()));

    idToUserContainerMap.forEach((id, container) ->
        container.setEntity(idToUserMap.get(id)));

    return PatchListResolver
        .patchDtoList(idToUserContainerMap, this::patchUser)
        .stream()
        .filter(Objects::nonNull)
        .map(userMapper::toDto)
        .collect(Collectors.toList());
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(
        () -> new ResourceNotFoundException("User with EMAIL=%s not found", email)
    );
  }

  private User patchUser(PatchContainer<User> container) {
    User user = container.getEntity();

    UserPatchRequest patchedUserDto = new PatchResolver<>(userMapper.toPatchDto(user), mapper)
        .patch(container, UserPatchRequest.class);

    userMapper.patchFields(patchedUserDto, user);

    return userRepository.save(user);
  }
}
