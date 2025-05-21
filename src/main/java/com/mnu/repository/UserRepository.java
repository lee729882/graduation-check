package com.mnu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnu.entity.User;



public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameAndEmail(String username, String email);

}