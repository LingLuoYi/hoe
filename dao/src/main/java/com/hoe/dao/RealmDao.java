package com.hoe.dao;

import com.hoe.entity.Realm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RealmDao extends JpaRepository<Realm,Integer> {
    List<Realm> findByRoles(String roles);
}
