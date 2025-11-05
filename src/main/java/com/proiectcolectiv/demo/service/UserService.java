package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    /**
     * Creates a new user after checking for duplicate email and encoding the password.
     * @param user the User object to be added
     * @return the saved User object
     */
    User createUser(User user);

    /**
     * Retrieves all users from the repository.
     * @return List of User objects or null if no users found
     */
    List<User> getAllUsers();

    /**
     * Retrieves a user by their unique ID.
     * @param id the UUID of the user
     * @return the User object with the specified ID
     */
    User getUserById(UUID id);
}
