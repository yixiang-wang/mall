package com.junior.mall.service.impl;

import com.junior.mall.service.EmailService;
import com.junior.mall.utils.Constant;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String text){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(Constant.EMAIL_FROM);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        mailSender.send(simpleMailMessage);
    }

    @Override
    public boolean saveEmailToRedis(String email, String code){
        RedissonClient redissonClient= Redisson.create();
        //根据主键获取值
        RBucket<String> bucket=redissonClient.getBucket(email);
        RBucket<String> bucketCheck=redissonClient.getBucket(email+"check");
        boolean exist=bucket.isExists();
        if(!exist){
            bucket.set(code,60L, TimeUnit.SECONDS);
            bucketCheck.set(code,5L,TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkEmailCode(String email,String code){
        RedissonClient redissonClient= Redisson.create();
        RBucket<String> bucketCheck=redissonClient.getBucket(email+"check");
        boolean exist=bucketCheck.isExists();
        if(exist){
            if(code.equals(bucketCheck.get())){
                return true;
            }
        }
        return false;
    }
}
