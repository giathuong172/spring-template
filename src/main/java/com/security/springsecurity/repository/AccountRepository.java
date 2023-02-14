package com.security.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.security.springsecurity.entity.Account;
@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    public Account findByUsername(String username);
}
