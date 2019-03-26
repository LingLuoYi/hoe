package com.henglong.cloud.dao;

import com.henglong.cloud.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDao extends JpaRepository<User,Integer> {
    User findByPhone(String phone);

    User findByEmail(String email);

    User findByUserId(String userid);

    Page<User> findAll(Pageable p);

    Page<User> findByUserStart(Integer s,Pageable pageable);
}
