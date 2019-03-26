package com.henglong.cloud.service;

import com.henglong.cloud.util.LoadFile;
import com.henglong.cloud.util.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    private static final String modeurl = LoadFile.Path()+"/mode/";

    @Value("${spring.mail.username}")
    private String Sender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void mail(String email, String s,String title) {
            MimeMessage message = null;
            try {
                message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(Sender);
                helper.setTo(email);
                helper.setSubject(title);

                helper.setText(s, true);
            } catch (Exception e) {
                log.error("邮件发送失败{}",e);
            }
            mailSender.send(message);
    }

}
