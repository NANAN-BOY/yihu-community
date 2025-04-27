package com.yihu.mapper;

import com.yihu.entity.Questionnaire;
import io.lettuce.core.dynamic.annotation.Param;

public interface QuestionnaireMapper {

    Integer create(Questionnaire questionnaire);

    Integer startQuestionnaire(@Param("questionnaireId") Integer questionnaireId);

    Integer stopQuestionnaire(@Param("questionnaireId") Integer questionnaireId);

    Questionnaire selectByPrimaryKey(@Param("questionnaireId") Integer questionnaireId);

    int incrementFillCount(@Param("questionnaireId") Integer questionnaireId);

    void deleteQuestionnaire(@Param("questionnaireId") Integer questionnaireId);
}
