package com.xdeew.finance.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xdeew.finance.auth.dto.LoginRequest;
import com.xdeew.finance.auth.dto.LoginResponse;
import com.xdeew.finance.auth.dto.RegisterRequest;
import com.xdeew.finance.auth.dto.RegisterResponse;
import com.xdeew.finance.category.service.DefaultCategoryService;
import com.xdeew.finance.common.exception.UnauthorizedException;
import com.xdeew.finance.user.model.User;
import com.xdeew.finance.user.model.UserRole;
import com.xdeew.finance.user.service.UserService;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DefaultCategoryService defaultCategoryService;

    public AuthService(UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            DefaultCategoryService defaultCategoryService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.defaultCategoryService = defaultCategoryService;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userService.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(UserRole.USER)
                .build();

        User savedUser = userService.save(user);

        defaultCategoryService.createDefaultCategoriesIfNeeded(savedUser);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (!passwordMatches) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs()
        );
    }
}
