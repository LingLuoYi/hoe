package com.hoe.dao;

import com.hoe.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuDao extends JpaRepository<Menu, Integer> {

    Menu findByTitle(String title);
}
