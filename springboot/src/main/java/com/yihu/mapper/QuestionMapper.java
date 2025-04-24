package com.yihu.mapper;

import com.yihu.entity.Question;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface QuestionMapper {

    void createQuestion(Question question);

    List<Question> findAllByQuestionnaireId(@Param("questionnaireId") Integer questionnaireId);
}
