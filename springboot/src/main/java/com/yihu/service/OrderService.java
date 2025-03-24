package com.yihu.service;

import com.github.pagehelper.PageInfo;
import com.yihu.dto.OrderQueryDTO;
import com.yihu.entity.Order;

public interface OrderService {

    String generateOrderNo();

    Boolean createOrder(String orderNo, Integer buyerId, Integer type, Integer status, float paymentAmount);

    Order findByOrderNo(String orderNo);

    Boolean updateOrder(String orderNo, String tradeNo);

    PageInfo<Order> queryOrder(OrderQueryDTO orderQueryDTO, Integer id, int pageNum, int pageSize);
}
