package com.example.my_app.Service;


import com.example.my_app.Controller.UserController;
import com.example.my_app.DTO.*;
import com.example.my_app.Handler.*;
import com.example.my_app.Mapper.UserMapper;
import com.example.my_app.Model.Role;
import com.example.my_app.Model.User;
import com.example.my_app.Model.UserRoles;
import com.example.my_app.Repository.RoleRepository;
import com.example.my_app.Repository.UserRepository;
import com.example.my_app.Security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserService {

    private final UserHandler userHandler;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserService(UserHandler userHandler,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userHandler = userHandler;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;

    }

    /*
    Validate user fields from request, convert dto to entity and encode the password of user.
    Get the role of the user, check if its given correct role and set role and save the db or return invalid role.
    Save user the db if everything is ok and out.
     */
    public void registerUser(UserDTO registerUserDTO) {
        userHandler.validateUser(registerUserDTO);
        User newUser = UserMapper.RegisterUserDTOToEntity(registerUserDTO);
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(UserRoles.USER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(UserRoles.USER);
                    return roleRepository.save(newRole);
                });
        roles.add(userRole);

        newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        newUser.setRoles(roles);
        try {
            userRepository.save(newUser);
        } catch (Exception e) {
            throw new DatabaseOperationException("User could not be saved");
        }

        UserMapper.EntityToUserDTO(newUser);
    }

    /*
    Get all users with name and password
    It only available for admin
     */

    @PreAuthorize("hasAuthority('GET_ALL_USERS')")
    public List<UserEntityDTO> getAllUsers() {
        List<User> users = userHandler.findAllUsersHandler();
        List<UserEntityDTO> userEntityDTOS = new ArrayList<>();
        for (User findUser : users) {
            UserEntityDTO userEntityDTO = UserMapper.EntityToUserDTO(findUser);
            userEntityDTOS.add(userEntityDTO);
        }
        return userEntityDTOS;
    }

    /*  Users will be paginated by 5, 10, 20.
        offset implements if page is going to next page and skip first seen users according the size of users

        @param page - Page number
        @param size - Size of the page
     */

    @PreAuthorize("hasAuthority('GET_ALL_USERS')")
    public List<UserEntityDTO> getAllUsersWithPagination(int page, int size) {
        int offset = (page - 1) * size;
        List<User> users = userRepository.findUsersWithPagination(size, offset);
        return users.stream().map(UserMapper::EntityToUserDTO).collect(Collectors.toList());
    }

    /*

        Search user by text and calculate offset based on  page and size
        Count total users by search results
        @Param search - searched text by user
        @Param page - which page to go
        @Param size - size of the page
    */
    public Map<String, Object> searchUsersByText(String search, int page, int size) {
        int offset = (page - 1) * size;
        List<User> users = userRepository.searchUserByText(search, size, offset);
        int totalUsers = userRepository.countSearchResults(search);

        Map<String, Object> response = new HashMap<>();
        response.put("users", users.stream().map(UserMapper::EntityToUserDTO).collect(Collectors.toList()));
        response.put("currentPage", page);
        response.put("totalUsers", totalUsers);
        response.put("totalPages", (int) Math.ceil((double) totalUsers / size));
        response.put("totalElements", totalUsers);

        return response;
    }

    // This security context contains information about the currently authenticated user.
    // setAuthentication stores the auth object available throughout the application
    public String loginCredentials(LoginRequestDTO loginRequestDTO) {
        userHandler.validateLogin(loginRequestDTO);
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDTO.getEmail(),
                                loginRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    /*
        check specific user by id

        @param id - user id
        @throws UserNotFound exception when retrieving user
    */
    @PreAuthorize("hasAuthority('GET_SPECIFIC_USER')")
    public UserEntityDTO getUserByID(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound("User not found"));
        return UserMapper.EntityToUserDTO(user);
    }

    /*
        check specific user by email

        @param email - user email
        @throws UserNotFound exception when retrieving user
     */
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public UserEntityDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFound("User not found"));
        return UserMapper.EntityToUserDTO(user);
    }


    /*
        Delete user with corresponding id
        It only available for admins.

        @param id - user id
        @throws UserNotFound exception when retrieving user
     */
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public void deleteUserByID(UUID id) {
        boolean user = userRepository.existsById(id);
        if (!user) {
            throw new UserNotFound("User not found");
        }
        userRepository.deleteById(id);
    }

    /*
        Updates user's all fields with 'put'
        Making validation if there is an issue in fields before save

        @param id - user id
        @param userDTO - user fields
        @throws Validation exception when fields are not matching.
     */
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public UpdatedUserDTO updateUser(UUID id, UserDTO userDTO) {
        User findUser = userRepository.findById(id).
                map(user -> {
                    if (userHandler.userEmailNotValid(userDTO.getEmail())) {
                        throw new Validation("Invalid email");
                    } else if (userHandler.passwordNotValid(userDTO.getPassword())) {
                        throw new Validation("Invalid password");
                    } else if (!userHandler.checkFieldsValidation(userDTO)) {
                        throw new Validation("Invalid fields");
                    }
                    user.setUserFirstName(userDTO.getFirstName());
                    user.setUserLastName(userDTO.getLastName());
                    user.setEmail(userDTO.getEmail());
                    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

                    return userRepository.save(user);
                }).orElseThrow(() -> new UserNotFound("User not found"));
        return UserMapper.EntityToUpdatedUserDTO(findUser);
    }


    /*
     Update user's some fields according to patch request
     Reduce load in the db, instead of setting other fields.
     Making validation if there is an issue before save.

     @param id - user id
     @param updates - requested update fields.
     @throws UserNotFound exception when retrieving user
     @throws Validation exception when fields are not matching
     */
    public ResponseEntity<UpdatedUserDTO> updateUserFields(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {

        User findUser = userRepository.findById(id).orElseThrow(() -> new UserNotFound("User not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    findUser.setUserFirstName((String) value);
                    break;
                case "lastName":
                    findUser.setUserLastName((String) value);
                    break;
                case "email":
                    if (userHandler.userEmailNotValid((String) value)) {
                        throw new Validation("Invalid email, please give a valid email");
                    }
                    findUser.setEmail((String) value);
                    break;
                case "password":
                    if (userHandler.passwordNotValid((String) value)) {
                        throw new Validation("Invalid password, password length should be greater than six letter.");
                    }
                    findUser.setPassword(passwordEncoder.encode((String) value));
                    break;
                default:
                    logger.warn("Unexpected field in update request: {}", key);
            }
        });

        User updatedUser = userRepository.save(findUser);
        UpdatedUserDTO result = UserMapper.EntityToUpdatedUserDTO(updatedUser);

        return ResponseEntity.ok(result);
    }


}