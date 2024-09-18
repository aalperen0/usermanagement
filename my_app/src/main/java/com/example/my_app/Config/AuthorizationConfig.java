package com.example.my_app.Config;

import com.example.my_app.Service.RoleAuthorityService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationConfig {

    private final RoleAuthorityService roleAuthorityService;

    public AuthorizationConfig(RoleAuthorityService roleAuthorityService) {
        this.roleAuthorityService = roleAuthorityService;
    }

    @PostConstruct
    private void setUp(){
        roleAuthorityService.initializeRoleAuthorities();
    }
}
