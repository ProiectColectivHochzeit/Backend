package com.proiectcolectiv.demo.controller;

import com.proiectcolectiv.demo.dto.user.UserDTO;
import com.proiectcolectiv.demo.dto.user.UserResponseDTO;
import com.proiectcolectiv.demo.exception.user.DuplicateUserException;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.mapper.UserMapper;
import com.proiectcolectiv.demo.model.User;
import com.proiectcolectiv.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Creates a new user.
     * @param userDTO the user data transfer object containing user details
     * @return a response entity containing the created user's response DTO
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserDTO userDTO) throws DuplicateUserException {
        User userToCreate = userMapper.userDTOToUser(userDTO);
        User createdUser = userService.createUser(userToCreate);
        UserResponseDTO response = userMapper.userToUserResponseDTO(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all users.
     * @return a response entity containing a list of user response DTOs
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() throws UserNotFoundException {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(userMapper::userToUserResponseDTO)
                .toList();
        return ResponseEntity.ok(userResponseDTOs);
    }

    /**
     * Retrieves a user by their unique ID.
     * @param id the UUID of the user
     * @return a response entity containing the user's response DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) throws UserNotFoundException {
        User user = userService.getUserById(id);
        UserResponseDTO response = userMapper.userToUserResponseDTO(user);
        return ResponseEntity.ok(response);
    }
}
