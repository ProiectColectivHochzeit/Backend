package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.model.User;
import com.proiectcolectiv.demo.repository.UserRepository;
import com.proiectcolectiv.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User createUser(User user) {
        log.info("UserService - Attempting to add user with email: {}", user.getEmail());
        checkForDuplicateEmail(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.info("UserService - No users found in the repository");
            return null;
        }
        return users;
    }

    @Override
    public User getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.info("UserService - User with ID: {} not found", id);
            throw new RuntimeException("User not found");
        }
        return user.get();
    }

    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.info("UserService - User with email: {} not found", email);
            throw new RuntimeException("User not found");
        }
        return user.get();
    }

    /**
     * Checks for duplicate email in the repository.
     * @param email the email to check
     * @throws RuntimeException if a user with the same email already exists
     */
    private void checkForDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("User with email: {} already exists", email);
            throw new RuntimeException("Email already in use");
        }
    }
}
