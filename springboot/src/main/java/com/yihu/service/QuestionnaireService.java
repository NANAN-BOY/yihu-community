package com.yihu.service;

import com.yihu.dto.AnswerDTO;
import com.yihu.entity.Answer;
import com.yihu.entity.Question;

import java.util.List;

public interface QuestionnaireService {

    String analysis(Integer questionId);

    Integer create();

    Integer release(Integer questionnaireId);

    Integer stop(Integer questionnaireId);

    String getQuestion(Integer questionnaireId);

    List<Answer> getAnswer(Integer questionId);

    Integer submit(List<AnswerDTO> answers, Integer questionnaireId, String ip);

    Integer update(List<Question> questions);
}
