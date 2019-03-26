package com.henglong.cloud.dao.other;

import com.henglong.cloud.entity.other.AssetsDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface AssetsDateDao extends JpaRepository<AssetsDate,Integer> {

    Page<AssetsDate> findByUserId(String userId, Pageable p);

    Page<AssetsDate> findAll(Specification<AssetsDate> var1, Pageable p);

    Long count(Specification<AssetsDate> var1);

    Long countByUserId(String userId);

    AssetsDate findByUserIdAndTime(String userId, Date time);

}
