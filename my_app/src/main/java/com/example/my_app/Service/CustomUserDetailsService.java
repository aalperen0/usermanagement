package com.example.my_app.Service;

import com.example.my_app.Handler.InvalidCredentials;
import com.example.my_app.Handler.UserNotFound;
import com.example.my_app.Model.Authority;
import com.example.my_app.Model.User;
import com.example.my_app.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    during the authentication process loads the data based on the provided email.
    it determines whether the user should be granted to access particular resource.
     */
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UserNotFound {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new InvalidCredentials("Invalid credentials"));


        Set<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .flatMap(role -> role.getAuthorities().stream())
                .map(authority -> new SimpleGrantedAuthority(authority.getName().name()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

}
