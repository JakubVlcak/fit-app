package com.github.fit_app.service;

import com.github.fit_app.entity.User;
import com.github.fit_app.exception.EntityNotFoundException;
import com.github.fit_app.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User login(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            // Access specific claims
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            if (existsByEmail(email)) {
                User user = findByEmail(email).orElseThrow(() -> new EntityNotFoundException(User.class, null));
                if (user != null) {
                    return user;
                }
            } else {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(name);
                return register(newUser);
            }
        }

        throw new IllegalStateException("Invalid login attempt");
    }

    @Transactional
    public User getUserFromAuthentication(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String email = jwt.getClaimAsString("email");
            return findByEmail(email).orElseThrow(() -> new EntityNotFoundException(User.class, null));
        }
        throw new IllegalStateException("Invalid authentication attempt");
    }

    private User register(User user) {
        user.setId(null);
        return userRepository.save(user);
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
