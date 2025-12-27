package dev.juviscript.techdeck.services;

import dev.juviscript.techdeck.dto.request.auth.LoginRequest;
import dev.juviscript.techdeck.dto.request.auth.RegisterRequest;
import dev.juviscript.techdeck.dto.response.AuthResponse;
import dev.juviscript.techdeck.dto.response.UserResponse;
import dev.juviscript.techdeck.mappers.UserMapper;
import dev.juviscript.techdeck.models.User;
import dev.juviscript.techdeck.repositories.UserRepository;
import dev.juviscript.techdeck.security.JwtService;
import dev.juviscript.techdeck.security.UserDetailsImpl;
import dev.juviscript.techdeck.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        // Normalize email
        String normalizedEmail = StringUtils.normalizeEmail(request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already in use: " + normalizedEmail);
        }

        // Create new user with normalized data
        User user = new User();
        user.setFirstName(StringUtils.capitalizeFirst(request.getFirstName()));
        user.setLastName(StringUtils.capitalizeFirst(request.getLastName()));
        user.setEmail(normalizedEmail);
        user.setPhoneNumber(StringUtils.normalizePhone(request.getPhoneNumber()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(true);

        // Save user
        User savedUser = userRepository.save(user);

        // Generate tokens
        UserDetailsImpl userDetails = UserDetailsImpl.build(savedUser);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Build response
        UserResponse userResponse = userMapper.toDTO(savedUser);
        return AuthResponse.of(accessToken, refreshToken, userResponse);
    }

    /**
     * Authenticate user and return tokens
     */
    public AuthResponse login(LoginRequest request) {
        // Normalize email for lookup
        String normalizedEmail = StringUtils.normalizeEmail(request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.getPassword()
                )
        );

        // Get user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Get user from database for full details
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Build response
        UserResponse userResponse = userMapper.toDTO(user);
        return AuthResponse.of(accessToken, refreshToken, userResponse);
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        // Extract username from refresh token
        String email = jwtService.extractUsername(refreshToken);

        // Load user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Build user details
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        // Build response (keep same refresh token)
        UserResponse userResponse = userMapper.toDTO(user);
        return AuthResponse.of(newAccessToken, refreshToken, userResponse);
    }
}