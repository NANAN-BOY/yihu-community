package com.yihu.mapper;

import com.yihu.entity.MemberShip;
import io.lettuce.core.dynamic.annotation.Param;

public interface MemberShipMapper {

    MemberShip selectByUserID(@Param("userId") Integer userId);
}
