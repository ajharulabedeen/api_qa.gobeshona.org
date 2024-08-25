package org.gobeshona.api.security.services;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.gobeshona.api.exception.EmailAlreadyExistsException;
import org.gobeshona.api.exception.InvalidCountryCodeException;
import org.gobeshona.api.exception.MobileNumberAlreadyExistsException;
import org.gobeshona.api.models.AuthTypeConstants;
import org.gobeshona.api.models.User;
import org.gobeshona.api.repository.CountryRepository;
import org.gobeshona.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

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

        if(!user.getCountryMobile().equals("BD") && user.getVerificationMethod().equals(AuthTypeConstants.MOBILE)){
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
