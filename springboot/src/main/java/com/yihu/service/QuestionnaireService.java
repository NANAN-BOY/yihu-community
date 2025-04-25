package com.yihu.service;

import com.yihu.entity.Answer;

import java.util.List;

public interface QuestionnaireService {

    String analysis(Integer questionId);

    Boolean create(Integer activityId, Integer userId);

    Integer release(Integer activityId);

    Integer stop(Integer activityId);

    String getQuestion(Integer activityId);

    List<Answer> getAnswer(Integer questionId);
}
