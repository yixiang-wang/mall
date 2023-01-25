package com.junior.mall.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);

    boolean saveEmailToRedis(String email, String code);

    boolean checkEmailCode(String email, String code);
}
