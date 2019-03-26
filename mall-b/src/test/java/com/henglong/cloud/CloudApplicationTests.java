package com.henglong.cloud;

import com.henglong.cloud.service.CommodityService;
import com.henglong.cloud.util.FileConfig;
import com.henglong.cloud.util.LoadFile;
import com.henglong.cloud.util.MessageUtils;
import com.henglong.cloud.util.Time;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableScheduling
public class CloudApplicationTests {

	Logger log = LoggerFactory.getLogger(CloudApplicationTests.class);

	@Autowired
	CommodityService commodityService;

	@Test
	public void contextLoads() throws Exception {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		log.info("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"+time.belongDate(new Date(),new Date(sdf.parse("2018-08-30 13:54:36").getTerm()),61));
//		commodityService.OrderTime("20180828104119000001564910391");
//		commodityService.OrderTime2();
//		log.info(FileConfig.OutputPath("encryption-salt","10"));
	}

	@Test
	public void us(){
		log.info("{}", MessageUtils.get("user.welcome"));
	}

}
