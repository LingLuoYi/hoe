package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDao extends JpaRepository<Image,Integer> {

    Image findByName(String name);
}
