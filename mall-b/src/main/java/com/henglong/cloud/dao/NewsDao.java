package com.henglong.cloud.dao;

import com.henglong.cloud.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsDao extends JpaRepository<News,Integer> {
}
