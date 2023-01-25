package com.junior.mall.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class WebLogAspect {

    private final Logger log = LoggerFactory.getLogger(WebLogAspect.class);

    //设置Pointcut及作用范围
    @Pointcut("execution(public * com.junior.mall.controller.*.*(..))")
    public void webLog() {
    }

    /**
     * @param joinPoint 用于获取Args
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("request url:" + request.getRequestURL().toString());
        log.info("request method:" + request.getMethod());
        log.info("args : " + Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * @param res 返回值
     * @throws JsonProcessingException ObjectMapper工具方法抛出异常
     */
    @AfterReturning(returning = "res", pointcut = "webLog()")
    public void doAfterReturning(Object res) throws JsonProcessingException {
        log.info("return response : " + new ObjectMapper().writeValueAsString(res));
    }

}
