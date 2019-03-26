package com.henglong.cloud.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class StaticService {

    private static final Logger log = LoggerFactory.getLogger(StaticService.class);

    private final AssetsService assetsService;

    @Autowired
    public StaticService(AssetsService assetsService) {
        this.assetsService = assetsService;
    }

    public void Static(){
        try {
            assetsService.AssetsUpdate();
        } catch (Exception e1){
            log.error("未知错误",e1);
        }
    }
}
