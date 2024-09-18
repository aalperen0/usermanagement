package com.example.my_app.Service;

import com.example.my_app.Model.Authority;
import com.example.my_app.Model.Role;
import com.example.my_app.Model.UserAuthorities;
import com.example.my_app.Model.UserRoles;
import com.example.my_app.Repository.AuthorityRepository;
import com.example.my_app.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class RoleAuthorityService {
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public RoleAuthorityService(RoleRepository roleRepository, AuthorityRepository authorityRepository) {
        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public void initializeRoleAuthorities() {
        // Fetch roles
        Role userRole = roleRepository.findByName(UserRoles.USER).orElseThrow(() -> new RuntimeException("USER ROLE NOT FOUND"));
        Role adminRole = roleRepository.findByName(UserRoles.ADMIN).orElseThrow(() -> new RuntimeException("ADMIN ROLE NOT FOUND"));

        // Fetch all authorities
        Authority updateUser = authorityRepository.findByName(UserAuthorities.UPDATE_USER)
                .orElseThrow(() -> new RuntimeException("UPDATE_USER authority not found"));
        Authority deleteUser = authorityRepository.findByName(UserAuthorities.DELETE_USER)
                .orElseThrow(() -> new RuntimeException("DELETE_USER authority not found"));
        Authority getAllUsers = authorityRepository.findByName(UserAuthorities.GET_ALL_USERS)
                .orElseThrow(() -> new RuntimeException("GET_ALL_USERS authority not found"));
        Authority getSpecificUser = authorityRepository.findByName(UserAuthorities.GET_SPECIFIC_USER)
                .orElseThrow(() -> new RuntimeException("GET_SPECIFIC_USER authority not found"));

        userRole.getAuthorities().clear();
        adminRole.getAuthorities().clear();

        Set<Authority> expectedUserAuthorities = new HashSet<>(Arrays.asList(updateUser));
        if (userRole.getAuthorities().containsAll(expectedUserAuthorities)) {
            System.out.println("USER ALREADY HAVE BEEN THIS AUTHORITIES");
        } else {
            userRole.setAuthorities(expectedUserAuthorities);
            roleRepository.save(userRole);
        }

        Set<Authority> expectedAdminAuthorities = new HashSet<>(Arrays.asList(updateUser, deleteUser, getSpecificUser, getAllUsers));
        if (adminRole.getAuthorities().containsAll(expectedAdminAuthorities)) {
            System.out.println("ADMIN ALREADY HAVE BEEN THIS AUTHORITIES");
        } else {
            adminRole.setAuthorities(expectedAdminAuthorities);
            roleRepository.save(adminRole);
        }
    }
}
