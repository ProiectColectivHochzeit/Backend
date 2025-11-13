package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.dto.auth.SignInRequest;
import com.proiectcolectiv.demo.dto.auth.SignInResponse;
import com.proiectcolectiv.demo.dto.user.UserDTO;
import com.proiectcolectiv.demo.exception.auth.InvalidPasswordException;
import com.proiectcolectiv.demo.exception.auth.InvalidTokenException;
import com.proiectcolectiv.demo.exception.user.DuplicateUserException;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.model.User;

public interface AuthService {

    /**
     * Signs in a user with the provided credentials.
     * @param signInRequest the request containing user email and password
     * @throws InvalidPasswordException if the password is invalid
     * @throws UserNotFoundException if the user is not found
     * @return SignInResponse containing JWT token
     */
    SignInResponse signIn(SignInRequest signInRequest) throws InvalidPasswordException, UserNotFoundException;

    /**
     * Registers a new user.
     * @param userDTO the user data transfer object containing user details
     * @throws DuplicateUserException if a user with the same email already exists
     * @return the registered User
     */
    User registerUser(UserDTO userDTO) throws DuplicateUserException;

    /**
     * Validates a JWT token.
     * @param token the JWT token to validate
     * @throws InvalidTokenException if the token is invalid
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token) throws InvalidTokenException;

}
