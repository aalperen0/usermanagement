package com.example.my_app.Controller;


import com.example.my_app.DTO.*;
import com.example.my_app.Mapper.UserMapper;
import com.example.my_app.Model.User;
import com.example.my_app.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO registerUserDTO) {
        userService.registerUser(registerUserDTO);
        return ResponseEntity.ok().body("SUCCESSFULLY REGISTERED");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String jwt = userService.loginCredentials(loginRequestDTO);
        return ResponseEntity.ok(jwt);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserEntityDTO>> findAllUsers(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size) {
        List<UserEntityDTO> allUsers = userService.getAllUsersWithPagination(page, size);
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<Map<String,Object>> searchUsers(
            @RequestParam("query") String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        Map<String,Object> response = userService.searchUsersByText(search, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable UUID id) {
        UserEntityDTO user = userService.getUserByID(id);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<UserEntityDTO> getUserByEmail(@PathVariable String email) {
        UserEntityDTO user = userService.getUserByEmail(email);
        System.out.println(user);
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUserByID(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UpdatedUserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO user) {
        UpdatedUserDTO updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok().body(updatedUser);
    }

    @PatchMapping("/users/patch/{id}")
    public ResponseEntity<Map<String, Object>> updateUserFields(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        userService.updateUserFields(id, updates);
        return ResponseEntity.ok().body(updates);
    }

    /*
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    private ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        UserDTO createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
    */


}
