package com.hoe.dao;

import com.hoe.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserDao extends JpaRepository<User,Integer> {
    User findByPhone(String phone);

    User findByEmail(String email);

    User findByUserId(String userid);

    Page<User> findAll(Pageable p);

    Page<User> findByUserStart(Integer s, Pageable pageable);
}
