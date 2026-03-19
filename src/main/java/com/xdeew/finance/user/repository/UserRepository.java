package com.xdeew.finance.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xdeew.finance.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}