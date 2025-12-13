package com.backend.Kata.repository;

import com.backend.Kata.entities.Role;
import com.backend.Kata.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findFirstByEmail(String email);

    Optional<User> findByRole(Role role);
}
