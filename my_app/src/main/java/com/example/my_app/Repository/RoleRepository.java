package com.example.my_app.Repository;

import com.example.my_app.Model.Role;
import com.example.my_app.Model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    // finding by role
    Optional<Role> findByName(UserRoles roles);

}
