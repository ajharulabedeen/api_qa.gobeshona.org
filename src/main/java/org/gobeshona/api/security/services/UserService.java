package org.gobeshona.api.security.services;

import jakarta.validation.Valid;
import org.gobeshona.api.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(@Valid User user) throws Exception;

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User updateUser(Long id, @Valid User userDetails) throws Exception;

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean changePassword(String username, String oldPassword, String newPassword);
}
