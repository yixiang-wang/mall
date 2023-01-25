package com.junior.mall.model.dao;

import com.junior.mall.model.pojo.Product;
import com.junior.mall.model.query.ProductListQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectByName(String name);

    int updateStatusBatch(Integer[] ids, Integer sellStatus);

    List<Product> selectAll();

    List<Product> selectListByUser(@Param("query") ProductListQuery query);
}