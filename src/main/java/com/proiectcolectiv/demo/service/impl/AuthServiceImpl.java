package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.dto.auth.SignInRequest;
import com.proiectcolectiv.demo.dto.auth.SignInResponse;
import com.proiectcolectiv.demo.dto.user.UserDTO;
import com.proiectcolectiv.demo.exception.auth.InvalidPasswordException;
import com.proiectcolectiv.demo.exception.auth.InvalidTokenException;
import com.proiectcolectiv.demo.exception.user.DuplicateUserException;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.mapper.UserMapper;
import com.proiectcolectiv.demo.model.User;
import com.proiectcolectiv.demo.repository.UserRepository;
import com.proiectcolectiv.demo.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public SignInResponse signIn(SignInRequest signInRequest) throws UserNotFoundException, InvalidPasswordException {
        log.info("Signing in user with email: {}", signInRequest.getEmail());
        Optional<User> user = userRepository.findByEmail(signInRequest.getEmail());
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        User foundUser = user.get();
        if (!passwordEncoder.matches(signInRequest.getPassword(), foundUser.getPassword())) {
            throw new InvalidPasswordException();
        }

        String token = generateToken(foundUser);
        return new SignInResponse(token);

    }

    @Override
    public User registerUser(UserDTO userDTO) throws DuplicateUserException {
        log.info("Registering user with email: {}", userDTO.getEmail());
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateUserException();
        }

        User newUser = userMapper.userDTOToUser(userDTO);
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setEmail(userDTO.getEmail());
        log.info("User registered with email: {}", newUser.getEmail());
        return userRepository.save(newUser);

    }

    @Override
    public boolean validateToken(String token) throws InvalidTokenException {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new InvalidTokenException();
        }
    }

    /**
     * Generates a JWT token for the given user.
     * @param user the user for whom to generate the token
     * @return the generated JWT token
     */
    private String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(jwtSecret)
                .compact();

    }
}
