package com.junior.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.junior.mall.exception.MallExceptionEnum;
import com.junior.mall.filter.UserFilter;
import com.junior.mall.model.dao.CartMapper;
import com.junior.mall.model.dao.OrderItemMapper;
import com.junior.mall.model.dao.OrderMapper;
import com.junior.mall.model.dao.ProductMapper;
import com.junior.mall.model.pojo.Order;
import com.junior.mall.model.pojo.OrderItem;
import com.junior.mall.model.pojo.Product;
import com.junior.mall.model.pojo.User;
import com.junior.mall.model.query.OrderStatisticsQuery;
import com.junior.mall.model.request.CreateOrderReq;
import com.junior.mall.model.vo.CartVO;
import com.junior.mall.model.vo.OrderItemVO;
import com.junior.mall.model.vo.OrderStatisticsVO;
import com.junior.mall.model.vo.OrderVO;
import com.junior.mall.service.CartService;
import com.junior.mall.service.OrderService;
import com.junior.mall.service.exception.ProductException;
import com.junior.mall.utils.Constant;
import com.junior.mall.utils.OrderCodeFactory;
import com.junior.mall.utils.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class OrderServiceImpl implements OrderService {
    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderItemMapper orderItemMapper;

    @Resource
    CartMapper cartMapper;

    @Resource
    CartService cartService;

    @Resource
    ProductMapper productMapper;

    @Value("${file.upload.uri}")
    String ip;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(CreateOrderReq createOrderReq) throws ProductException {
        Integer userId = UserFilter.user.getId();

        //1.将购物车中勾选的产品判断是否有售
        List<CartVO> cartVOList = cartService.getList(userId);
        ArrayList<CartVO> cartVOSelectedList = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            if (cartVO.getSelected().equals(Constant.CartSelect.SELECT)) {
                cartVOSelectedList.add(cartVO);
            }
        }
        cartVOList = cartVOSelectedList;
        //确定是否有勾选，未勾选报错
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new ProductException(MallExceptionEnum.NOT_FOUND);
        }
        //确定勾选商品是否在架或库存是否充足
        validStatusAndStock(cartVOList);
        //2.生成订单号(上述方法在未抛出异常时方可生成订单)
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        //3.将CartVOList中的商品添加到OrderItem中，生成记录同时并扣库存，添加后将cartVOList中的商品从购物车中移除
        int totalPrice = 0;
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(cartVO.getProductId());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            totalPrice += cartVO.getTotalPrice();
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            int stock = product.getStock() - cartVO.getQuantity();
            product.setStock(stock);
            product.setUpdateTime(null);
            productMapper.updateByPrimaryKeySelective(product);
            cartMapper.deleteByPrimaryKey(cartVO.getId());
            orderItemMapper.insertSelective(orderItem);
        }
        //3.将总价等内容添加到Order中
        Order order = new Order();
        BeanUtils.copyProperties(createOrderReq, order);
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        orderMapper.insertSelective(order);

        return orderNo;
    }

    private void validStatusAndStock(List<CartVO> cartVOList) throws ProductException {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            //判断是否在售中
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new ProductException(MallExceptionEnum.NOT_SALE.getStatus(), cartVO.getProductName() + MallExceptionEnum.NOT_SALE.getMsg());
            }
            //判断库存是否充足
            if (cartVO.getQuantity() > product.getStock()) {
                throw new ProductException(MallExceptionEnum.NOT_ENOUGH.getStatus(), cartVO.getProductName() + MallExceptionEnum.NOT_ENOUGH.getMsg());
            }
        }
    }

    @Override
    public OrderVO detail(String orderNo) throws ProductException {

        Integer userId = UserFilter.user.getId();
        Order order = orderMapper.selectByOrderNo(orderNo);
        //判断订单是否存在
        if (order == null) {
            throw new ProductException(MallExceptionEnum.NO_ORDER);
        }
        //判断订单归属
        if (!order.getUserId().equals(userId)) {
            throw new ProductException(MallExceptionEnum.NOT_YOUR_ORDER);
        }
        OrderVO orderVO = new OrderVO();
        //为orderVO赋值
        BeanUtils.copyProperties(order, orderVO);
        //为orderVO的订单状态名赋值（利用枚举）
        orderVO.setOrderStatusName(Constant.OrderStatusName.getStatus(orderVO.getOrderStatus()).getValue());
        //为orderVO的orderItemVOList赋值
        //① 生成orderItemList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        //② 遍历orderItem,转化为OrderItemVO,存储到OrderItemVOList中
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        return orderVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(String orderNo) throws ProductException {
        Integer userId = UserFilter.user.getId();
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ProductException(MallExceptionEnum.NO_ORDER);
        }
        if (!order.getUserId().equals(userId)) {
            throw new ProductException(MallExceptionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusName.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusName.CANCELED.getCode());
            order.setEndTime(new Date());
            order.setUpdateTime(null);
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ProductException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }

    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) throws ProductException {
        PageHelper.startPage(pageNum, pageSize);
        Integer userId = UserFilter.user.getId();
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVO> orderVOList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVO orderVO = getOrderVO(order.getOrderNo());
            orderVOList.add(orderVO);
        }
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    @Override
    public String qrCode(String orderNo) {
        //书写支付url
        String payUrl = "http://" + ip + "/order/pay?orderNo=" + orderNo;
        //生成二维码图片
        try {
            QRCodeGenerator.generateQRCodeImage(payUrl, 350, 350, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //资源映射的地址
        String pngAddress = "http://" + ip + "/images/" + orderNo + ".png";
        return pngAddress;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(String orderNo) throws ProductException {
        //订单归属判断
        Integer userId = UserFilter.user.getId();
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ProductException(MallExceptionEnum.NO_ORDER);
        }
        if (!order.getUserId().equals(userId)) {
            throw new ProductException(MallExceptionEnum.NOT_YOUR_ORDER);
        }
        //订单状态判断
        if (order.getOrderStatus().equals(Constant.OrderStatusName.NOT_PAID.getCode())) {
            order.setUpdateTime(null);
            order.setPayTime(new Date());
            order.setOrderStatus(Constant.OrderStatusName.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ProductException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) throws ProductException {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(null);
        List<OrderVO> orderVOList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVO orderVO = getOrderVO(order.getOrderNo());
            orderVOList.add(orderVO);
        }
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    @Override
    public void delivered(String orderNo) throws ProductException {
        //订单检查
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ProductException(MallExceptionEnum.NO_ORDER);
        }
        //订单状态检查
        if (order.getOrderStatus().equals(Constant.OrderStatusName.PAID.getCode())) {
            order.setUpdateTime(null);
            order.setDeliveryTime(new Date());
            order.setOrderStatus(Constant.OrderStatusName.DELIVERED.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ProductException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finish(String orderNo) throws ProductException {
        User user = UserFilter.user;
        //订单检查
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ProductException(MallExceptionEnum.NO_ORDER);
        }
        //判断订单归属(非管理员且订单归属不符时抛出异常）
        if (!user.getRole().equals(2) && !order.getUserId().equals(user.getId())) {
            throw new ProductException(MallExceptionEnum.NOT_YOUR_ORDER);
        }
        //订单状态检查
        if (order.getOrderStatus().equals(Constant.OrderStatusName.DELIVERED.getCode())) {
            order.setUpdateTime(null);
            order.setEndTime(new Date());
            order.setOrderStatus(Constant.OrderStatusName.FINISHED.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ProductException(MallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    private OrderVO getOrderVO(String orderNo) throws ProductException {

        Order order = orderMapper.selectByOrderNo(orderNo);
        //判断订单是否存在
        if (order == null) {
            throw new ProductException(MallExceptionEnum.NO_ORDER);
        }
        OrderVO orderVO = new OrderVO();
        //为orderVO赋值
        BeanUtils.copyProperties(order, orderVO);
        //为orderVO的订单状态名赋值（利用枚举）
        orderVO.setOrderStatusName(Constant.OrderStatusName.getStatus(orderVO.getOrderStatus()).getValue());
        //为orderVO的orderItemVOList赋值
        //① 生成orderItemList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        //② 遍历orderItem,转化为OrderItemVO,存储到OrderItemVOList中
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        return orderVO;
    }

    @Override
    public List<OrderStatisticsVO> OrderStatistics(Date startDate, Date endDate){
        OrderStatisticsQuery query=new OrderStatisticsQuery();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        List<OrderStatisticsVO> list=orderMapper.selectOrderStatistics(query);
        return list;
    }
}
