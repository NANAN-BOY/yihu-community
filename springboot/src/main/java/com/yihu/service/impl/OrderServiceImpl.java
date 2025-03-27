package com.yihu.service.impl;

import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yihu.dto.OrderQueryDTO;
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
import java.util.Calendar;
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

    private Boolean compareTime(Date deadLine) {
        return deadLine.compareTo(new Date()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrder(String orderNo, String tradeNo) {

        Order order = this.findByOrderNo(orderNo);
        MemberShip ms = memberShipMapper.selectByUserID(order.getBuyerId());
        // 参数校验
        if (order.getOrderNo() == null || order.getStatus() == null) {
            throw new IllegalArgumentException("订单号和状态不能为空");
        }

        order.setStatus(1);
        order.setPayAt(new Date());
        order.setOtherOrderNo(tradeNo);
        order.setPaymentType(1);
        //专家服务订单
        if (order.getType() == 0) {
            // 执行乐观锁更新
            int affectedRows = orderMapper.ExpertOrder(
                    order.getOrderNo(),
                    order.getStatus() - 1,  // 旧状态=已支付状态-1
                    order.getStatus(), //新状态=已支付状态
                    order.getOtherOrderNo(),
                    new Date(),// payAt=当前时间
                    order.getPaymentType()
                    //endAt在服务结束后再通过别的方法写入
            );
            if (affectedRows != 0) {
                log.info("订单状态更新成功");
                return true;
            } else {
                return false;
            }
        }

        //购买vip
        Calendar calendar = Calendar.getInstance();
        if (ms != null && compareTime(ms.getDeadline())) {
            calendar.setTime(ms.getDeadline());
        } else {
            calendar.setTime(order.getPayAt());
        }
        // 执行乐观锁更新
        int affectedRows = orderMapper.vipOrder(
                order.getOrderNo(),
                order.getStatus() - 1,  // 旧状态=已支付状态-1
                order.getStatus() + 2, // 新状态=已支付状态+2
                order.getOtherOrderNo(),
                new Date(),// payAt=当前时间
                order.getPaymentType(),
                new Date() //endAt=当前时间
        );
        if (affectedRows != 0) {
            if (order.getType() == 1) {
                calendar.add(Calendar.MONTH, 1);
            } else if (order.getType() == 2) {
                calendar.add(Calendar.YEAR, 1);
            }
            MemberShip memberShip = new MemberShip(order.getOrderNo(),
                    order.getBuyerId(), order.getPayAt(), calendar.getTime(), 1);
            if (ms == null) {
                return orderMapper.insertVip(memberShip) > 0;
            } else {
                return orderMapper.updateVip(memberShip) > 0;
            }
        }else {
            return false;
        }
    }

    @Override
    public PageInfo<Order> queryOrder(OrderQueryDTO orderQueryDTO, Integer id, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(orderMapper.queryOrder(orderQueryDTO, id));
    }

    @Override
    public PageInfo<Order> queryPreemptOrder(Integer id, int status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(orderMapper.queryPreemptOrder(id, status));
    }

}
