package com.junior.mall.model.dao;

import com.junior.mall.model.pojo.Cart;
import com.junior.mall.model.vo.CartVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<CartVO> selectByUserId(Integer userId);

    Cart selectByUserIdAndProductId(Integer userId, Integer productId);

    int selectOrNot(Integer userId, Integer productId, Integer selected);
}