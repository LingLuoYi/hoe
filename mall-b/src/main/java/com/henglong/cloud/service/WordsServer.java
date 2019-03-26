package com.henglong.cloud.service;

import com.henglong.cloud.dao.WordsDao;
import com.henglong.cloud.entity.Words;
import com.henglong.cloud.util.API;
import com.henglong.cloud.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WordsServer {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(WordsServer.class);

    private final WordsDao wordsDao;


    @Autowired
    public WordsServer(WordsDao wordsDao) {
        this.wordsDao = wordsDao;
    }

    //添加加文字
    public Json wordsAdd(Words words){
        if (words.getTitle() == null || "".equals(words.getTitle()))
            return API.error("标题不能为空");
        if (words.getPurpose() == null || "".equals(words.getPurpose()))
            return API.error("用途不能为空");
        if (words.getContent() == null || "".equals(words.getContent()))
            return API.error("内容不能为空");
        wordsDao.save(words);
        return API.Success(words);
    }

    //修改文字
    public Json wordsUpdate(Words words){
        if (words.getId() == null || 0 == words.getId())
            return API.error("请输入id");
        Optional<Words> optionalWords = wordsDao.findById(words.getId());
        if (!optionalWords.isPresent())
            return API.error("没有要修改的文字");
        Words words1 = optionalWords.get();
        if (words.getTitle() != null && !"".equals(words.getTitle()))
            words1.setTitle(words.getTitle());
        if (words.getClassify() != null && !"".equals(words.getClassify()))
            words1.setClassify(words.getClassify());
        if (words.getPurpose() != null && !"".equals(words.getPurpose()))
            words1.setPurpose(words.getPurpose());
        if (words.getContent() != null && !"".equals(words.getContent()))
            words1.setContent(words.getContent());
        wordsDao.save(words1);
        return API.Success(words1);
    }

    //删除文字
    public Json wordsDelete(Integer id){
        Optional<Words> optionalWords = wordsDao.findById(id);
        if (!optionalWords.isPresent())
            return API.error("没有要删除的文字");
        wordsDao.delete(optionalWords.get());
        return API.Success(optionalWords.get());
    }

    //查询全部
    public Json wordsAll(){
        return API.Success(wordsDao.findAll());
    }

    //查询全部，分页
    public Json wordsAllPage(Integer index,Integer size){
        if (size == null || 0 == size)
            size =10;
        Pageable pageable = PageRequest.of(index,size);
        Page<Words> wordsList = wordsDao.findAll(pageable);
        List<Words> list = new ArrayList<>();
        for (Words words: wordsList){
            list.add(words);
        }
        return API.Success(list);
    }

    //根据用途查询
    public Json wordsPurpose(String p){
        return API.Success(wordsDao.findByPurpose(p));
    }

    //根据标题查询
    public Json wordsTitle(String title){
        return API.Success(wordsDao.findByTitle(title));
    }

    //根据分类查询
    public Json wordsClassfiy(String c){
        return API.Success(wordsDao.findByClassify(c));
    }

}
