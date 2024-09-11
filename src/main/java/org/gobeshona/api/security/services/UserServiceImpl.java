package org.gobeshona.api.security.services;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.gobeshona.api.exception.*;
import org.gobeshona.api.models.AuthTypeConstants;
import org.gobeshona.api.models.User;
import org.gobeshona.api.repository.CountryRepository;
import org.gobeshona.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    SmsService smsService;

    @Override
    public User createUser(@Valid User user) throws Exception {
        // Validate email and mobile uniqueness
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already in use: " + user.getEmail());

        }
        if (user.getMobile().length() != 0 && userRepository.existsByMobile(user.getMobile())) {
            throw new MobileNumberAlreadyExistsException("Mobile number is already in use: " + user.getMobile());
        }

        // Validate country code
        if (user.getMobile().length() != 0 && !countryRepository.findByCode(user.getCountryMobile()).isPresent()) {
            throw new InvalidCountryCodeException("Invalid country code: " + user.getCountryMobile());
        }

        // Set usernameType based on email and mobile presence

        if (user.getUsernameType().toLowerCase().equals(AuthTypeConstants.EMAIL.toLowerCase())) {
            user.setUsername(user.getEmail());
        } else if (user.getUsernameType().toLowerCase().equals(AuthTypeConstants.MOBILE.toLowerCase())) {
            user.setUsername(user.getMobile());
        } else {
            throw new ValidationException("Either email or mobile must be provided");
        }

        if (!user.getCountryMobile().equals("BD") && user.getVerificationMethod().equals(AuthTypeConstants.MOBILE)) {
            throw new ValidationException("Mobile Number UserName currenlty only possible for Bangladesh");
        }


        // Validate verificationMethod
        if (!AuthTypeConstants.EMAIL.toLowerCase().equals(user.getVerificationMethod()) &&
                !AuthTypeConstants.MOBILE.toLowerCase().equals(user.getVerificationMethod())) {
            throw new ValidationException("Invalid verification method");
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(Long id, @Valid User userDetails) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            //TODO: kept for Rahul
//            if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
//                throw new EmailAlreadyExistsException("Email is already in use: " + user.getEmail());
//
//            }
//            if (user.getMobile().length() != 0 && userRepository.existsByMobile(user.getMobile())) {
//                throw new MobileNumberAlreadyExistsException("Mobile number is already in use: " + user.getMobile());
//            }
//
//            // Validate country code
//            if (user.getMobile().length() != 0 && !countryRepository.findByCode(user.getCountryMobile()).isPresent()) {
//                throw new InvalidCountryCodeException("Invalid country code: " + user.getCountryMobile());
//            }
//
//            // Set usernameType based on email and mobile presence
//
//            if (user.getUsernameType().toLowerCase().equals(AuthTypeConstants.EMAIL.toLowerCase())) {
//                user.setUsername(user.getEmail());
//            } else if (user.getUsernameType().toLowerCase().equals(AuthTypeConstants.MOBILE.toLowerCase())) {
//                user.setUsername(user.getMobile());
//            } else {
//                throw new ValidationException("Either email or mobile must be provided");
//            }
//
//            if(!user.getCountryMobile().equals("BD") && user.getVerificationMethod().equals(AuthTypeConstants.MOBILE)){
//                throw new ValidationException("Mobile Number UserName currenlty only possible for Bangladesh");
//            }
//
//
//            // Validate verificationMethod
//            if (!AuthTypeConstants.EMAIL.equals(user.getVerificationMethod()) &&
//                    !AuthTypeConstants.MOBILE.equals(user.getVerificationMethod())) {
//                throw new ValidationException("Invalid verification method");
//            }

            return userRepository.save(user);
        } else {
            throw new ValidationException("User not found");
        }
    }

    public void resetPassword(String username) throws UserNotFoundException, UserDisabledException, EmailSendFailException, SMSSendFailException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        if (!user.getEnabled()) {
            throw new UserDisabledException("User is disabled. Please contact the admin.");
        }

        String newPassword = generateRandomPassword();
        String encryptedPassword = passwordEncoder.encode(newPassword);
        boolean passSendingStatus = false;

        // Attempt to send email or SMS based on the user's verification method
        if ("email".equalsIgnoreCase(user.getVerificationMethod())) {
            passSendingStatus = emailService.sendEmail(user.getEmail(), newPassword);
            if (!passSendingStatus) {
                throw new EmailSendFailException("Failed to send the reset password email.");
            }
        } else if ("mobile".equalsIgnoreCase(user.getVerificationMethod())) {
            try {
                smsService.sendSms(user.getMobile(), "আপনার নুতুন পাসওয়ার্ড:\n"+newPassword);
            } catch (Exception e) {
                e.printStackTrace();
                throw new SMSSendFailException("Failed to send the reset password SMS.");
            }


        } else {
            throw new IllegalArgumentException("Unknown verification method: " + user.getVerificationMethod());
        }

        // Save the user's new password after successfully sending email/SMS
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }


    private String generateRandomPassword() {
        // Define the characters for different categories
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String allCharacters = upperCaseLetters + lowerCaseLetters + digits;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category is included
        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));

        // Fill the remaining 5 characters randomly from all characters
        for (int i = 3; i < 8; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        // Shuffle the characters to ensure randomness
        return shuffleString(password.toString());
    }

    // Method to shuffle the characters in a string to ensure randomness
    private String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        StringBuilder result = new StringBuilder(characters.size());
        for (char c : characters) {
            result.append(c);
        }
        return result.toString();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
