package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.exception.user.DuplicateUserException;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    /**
     * Creates a new user after checking for duplicate email and encoding the password.
     * @param user the User object to be added
     * @throws DuplicateUserException if a user with the same email already exists
     * @return the saved User object
     */
    User createUser(User user) throws DuplicateUserException;

    /**
     * Retrieves all users from the repository.
     * @throws UserNotFoundException if no users are found
     * @return List of User objects or null if no users found
     */
    List<User> getAllUsers() throws UserNotFoundException;

    /**
     * Retrieves a user by their unique ID.
     * @param id the UUID of the user
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @return the User object with the specified ID
     */
    User getUserById(UUID id) throws UserNotFoundException;

    /**
     * Retrieves a user by their email address.
     * @param email the email address of the user
     * @throws UserNotFoundException if the user with the specified email is not found
     * @return the User object with the specified email
     */
    User getUserByEmail(String email) throws UserNotFoundException;
}
