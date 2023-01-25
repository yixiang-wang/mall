package com.junior.mall.utils;

import com.junior.mall.exception.MallExceptionEnum;
import com.junior.mall.model.pojo.Category;
import com.junior.mall.model.pojo.Product;
import com.junior.mall.service.exception.CategoryException;
import com.junior.mall.service.exception.ProductException;
import com.junior.mall.service.exception.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseUtils handleException(Exception e) {
        logger.error("Default Exception", e);
        return ResponseUtils.error(MallExceptionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(UserException.class)
    @ResponseBody
    public ResponseUtils handleUserException(UserException ue) {
        logger.error("User Exception", ue);
        return ResponseUtils.error(ue.getStatus(), ue.getMsg());
    }

    @ExceptionHandler(CategoryException.class)
    @ResponseBody
    public ResponseUtils handleCategoryException(CategoryException ce) {
        logger.error("Category Exception", ce);
        return ResponseUtils.error(ce.getStatus(), ce.getMsg());
    }

    @ExceptionHandler(ProductException.class)
    @ResponseBody
    public ResponseUtils handleCategoryException(ProductException pe) {
        logger.error("Product Exception", pe);
        return ResponseUtils.error(pe.getStatus(), pe.getMsg());
    }
}
