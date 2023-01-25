package com.junior.mall.service.impl;

import com.junior.mall.exception.MallExceptionEnum;
import com.junior.mall.model.dao.UserMapper;
import com.junior.mall.model.pojo.User;
import com.junior.mall.service.UserService;
import com.junior.mall.service.exception.UserException;
import com.junior.mall.utils.Md5Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String username, String password,String email) throws UserException {
        User user = userMapper.selectByUserName(username);
        if (user != null) {
            throw new UserException(MallExceptionEnum.NAME_EXISTED);
        }
        user = new User();
        password = Md5Utils.md5Digest(password, 1000);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmailAddress(email);
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new UserException(MallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String username, String password) throws UserException {
        User user = userMapper.selectByUserName(username);
        if (user == null) {
            throw new UserException(MallExceptionEnum.NAME_NOT_EXISTED);
        }
        if (!Md5Utils.md5Digest(password, 1000).equals(user.getPassword())) {
            throw new UserException(MallExceptionEnum.WRONG_PASSWORD);
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void update(User user, String signature) throws UserException {
        User userNew = new User();
        userNew.setId(user.getId());
        userNew.setPersonalizedSignature(signature);
        int count = userMapper.updateByPrimaryKeySelective(userNew);
        if (count != 1) {
            throw new UserException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void checkEmail(String email) throws UserException {
        User user=userMapper.selectOneByEmailAddress(email);
        if(user!=null){
            throw new UserException(MallExceptionEnum.EMAIL_ADDRESS_EXISTED);
        }
    }
}
