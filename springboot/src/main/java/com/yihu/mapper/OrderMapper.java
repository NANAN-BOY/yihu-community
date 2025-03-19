package com.yihu.mapper;

import com.yihu.entity.MemberShip;
import com.yihu.entity.Order;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.Date;


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
}
