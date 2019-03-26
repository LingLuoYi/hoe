package com.hoe.dao;

import com.hoe.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsDao extends JpaRepository<News,Integer> {
}
