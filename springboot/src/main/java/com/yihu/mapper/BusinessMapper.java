package com.yihu.mapper;

import com.yihu.entity.Business;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.Date;

public interface BusinessMapper {
    Integer insertBusiness(Business business);

    Business getBusiness(@Param("orderNo") String orderNo,
                         @Param("applyUserId") Integer applyUserId);

    Business getMyBusiness(@Param("orderNo") String orderNo,
                           @Param("acceptExpertId") Integer acceptExpertId);

    Business getBusinessById(@Param("id") Integer id,
                             @Param("userId") Integer userId);

    Business getBusinessByOrderNo(@Param("orderNo") String orderNo);

    int finishBusiness(@Param("id") Integer id,
                       @Param("oldStatus") Integer oldStatus,
                       @Param("newStatus") Integer newStatus,
                       @Param("finishAt") Date finishAt);
}
