package com.junior.mall.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.junior.mall.exception.MallExceptionEnum;
import com.junior.mall.model.pojo.User;
import com.junior.mall.service.EmailService;
import com.junior.mall.service.UserService;
import com.junior.mall.service.exception.UserException;
import com.junior.mall.utils.Constant;
import com.junior.mall.utils.EmailUtils;
import com.junior.mall.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseUtils register(String username, String password, String emailAddress, String verificationCode) throws UserException {
        if (!StringUtils.hasLength(username)) {
            return ResponseUtils.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (!StringUtils.hasLength(password)) {
            return ResponseUtils.error(MallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ResponseUtils.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        if (!StringUtils.hasLength(emailAddress)) {
            return ResponseUtils.error(MallExceptionEnum.NEED_EMAIL_ADDRESS);
        }
        if (!StringUtils.hasLength(verificationCode)) {
            return ResponseUtils.error(MallExceptionEnum.NEED_VERIFICATION_CODE);
        }
        userService.checkEmail(emailAddress);
        boolean checkEmailCode = emailService.checkEmailCode(emailAddress, verificationCode);
        if (!checkEmailCode) {
            return ResponseUtils.error(MallExceptionEnum.WRONG_CODE);
        }
        userService.register(username, password, emailAddress);
        return ResponseUtils.success();
    }

    @PostMapping("/login")
    public ResponseUtils login(String username, String password) throws UserException {
        if (!StringUtils.hasLength(username)) {
            return ResponseUtils.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (!StringUtils.hasLength(password)) {
            return ResponseUtils.error(MallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ResponseUtils.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        User user = userService.login(username, password);
        //创建token
        Algorithm algorithm=Algorithm.HMAC256(Constant.JWT_KEY);
        String token=JWT.create().withClaim(Constant.USER_ID,user.getId()).withClaim(Constant.USER_NAME,user.getUsername()).withClaim(Constant.USER_ROLE,user.getRole()).withExpiresAt(new Date(System.currentTimeMillis()+Constant.EXPIRE_TIME)).sign(algorithm);
        return ResponseUtils.success(token);
    }

    @PostMapping("/user/update")
    public ResponseUtils update(String signature, HttpServletRequest request) throws UserException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResponseUtils.error(MallExceptionEnum.NEED_LOGIN);
        }
        userService.update(user, signature);
        return ResponseUtils.success();
    }

    @PostMapping("/user/logout")
    public ResponseUtils logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return ResponseUtils.success();
    }

    @PostMapping("/adminlogin")
    public ResponseUtils adminLogin(String username, String password) throws UserException {
        if (!StringUtils.hasLength(username)) {
            return ResponseUtils.error(MallExceptionEnum.NEED_USER_NAME);
        }
        if (!StringUtils.hasLength(password)) {
            return ResponseUtils.error(MallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ResponseUtils.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        User adminUser = userService.login(username, password);
        if (adminUser.getRole() != 2) {
            return ResponseUtils.error(MallExceptionEnum.NEED_ADMIN);
        }
        //创建token
        Algorithm algorithm=Algorithm.HMAC256(Constant.JWT_KEY);
        String token=JWT.create().withClaim(Constant.USER_ID,adminUser.getId()).withClaim(Constant.USER_NAME,adminUser.getUsername()).withClaim(Constant.USER_ROLE,adminUser.getRole()).withExpiresAt(new Date(System.currentTimeMillis()+Constant.EXPIRE_TIME)).sign(algorithm);
        return ResponseUtils.success(token);
    }

    @PostMapping("/sendmail")
    public ResponseUtils sendMail(String email) throws UserException {
        if (EmailUtils.validEmailAddress(email)) {
            userService.checkEmail(email);
            String code = EmailUtils.genEmailValidCode();
            boolean exist = emailService.saveEmailToRedis(email, code);
            if (!exist) {
                return ResponseUtils.error(MallExceptionEnum.EMAIL_ALREADY_SEND);
            }
            emailService.sendEmail(email, "验证码", "您的验证码为" + code);
        } else {
            return ResponseUtils.error(MallExceptionEnum.EMAIL_ADDRESS_ILLOGICAL);
        }
        return ResponseUtils.success();
    }


}
