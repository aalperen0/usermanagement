package com.example.my_app.Repository;

import com.example.my_app.Model.Authority;
import com.example.my_app.Model.UserAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    // finding user's authorities
    Optional<Authority> findByName(UserAuthorities userAuthorities);
}
