package com.hoe.dao;

import com.hoe.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolesDao extends JpaRepository<Roles,Integer> {
    List<Roles> findByUserId(String userId);
}
