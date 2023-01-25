package com.junior.mall.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.junior.mall.exception.MallExceptionEnum;
import com.junior.mall.model.pojo.User;
import com.junior.mall.service.exception.UserException;
import com.junior.mall.utils.Constant;
import com.junior.mall.utils.ResponseUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UserFilter implements Filter {
    public static User user;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token=request.getHeader(Constant.JWT_TOKEN);
        if (token == null) {
            PrintWriter out = new ServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write(ResponseUtils.error(MallExceptionEnum.NEED_LOGIN).toJsonString());
            out.flush();
            out.close();
            return;
        }
        Algorithm algorithm=Algorithm.HMAC256(Constant.JWT_KEY);
        //开启核验器
        JWTVerifier verifier= JWT.require(algorithm).build();
        try {
            DecodedJWT jwt=verifier.verify(token);
            user=new User();
            user.setId(jwt.getClaim(Constant.USER_ID).asInt());
            user.setUsername(jwt.getClaim(Constant.USER_NAME).asString());
            user.setRole(jwt.getClaim(Constant.USER_ROLE).asInt());
        } catch (TokenExpiredException e){
            //编码过期异常
            PrintWriter out = new ServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write(ResponseUtils.error(MallExceptionEnum.TOKEN_EXPIRED).toJsonString());
            out.flush();
            out.close();
            return;
        } catch (JWTVerificationException e) {
            //解码失败异常
            PrintWriter out = new ServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write(ResponseUtils.error(MallExceptionEnum.TOKEN_WRONG).toJsonString());
            out.flush();
            out.close();
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
