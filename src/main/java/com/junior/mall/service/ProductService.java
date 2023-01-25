package com.junior.mall.service;

import com.github.pagehelper.PageInfo;
import com.junior.mall.model.pojo.Product;
import com.junior.mall.model.request.AddProductReq;
import com.junior.mall.model.request.ListProductReq;
import com.junior.mall.model.request.UpdateProductReq;
import com.junior.mall.service.exception.ProductException;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

public interface ProductService {
    void addProduct(AddProductReq productReq) throws ProductException;

    @Transactional(rollbackFor = Exception.class)
    void updateProduct(UpdateProductReq productReq) throws ProductException;

    @Transactional(rollbackFor = Exception.class)
    void deleteProduct(Integer id) throws ProductException;

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo selectAllAdmin(Integer pageNum, Integer pageSize);

    PageInfo userList(ListProductReq productReq);

    Product getDetail(Integer id);

    void batchByExcel(File destFile) throws IOException, ProductException;
}
