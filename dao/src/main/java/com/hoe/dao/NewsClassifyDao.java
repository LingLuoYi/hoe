package com.hoe.dao;

import com.hoe.entity.NewsClassify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsClassifyDao extends JpaRepository<NewsClassify,Integer> {
}
