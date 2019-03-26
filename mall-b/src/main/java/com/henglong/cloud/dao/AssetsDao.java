package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Assets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetsDao extends JpaRepository<Assets,Integer> {

    List<Assets> findByAssetsUserId(String userId);

    Assets findByAssetsPayId(String id);

    Assets findByAssetsPayIdAndAssetsUserId(String id,String userId);

    Page<Assets> findByAssetsUserId(String userId,Pageable p);

    Page<Assets> findByAssetsUserIdAndAssetsState(String userId,String state,Pageable p);

    Page<Assets> findAll(Pageable pageable);

    Page<Assets> findByAssetsPayId(String assetsId,Pageable pageable);

    Long countByAssetsUserId(String userId);

    Long countByAssetsUserIdAndAssetsState(String userId,String state);

}
