package com.example.my_app.Handler;

import com.example.my_app.DTO.LoginRequestDTO;
import com.example.my_app.DTO.UserDTO;
import com.example.my_app.Model.User;
import com.example.my_app.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserHandler {

    private final UserRepository userRepository;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Autowired
    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    Validating user's email with corresponding request
    Checking via regex.
     */
    public boolean userEmailNotValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }

    /*
    Check password length
     */

    public boolean passwordNotValid(String password) {
        return password.length() <= 6;
    }

    /*
    Validating user's request, if any fields null or empty from the user
     */
    public boolean checkFieldsValidation(UserDTO userDTO) {
        String userFirstName = userDTO.getFirstName();
        String userLastName = userDTO.getLastName();
        String userEmail = userDTO.getEmail();
        String userPassword = userDTO.getPassword();
        return userFirstName == null || userFirstName.isEmpty() ||
                userLastName == null || userLastName.isEmpty() ||
                userEmail == null || userEmail.isEmpty() ||
                userPassword == null || userPassword.isEmpty();
    }

    /*
    Finding all users from the database.
     */
    public List<User> findAllUsersHandler() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users not found");
        }
        return users;
    }

    // Check the current user email, if it doesn't exist, respond with 404,
    // If any field is empty respond  with 401, shouldn't be authorized.
    // Compare user's rawPassword with registered password.
    public void validateLogin(LoginRequestDTO loginRequestDTO) {
        String userEmail = loginRequestDTO.getEmail();
        String rawPassword = loginRequestDTO.getPassword();

        User currentUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFound("This email doesnt exists"));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String userPassword = currentUser.getPassword();
        if (!encoder.matches(rawPassword, userPassword)) {
            throw new InvalidCredentials("Wrong password");
        }
        if (userEmail.isEmpty() || rawPassword.isEmpty()) {
            throw new InvalidCredentials("Required fields are missing");
        }
    }

    // Validate user when try to escape fields.
    // Check email and password validation
    // If email is using already, send invalid email.
    public void validateUser(UserDTO userDTO) {
        String userEmail = userDTO.getEmail();
        String userPassword = userDTO.getPassword();

        if (checkFieldsValidation(userDTO)) {
            throw new Validation("Required fields are missing");
        } else if (userEmailNotValid(userEmail)) {
            throw new Validation("Invalid email address");
        } else if (passwordNotValid(userPassword)) {
            throw new Validation("Invalid password");
        } else if (userRepository.existsByEmail(userEmail)) {
            throw new Validation("Email already have been using.");
        }
    }

}
