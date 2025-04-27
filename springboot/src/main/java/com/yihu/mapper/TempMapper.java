package com.yihu.mapper;

import com.yihu.entity.Temp;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface TempMapper {

    List<Temp> selectTemp();

    Integer insert(Temp temp);

    Integer deleteTemp(@Param("tempId") Integer tempId);

    Integer updateTemp(Temp temp);
}
