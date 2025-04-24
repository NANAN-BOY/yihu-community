package com.yihu.mapper;

import com.yihu.entity.Questionnaire;
import io.lettuce.core.dynamic.annotation.Param;

public interface QuestionnaireMapper {

    Integer create(Questionnaire questionnaire);

    Integer startQuestionnaire(@Param("activityId") Integer activityId);

    Integer stopQuestionnaire(@Param("activityId") Integer activityId);

    Integer findQuestionnaireIdByActivityId(@Param("activityId") Integer activityId);
}
