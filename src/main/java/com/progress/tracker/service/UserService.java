package com.progress.tracker.service;

import com.progress.tracker.dto.PasswordChangeDto;
import com.progress.tracker.dto.UserAdminDto;
import com.progress.tracker.dto.UserProfileDto;
import com.progress.tracker.entity.Role;
import com.progress.tracker.entity.User;
import com.progress.tracker.exception.InvalidTaskDateException;
import com.progress.tracker.exception.ResourceNotFoundException;
import com.progress.tracker.repository.TaskRepository;
import com.progress.tracker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       TaskRepository taskRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public User registerUser(String username, String email, String rawPassword, String firstName, String lastName, Set<Role> roles) {
        if (userRepository.existsByUsername(username.trim())) {
            throw new InvalidTaskDateException("Username '" + username.trim() + "' is already taken");
        }
        if (userRepository.existsByEmail(email.trim())) {
            throw new InvalidTaskDateException("Email '" + email.trim() + "' is already registered");
        }

        User user = User.builder()
                .username(username.trim())
                .email(email.trim())
                .password(passwordEncoder.encode(rawPassword))
                .firstName(firstName != null ? firstName.trim() : null)
                .lastName(lastName != null ? lastName.trim() : null)
                .enabled(true)
                .roles(roles != null && !roles.isEmpty() ? roles : Set.of(Role.ROLE_USER))
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public UserProfileDto updateProfile(User currentUser, UserProfileDto dto) {
        User user = findById(currentUser.getId());

        if (!user.getEmail().equalsIgnoreCase(dto.getEmail().trim()) &&
            userRepository.existsByEmail(dto.getEmail().trim())) {
            throw new InvalidTaskDateException("Email '" + dto.getEmail().trim() + "' is already in use by another user");
        }

        user.setEmail(dto.getEmail().trim());
        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());

        User saved = userRepository.save(user);
        return UserProfileDto.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .build();
    }

    @Transactional
    public void changePassword(User currentUser, PasswordChangeDto dto) {
        User user = findById(currentUser.getId());

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidTaskDateException("Current password does not match");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new InvalidTaskDateException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<UserAdminDto> getAllUsersForAdmin(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(u -> UserAdminDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .enabled(u.isEnabled())
                .roles(u.getRoles())
                .totalTasks(taskRepository.countByUser(u))
                .createdAt(u.getCreatedAt())
                .build());
    }

    @Transactional
    public void toggleUserStatus(Long userId, User adminUser) {
        User user = findById(userId);

        if (user.getId().equals(adminUser.getId())) {
            throw new InvalidTaskDateException("You cannot deactivate your own admin account");
        }

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }
}
