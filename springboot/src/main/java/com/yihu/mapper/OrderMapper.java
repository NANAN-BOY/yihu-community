package com.yihu.mapper;

import com.yihu.dto.OrderQueryDTO;
import com.yihu.entity.MemberShip;
import com.yihu.entity.Order;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.Date;
import java.util.List;


public interface OrderMapper {

    int insertOrder(@Param("orderNo") String orderNo,@Param("buyerId") Integer buyerId,@Param("type") Integer type,
                    @Param("status") Integer status,@Param("paymentAmount") float paymentAmount,
                    @Param("createAt") Date createAt);

    Order findByOrderNo(@Param("orderNo") String orderNo);

    int vipOrder(@Param("orderNo") String orderNo,
                          @Param("oldStatus") Integer oldStatus,
                          @Param("newStatus") Integer newStatus,
                          @Param("otherOrderNo") String otherOrderNo,
                          @Param("payAt") Date payAt,
                          @Param("paymentType") Integer paymentType,
                          @Param("endAt") Date endAt);

    int insertVip(MemberShip memberShip);

    int updateVip(MemberShip memberShip);

    int ExpertOrder(@Param("orderNo") String orderNo,
                    @Param("oldStatus") Integer oldStatus,
                    @Param("newStatus") Integer newStatus,
                    @Param("otherOrderNo") String otherOrderNo,
                    @Param("payAt") Date payAt,
                    @Param("paymentType") Integer paymentType);

    List<Order> getOrderList();

    int updateExpertOrder(@Param("orderNo") String orderNo,
                          @Param("expertId") Integer expertId,
                          @Param("newStatus") Integer newStatus,
                          @Param("oldStatus") Integer oldStatus);

    List<Order> queryOrder(OrderQueryDTO orderQueryDTO,
                           @Param("userId") Integer userId);
}
