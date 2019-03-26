package com.hoe.dao;

import com.hoe.entity.PutForward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PutForwardDao extends JpaRepository<PutForward,Integer>{

    PutForward findByAssetsPayId(String id);
}
