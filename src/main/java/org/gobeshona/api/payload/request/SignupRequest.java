package org.gobeshona.api.payload.request;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
    @NotEmpty(message = "First name is required")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

//    @NotEmpty(message = "Mobile number is required")
    private String mobile;

//    @NotNull(message = "Country code is required")
    private String countryMobile;

    @NotEmpty(message = "Username is required")
    private String username;

    @NotNull(message = "Username type is required")
    private String usernameType;

    @NotNull(message = "Verification method is required")
    private String verificationMethod;

    @NotEmpty(message = "Password is required")
    private String password;

    private Set<String> role;
}
