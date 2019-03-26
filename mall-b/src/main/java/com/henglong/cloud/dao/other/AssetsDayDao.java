package com.henglong.cloud.dao.other;

import com.henglong.cloud.entity.other.AssetsDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface AssetsDayDao extends JpaRepository<AssetsDay,Integer> {

    Page<AssetsDay> findByUserIdAndTime(String userId, Date time,Pageable p);

    Page<AssetsDay> findByUserId(String userId,Pageable p);

    List<AssetsDay> findByAssetsId(String assetsId);

    Long countByUserIdAndTime(String userId,Date times);

    Long countByUserId(String userId);

    AssetsDay findByUserIdAndTimeAndAssetsId(String userId,Date time,String assetsId);

    AssetsDay findByTimeAndAssetsId(Date time,String assetsId);


}
