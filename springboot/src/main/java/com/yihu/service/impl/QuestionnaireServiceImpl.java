package com.yihu.service.impl;

import com.yihu.entity.Answer;
import com.yihu.entity.Questionnaire;
import com.yihu.mapper.AnswerMapper;
import com.yihu.mapper.QuestionnaireMapper;
import com.yihu.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final AnswerMapper answerMapper;
    private final QuestionnaireMapper questionnaireMapper;

    @Autowired
    public QuestionnaireServiceImpl(AnswerMapper answerMapper, QuestionnaireMapper questionnaireMapper) {
        this.answerMapper = answerMapper;
        this.questionnaireMapper = questionnaireMapper;
    }


    @Override
    public List<Answer> analysis(Integer questionId) {
        return answerMapper.analysis(questionId);
    }

    @Override
    public Boolean create(Integer activityId, Integer userId, String title, String description) {
        Questionnaire questionnaire = new Questionnaire(userId,
                activityId,
                new Date(),
                0,
                title,
                0,
                description,
                0);

        return questionnaireMapper.create(questionnaire) > 0;
    }
}
