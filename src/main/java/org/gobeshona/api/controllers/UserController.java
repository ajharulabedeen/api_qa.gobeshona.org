package org.gobeshona.api.controllers;

// UserController.java
import lombok.RequiredArgsConstructor;
import org.gobeshona.api.payload.request.PasswordChangeRequest;
import org.gobeshona.api.payload.response.PasswordChangeResponse;
import org.gobeshona.api.security.services.UserService;
import org.gobeshona.api.util.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<PasswordChangeResponse> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            String username = SecurityUtils.getCurrentUsername();
            userService.changePassword(username, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(new PasswordChangeResponse(true, "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new PasswordChangeResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PasswordChangeResponse(false, "An error occurred"));
        }
    }

}
