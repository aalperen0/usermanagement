package com.example.my_app.Repository;

import com.example.my_app.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // check if users exists by email
    boolean existsByEmail(String email);

    // check user by email
    Optional<User> findByEmail(String email);

    /*
    Pagination for the users
    @Param limit - how many we should display
    @Param offset - how many user we should skip when next page
     */
    @Query(value = "SELECT * FROM user_table LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<User> findUsersWithPagination(@Param("limit") int limit, @Param("offset") int offset);

    /*
    Searching users by text
    @Param search - search user by name, email, lastname and change input to lowercase for more concise result.
    @Param limit - when we select page size, how many user we should display.
    @Param offset - when we select page size, how many user we should skip.
     */
    @Query(value = "SELECT * FROM user_table WHERE " +
            "LOWER(firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CONCAT(firstname , ' ' , lastname)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(email) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<User> searchUserByText(@Param("search") String search, @Param("limit") int limit, @Param("offset") int offset);

    /*
    How many users we get from search
    @Param search - search users
     */
    @Query(value = "SELECT COUNT(*) FROM user_table  WHERE " +
            "LOWER(firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CONCAT(firstname , ' ' , lastname)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(email) LIKE LOWER(CONCAT('%', :search, '%'))",
            nativeQuery = true)
    int countSearchResults(@Param("search") String search);
}




