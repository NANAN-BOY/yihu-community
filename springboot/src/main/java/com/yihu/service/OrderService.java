package com.yihu.service;

import com.yihu.entity.Order;

public interface OrderService {

    String generateOrderNo();

    Boolean createOrder(String orderNo, Integer buyerId, Integer type, Integer status, float paymentAmount);

    Order findByOrderNo(String orderNo);

    void updateOrder(Order order);
}
