package org.gobeshona.api.security.services;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
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
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail().toLowerCase())) {
            throw new ValidationException("Email is already in use");
        }
        if (user.getMobile().length() != 0 && userRepository.existsByMobile(user.getMobile())) {
            throw new ValidationException("Mobile number is already in use");
        }

        // Validate country code
        if (!countryRepository.findByCode(user.getCountryMobile()).isPresent()) {
            throw new ValidationException("Invalid country code");
        }

        // Set usernameType based on email and mobile presence
        if (user.getEmail() != null) {
            user. setUsernameType(AuthTypeConstants.EMAIL);
        } else if (user.getMobile().length() != 0) {
            user.setUsernameType(AuthTypeConstants.MOBILE);
        } else {
            throw new ValidationException("Either email or mobile must be provided");
        }

        if(!user.getCountryMobile().equals("BD") && user.getVerificationMethod().equals(AuthTypeConstants.MOBILE)){
            throw new ValidationException("Mobile Number UserName currenlty only possible for Bangladesh");
        }


        // Validate verificationMethod
        if (!AuthTypeConstants.EMAIL.equals(user.getVerificationMethod()) &&
                !AuthTypeConstants.MOBILE.equals(user.getVerificationMethod())) {
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

            if (userDetails.getFirstName() != null) {
                user.setFirstName(userDetails.getFirstName());
            }
            if (userDetails.getLastName() != null) {
                user.setLastName(userDetails.getLastName());
            }
            if (userDetails.getEmail() != null && !user.getEmail().equals(userDetails.getEmail())) {
                throw new ValidationException("Email cannot be updated");
            }
            if (userDetails.getMobile().length() != 0 && user.getMobile() != userDetails.getMobile()) {
                throw new ValidationException("Mobile number cannot be updated");
            }
            if (userDetails.getCountryMobile() != null) {
                if (!countryRepository.findByCode(userDetails.getCountryMobile()).isPresent()) {
                    throw new ValidationException("Invalid country code");
                }
                user.setCountryMobile(userDetails.getCountryMobile());
            }
            // Prevent updating usernameType
            if (userDetails.getUsernameType() != null && !userDetails.getUsernameType().equals(user.getUsernameType())) {
                throw new ValidationException("Username type cannot be updated");
            }
            // Validate verificationMethod
            if (!AuthTypeConstants.EMAIL.equals(userDetails.getVerificationMethod()) &&
                    !AuthTypeConstants.MOBILE.equals(userDetails.getVerificationMethod())) {
                throw new ValidationException("Invalid verification method");
            }
            user.setVerificationMethod(userDetails.getVerificationMethod());

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
