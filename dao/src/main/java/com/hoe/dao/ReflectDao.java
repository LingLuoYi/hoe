package com.hoe.dao;

import com.hoe.entity.Reflect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReflectDao extends JpaRepository<Reflect,Integer> {

    List<Reflect> findByUserId(String userId);

    Page<Reflect> findByUserId(String userId, Pageable p);//分页查询

    Page<Reflect> findByUserIdAndState(String userId, String state, Pageable p);

    Page<Reflect> findByState(String state, Pageable pageable);

    Page<Reflect> findAll(Pageable pageable);

    List<Reflect> findByAssetsId(String assetsId);

    Long countByState(String state);

    Long countByUserId(String userId);

    Long countByUserIdAndState(String userId, String state);
}
