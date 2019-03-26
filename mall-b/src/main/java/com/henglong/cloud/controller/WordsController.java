package com.henglong.cloud.controller;

import com.henglong.cloud.entity.Words;
import com.henglong.cloud.service.WordsServer;
import com.henglong.cloud.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/words")
@CrossOrigin(allowCredentials="true")
public class WordsController {

    private final WordsServer wordsServer;

    @Autowired
    public WordsController(WordsServer wordsServer) {
        this.wordsServer = wordsServer;
    }

    @RequestMapping("/words_add")
    public Json wordsAdd(Words words){
        return wordsServer.wordsAdd(words);
    }

    @RequestMapping("/words_update")
    public Json wordsUpdate(Words words){
        return wordsServer.wordsUpdate(words);
    }

    @RequestMapping("/words_delete")
    public Json wordsDelete(Integer id){
        return wordsServer.wordsDelete(id);
    }

    @RequestMapping("/words_all")
    public Json wordsAll(){
        return wordsServer.wordsAll();
    }

    @RequestMapping("/words_page")
    public Json wordsPage(Integer index,Integer size){
        return wordsServer.wordsAllPage(index,size);
    }

    @RequestMapping("/words_purpose")
    public Json wordsPurpose(String purpose){
        return wordsServer.wordsPurpose(purpose);
    }

    @RequestMapping("/words_title")
    public Json wordsTitle(String title){
        return wordsServer.wordsTitle(title);
    }

    @RequestMapping("/words_classify")
    public Json wordsClassify(String classify){
        return wordsServer.wordsClassfiy(classify);
    }
}
