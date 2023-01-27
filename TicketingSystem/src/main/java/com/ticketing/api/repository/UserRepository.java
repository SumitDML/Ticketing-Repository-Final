package com.ticketing.api.repository;

import com.ticketing.api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);
    boolean existsByEmail(String userId);


}
