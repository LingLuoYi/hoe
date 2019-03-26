package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Pay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayDao extends JpaRepository<Pay,Integer> {

    List<Pay> findByPayUserId(String phone);

    Optional<Pay> findByPayId(String id);

    Pay findByPayOrderId(String orderId);

    List<Pay> findByVoucherState(String vs);

    List<Pay> findByPayState(String s);

    Pay findByPayIdAndPayUserId(String id,String userId);

    Pay findByPayOrderIdAndPayUserId(String id ,String userId);

    Page<Pay> findAll(Pageable pageable);

    Page<Pay> findByPayState(String state,Pageable pageable);

    Page<Pay> findByVoucherState(String vs,Pageable pageable);

    Long countByPayState(String state);

    Long countByVoucherState(String vs);
}
