package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Realm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RealmDao extends JpaRepository<Realm,Integer> {
    List<Realm> findByRoles(String roles);
}
