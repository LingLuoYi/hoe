package com.hoe.dao;

import com.hoe.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDao extends JpaRepository<Image,Integer> {

    Image findByName(String name);
}
