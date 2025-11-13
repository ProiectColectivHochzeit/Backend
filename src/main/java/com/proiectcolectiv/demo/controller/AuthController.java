package com.proiectcolectiv.demo.controller;


import com.proiectcolectiv.demo.dto.auth.SignInRequest;
import com.proiectcolectiv.demo.dto.auth.SignInResponse;
import com.proiectcolectiv.demo.dto.user.UserDTO;
import com.proiectcolectiv.demo.dto.user.UserResponseDTO;
import com.proiectcolectiv.demo.exception.auth.InvalidPasswordException;
import com.proiectcolectiv.demo.exception.user.DuplicateUserException;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.mapper.UserMapper;
import com.proiectcolectiv.demo.model.User;
import com.proiectcolectiv.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    /**
     * Handles user sign-in requests.
     * @param signInRequest the sign-in request containing email and password
     * throws InvalidPasswordException if the password is invalid
     * @return a response entity containing the sign-in response
     */
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest signInRequest) throws InvalidPasswordException, UserNotFoundException {
        return ResponseEntity.ok(authService.signIn(signInRequest));
    }

    /**
     * Handles user registration requests.
     * @param newUser the user data transfer object containing registration details
     * @return a response entity containing the registered user's response DTO
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserDTO newUser) throws DuplicateUserException {
        User registeredUser = authService.registerUser(newUser);
        UserResponseDTO userResponse = userMapper.userToUserResponseDTO(registeredUser);
        return ResponseEntity.ok(userResponse);
    }

}
