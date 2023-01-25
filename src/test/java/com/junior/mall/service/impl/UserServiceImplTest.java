package com.junior.mall.service.impl;

import com.junior.mall.service.UserService;
import com.junior.mall.service.exception.UserException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @Test
    void register() throws UserException {
        userService.register("123456aaa","12345678","553311@qq.com");
    }
}