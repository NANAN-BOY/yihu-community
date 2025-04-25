package com.yihu.mapper;

import com.yihu.entity.Answer;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface AnswerMapper {

    List<Answer> analysis(@Param("questionId") Integer questionId);

}
