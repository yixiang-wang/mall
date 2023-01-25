package com.junior.mall.controller;

import com.junior.mall.filter.UserFilter;
import com.junior.mall.model.pojo.Cart;
import com.junior.mall.model.vo.CartVO;
import com.junior.mall.service.CartService;
import com.junior.mall.service.exception.ProductException;
import com.junior.mall.utils.ResponseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Resource
    private CartService cartService;

    @GetMapping("/list")
    public ResponseUtils getList() {
        List<CartVO> cartList = cartService.getList(UserFilter.user.getId());
        return ResponseUtils.success(cartList);
    }

    @PostMapping("/add")
    public ResponseUtils add(Integer productId, Integer count) throws ProductException {
        List<CartVO> cartVOS = cartService.add(UserFilter.user.getId(), productId, count);
        return ResponseUtils.success(cartVOS);
    }

    @PostMapping("/update")
    public ResponseUtils update(Integer productId, Integer count) throws ProductException {
        List<CartVO> cartVOS = cartService.update(UserFilter.user.getId(), productId, count);
        return ResponseUtils.success(cartVOS);
    }

    @PostMapping("/delete")
    public ResponseUtils delete(Integer productId) throws ProductException {
        List<CartVO> cartVOS = cartService.delete(UserFilter.user.getId(), productId);
        return ResponseUtils.success(cartVOS);
    }

    @PostMapping("/select")
    public ResponseUtils select(Integer productId, Integer selected) throws ProductException {
        List<CartVO> cartVOS = cartService.selectOrNot(UserFilter.user.getId(), productId, selected);
        return ResponseUtils.success(cartVOS);
    }

    @PostMapping("/selectAll")
    public ResponseUtils selectAll(Integer selected) {
        List<CartVO> cartVOS = cartService.selectAllOrNot(UserFilter.user.getId(), selected);
        return ResponseUtils.success(cartVOS);
    }
}
