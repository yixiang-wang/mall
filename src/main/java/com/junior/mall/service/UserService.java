package com.junior.mall.service;

import com.junior.mall.model.pojo.User;
import com.junior.mall.service.exception.UserException;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    @Transactional(rollbackFor = Exception.class)
    void register(String username, String password, String email) throws UserException;

    public User login(String username, String password) throws UserException;

    public void update(User user, String signature) throws UserException;

    void checkEmail(String email) throws UserException;
}
