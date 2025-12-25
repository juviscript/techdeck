package dev.juviscript.techdeck.controllers;

import dev.juviscript.techdeck.dto.request.CreateUserRequest;
import dev.juviscript.techdeck.dto.request.UpdateUserRequest;
import dev.juviscript.techdeck.dto.response.UserResponse;
import dev.juviscript.techdeck.mappers.UserMapper;
import dev.juviscript.techdeck.models.User;
import dev.juviscript.techdeck.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * GET /api/v1/users
     * Get all users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(userMapper::toDTO)
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/users/{id}
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/users
     * Create a new user
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        // TODO: Encode password before saving (will add with Spring Security)
        User savedUser = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toDTO(savedUser));
    }

    /**
     * PUT /api/v1/users/{id}
     * Update an existing user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {

        return userService.getUserById(id)
                .map(existingUser -> {
                    // Only update fields that are provided
                    if (request.getFirstName() != null) {
                        existingUser.setFirstName(request.getFirstName());
                    }
                    if (request.getLastName() != null) {
                        existingUser.setLastName(request.getLastName());
                    }
                    if (request.getPhoneNumber() != null) {
                        existingUser.setPhoneNumber(request.getPhoneNumber());
                    }
                    if (request.getRole() != null) {
                        existingUser.setRole(request.getRole());
                    }
                    if (request.getIsActive() != null) {
                        existingUser.setActive(request.getIsActive());
                    }

                    User updatedUser = userService.updateUser(id, existingUser);
                    return ResponseEntity.ok(userMapper.toDTO(updatedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/v1/users/{id}
     * Deactivate a user (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID id) {
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/users/check-email?email={email}
     * Check if email is available
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailAvailability(@RequestParam String email) {
        return ResponseEntity.ok(userService.isEmailAvailable(email));
    }
}