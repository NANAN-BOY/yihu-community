package com.yihu.service.impl;

import cn.hutool.core.util.IdUtil;
import com.yihu.entity.MemberShip;
import com.yihu.entity.Order;
import com.yihu.mapper.MemberShipMapper;
import com.yihu.mapper.OrderMapper;
import com.yihu.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final MemberShipMapper memberShipMapper;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, MemberShipMapper memberShipMapper) {
        this.orderMapper = orderMapper;
        this.memberShipMapper = memberShipMapper;
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
    public void updateOrder(Order order, MemberShip ms) {
        // 参数校验
        if (order.getOrderNo() == null || order.getStatus() == null) {
            throw new IllegalArgumentException("订单号和状态不能为空");
        }
        //专家服务订单
        if (order.getType() == 0) {
            // 执行乐观锁更新
            int affectedRows = orderMapper.ExpertOrder(
                    order.getOrderNo(),
                    order.getStatus() - 1,  // 示例：假设新状态=旧状态+1
                    order.getStatus(),
                    order.getOtherOrderNo(),
                    order.getPayAt(),
                    order.getPaymentType()
            );
            if (affectedRows != 0) {
                log.info("订单状态更新成功");
                return;
            } else {
                throw new RuntimeException("订单状态更新冲突或订单不存在: " + order.getOrderNo());
            }
        }

        //购买vip
        // 执行乐观锁更新
        int affectedRows = orderMapper.vipOrder(
                order.getOrderNo(),
                order.getStatus() - 1,  // 示例：假设新状态=旧状态+1
                order.getStatus(),
                order.getOtherOrderNo(),
                order.getPayAt(),
                order.getPaymentType(),
                order.getPayAt()
        );
        if (affectedRows != 0) {
            if (ms == null) {
                MemberShip memberShip = new MemberShip(order.getOrderNo(),
                        order.getBuyerId(), order.getPayAt(), order.getEndAt(), 1);
                int isSuccess = orderMapper.insertVip(memberShip);
                if (isSuccess > 0) {
                    log.info("会员信息: {}", memberShip);
                } else {
                    log.error("会员信息插入失败: {}", memberShip);
                }
            } else {
                log.info("会员信息已存在: {}", order.getBuyerId());
                MemberShip memberShip = new MemberShip(order.getOrderNo(),
                        order.getBuyerId(), order.getPayAt(), order.getEndAt(), 1);
                int isSuccess = orderMapper.updateVip(memberShip);
                if (isSuccess > 0) {
                    log.info("会员信息更新成功: {}", memberShip);
                } else {
                    log.error("会员信息更新失败: {}", memberShip);
                }
            }
            System.out.println("订单状态更新成功");
        }else {
            throw new RuntimeException("订单状态更新冲突或订单不存在: " + order.getOrderNo());
        }
    }

}
