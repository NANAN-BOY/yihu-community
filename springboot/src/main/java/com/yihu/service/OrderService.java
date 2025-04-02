package com.yihu.service;

import com.github.pagehelper.PageInfo;
import com.yihu.dto.OrderQueryDTO;
import com.yihu.entity.Business;
import com.yihu.entity.Order;

public interface OrderService {

    String generateOrderNo();

    Boolean createOrder(String orderNo, Integer buyerId, Integer type, Integer status, float paymentAmount);

    Order findByOrderNo(String orderNo);

    Boolean updateOrder(String orderNo, String tradeNo);

    PageInfo<Order> queryOrder(OrderQueryDTO orderQueryDTO, Integer id, int pageNum, int pageSize);

    PageInfo<Order> queryPreemptOrder(Integer id, int status, int pageNum, int pageSize);

    Business getBusiness(String orderNo, Integer userId);

    Business getMyBusiness(String orderNo, Integer userId);

    Business getBusinessById(Integer id, Integer userId);

    int finishOrder(String orderNo, Integer id);

    void sendMessage(String orderNo);
}
