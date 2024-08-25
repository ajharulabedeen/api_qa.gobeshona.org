package org.gobeshona.api.controllers;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.gobeshona.api.models.AuthTypeConstants;
import org.gobeshona.api.models.ERole;
import org.gobeshona.api.models.Role;
import org.gobeshona.api.models.User;
import org.gobeshona.api.payload.request.SignupRequest;
import org.gobeshona.api.payload.response.MessageResponse;
import org.gobeshona.api.repository.RoleRepository;
import org.gobeshona.api.security.jwt.JwtUtils;
import org.gobeshona.api.security.services.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.Set;

//@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
@ApiController
@RequestMapping("/api/public")
public class TestController {

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


  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public String userAccess() {
    return "User Content.";
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR')")
  public String moderatorAccess() {
    return "Moderator Board.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }



  @PostMapping("/register")
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

}
