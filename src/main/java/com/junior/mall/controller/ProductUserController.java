package com.junior.mall.controller;

import com.junior.mall.model.request.ListProductReq;
import com.junior.mall.service.ProductService;
import com.junior.mall.utils.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ProductUserController {
    @Resource
    private ProductService productService;

    @GetMapping("/product/detail")
    public ResponseUtils detail(Integer id) {
        return ResponseUtils.success(productService.getDetail(id));
    }

    @GetMapping("/product/list")
    public ResponseUtils list(ListProductReq productReq) {
        return ResponseUtils.success(productService.userList(productReq));
    }
}
