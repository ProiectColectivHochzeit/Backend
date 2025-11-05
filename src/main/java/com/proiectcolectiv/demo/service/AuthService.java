package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.dto.auth.SignInRequest;
import com.proiectcolectiv.demo.dto.auth.SignInResponse;
import com.proiectcolectiv.demo.dto.user.UserDTO;
import com.proiectcolectiv.demo.exception.auth.InvalidPasswordException;
import com.proiectcolectiv.demo.model.User;

public interface AuthService {

    /**
     * Signs in a user with the provided credentials.
     * @param signInRequest the request containing user email and password
     * @throws InvalidPasswordException if the password is invalid
     * @return SignInResponse containing JWT token
     */
    SignInResponse signIn(SignInRequest signInRequest) throws InvalidPasswordException;

    /**
     * Registers a new user.
     * @param userDTO the user data transfer object containing user details
     * @return the registered User
     */
    User registerUser(UserDTO userDTO);

    /**
     * Validates a JWT token.
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);

}
