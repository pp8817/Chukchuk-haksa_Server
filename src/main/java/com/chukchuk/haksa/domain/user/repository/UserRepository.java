package com.chukchuk.haksa.domain.user.repository;

import com.chukchuk.haksa.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
 * Retrieves a User entity that matches the specified email address.
 *
 * @param email the email address of the user to retrieve
 * @return an Optional containing the User if found, or an empty Optional otherwise
 */
Optional<User> findByEmail(String email);
}
