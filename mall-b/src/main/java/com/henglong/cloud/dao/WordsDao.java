package com.henglong.cloud.dao;

import com.henglong.cloud.entity.Words;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordsDao extends JpaRepository<Words,Integer> {

    List<Words> findByPurpose(String p);

    Words findByTitle(String t);

    List<Words> findByClassify(String c);
}
