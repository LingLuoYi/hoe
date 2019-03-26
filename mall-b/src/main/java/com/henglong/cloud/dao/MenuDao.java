package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuDao extends JpaRepository<Menu, Integer> {

    Menu findByTitle(String title);
}
