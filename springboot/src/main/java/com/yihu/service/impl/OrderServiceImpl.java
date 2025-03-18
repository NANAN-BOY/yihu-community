package com.yihu.service.impl;

import cn.hutool.core.util.IdUtil;
import com.yihu.entity.Order;
import com.yihu.mapper.OrderMapper;
import com.yihu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }


    @Override
    public String generateOrderNo() {
        return IdUtil.getSnowflakeNextIdStr();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean createOrder(String orderNo, Integer buyerId, Integer type, Integer status, float paymentAmount) {
        try {
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            int isSuccess = orderMapper.insertOrder(orderNo, buyerId, type, status, paymentAmount, currentTimestamp);
            return isSuccess > 0;
        } catch (Exception e) {
            System.err.println("订单创建异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Order findByOrderNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(Order order) {
        // 参数校验
        if (order.getOrderNo() == null || order.getStatus() == null) {
            throw new IllegalArgumentException("订单号和状态不能为空");
        }

        // 执行乐观锁更新
        int affectedRows = orderMapper.updateOrderStatus(
                order.getOrderNo(),
                order.getStatus() - 1,  // 示例：假设新状态=旧状态+1
                order.getStatus(),
                order.getOtherOrderNo(),
                order.getPayAt(),
                order.getPaymentType()
        );

        if (affectedRows == 0) {
            // 更新失败：可能订单不存在或状态不匹配
            throw new RuntimeException("订单状态更新冲突或订单不存在: " + order.getOrderNo());
        }
    }

}
