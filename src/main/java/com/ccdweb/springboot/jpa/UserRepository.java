package com.ccdweb.springboot.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
	Optional<UserEntity> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
