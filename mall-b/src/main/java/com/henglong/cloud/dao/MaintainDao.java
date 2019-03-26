package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Maintain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaintainDao extends JpaRepository<Maintain,Integer> {

    Optional<Maintain> findByMaintainId(String maintainId);

    Optional<Maintain> findByUserIdAndMaintainId(String userId,String maintainId);

    List<Maintain> findByUserIdAndAssetsId(String userId, String assetsId);

    List<Maintain> findByAssetsId(String assetsId);

    Page<Maintain> findByUserIdAndAssetsIdAndState(String userId, String assetsId, Integer start, Pageable p);

    Page<Maintain> findByUserId(String userId,Pageable p);

    Page<Maintain> findByUserIdAndState(String userId,Integer state, Pageable p);

    Page<Maintain> findAll(Pageable p);

    Page<Maintain> findByState(Integer state,Pageable p);

    Long countByUserIdAndState(String userId,Integer state);

    Long countByUserId(String userId);

}
