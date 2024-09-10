package org.gobeshona.api.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import jakarta.validation.ValidationException;
import org.gobeshona.api.exception.UserDisabledException;
import org.gobeshona.api.exception.UserNotFoundException;
import org.gobeshona.api.models.AuthTypeConstants;
import org.gobeshona.api.models.ERole;
import org.gobeshona.api.models.Role;
import org.gobeshona.api.models.User;
import org.gobeshona.api.payload.request.LoginRequest;
import org.gobeshona.api.payload.request.SignupRequest;
import org.gobeshona.api.payload.response.JwtResponse;
import org.gobeshona.api.payload.response.JwtResponseWeb;
import org.gobeshona.api.payload.response.MessageResponse;
import org.gobeshona.api.payload.response.PasswordResetResponse;
import org.gobeshona.api.security.jwt.JwtUtils;
import org.gobeshona.api.security.services.SmsService;
import org.gobeshona.api.security.services.UserDetailsImpl;
import org.gobeshona.api.security.services.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.gobeshona.api.repository.RoleRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserServiceImpl userService;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private SmsService smsService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    JwtResponse jwtResponse = new JwtResponse(jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles);

    return ResponseEntity.ok(jwtResponse);
  }
  @PostMapping("/web-signin")
  public ResponseEntity<?> webAuthenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    JwtResponseWeb jwtResponse = new JwtResponseWeb(jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getPassword(),
            userDetails.getEmail(),
            roles);

    return ResponseEntity.ok(jwtResponse);
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws Exception{

    if (signUpRequest.getUsernameType().toLowerCase().equals(AuthTypeConstants.EMAIL.toLowerCase())) {
      signUpRequest.setUsername(signUpRequest.getEmail());
    } else if (signUpRequest.getUsernameType().toLowerCase().equals(AuthTypeConstants.MOBILE.toLowerCase())) {
      signUpRequest.setUsername(signUpRequest.getMobile());
    } else {
      throw new ValidationException("Either email or mobile must be provided");
    }


    if (userService.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

//    if (userService.existsByEmail(signUpRequest.getEmail())) {
//      return ResponseEntity
//          .badRequest()
//          .body(new MessageResponse("Error: Email is already in use!"));
//    }

    // Create new user's account
//      User user = new User(signUpRequest.getUsername(), (signUpRequest.getFirstName()+signUpRequest.getLastName()),
//              signUpRequest.getEmail(),
//              encoder.encode(signUpRequest.getPassword()));


    User user = modelMapper.map(signUpRequest, User.class);
    user.setPassword(encoder.encode(signUpRequest.getPassword()));
    user.setEnabled(true); // Set additional properties if needed
    user.setVerified(false);

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

//    todo: have to refactor.
    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "mod":
          Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userService.createUser(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<PasswordResetResponse> resetPassword(@RequestParam("username") String username) {
    try {
      userService.resetPassword(username);
      return ResponseEntity.ok(new PasswordResetResponse(true, "New password has been sent to your mobile/email."));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PasswordResetResponse(false, e.getMessage()));
    } catch (UserDisabledException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new PasswordResetResponse(false, e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PasswordResetResponse(false, "An error occurred while resetting the password."));
    }
  }

  @GetMapping("/send-sms")
  public String sendSms(@RequestParam String phoneNumber, @RequestParam String message) {
    return smsService.sendSms(phoneNumber, message);
  }


}
