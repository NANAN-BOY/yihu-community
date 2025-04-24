package com.yihu.service;

import com.yihu.entity.Answer;

import java.util.List;

public interface QuestionnaireService {

    List<Answer> analysis(Integer questionId);

    Boolean create(Integer activityId, Integer userId);

    Integer release(Integer activityId);

    Integer stop(Integer activityId);

    String getQuestion(Integer activityId);
}
