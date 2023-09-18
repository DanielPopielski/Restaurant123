package com.restaurant.Repository;

import com.restaurant.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String usernameNick);
    Optional<UserEntity> findByRole(String role);
}