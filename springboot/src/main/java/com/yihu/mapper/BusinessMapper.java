package com.yihu.mapper;

import com.yihu.entity.Business;
import io.lettuce.core.dynamic.annotation.Param;

public interface BusinessMapper {
    Integer insertBusiness(Business business);

    Business getBusiness(@Param("orderNo") String orderNo,
                         @Param("applyUserId") Integer applyUserId);

    Business getMyBusiness(@Param("orderNo") String orderNo,
                           @Param("acceptExpertId") Integer acceptExpertId);
}
