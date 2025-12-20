package com.niam.kardan.repository;

import com.niam.kardan.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUser_id(Long userId);
    boolean existsByUser_username(String username);
}