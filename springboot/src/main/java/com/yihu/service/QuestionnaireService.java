package com.yihu.service;

import com.yihu.dto.TempDTO;
import com.yihu.entity.Answer;
import com.yihu.entity.Temp;

import java.util.List;

public interface QuestionnaireService {

    String analysis(Integer questionId);

    Integer create();

    Integer release(Integer questionnaireId);

    Integer stop(Integer questionnaireId);

    String getQuestion(Integer questionnaireId);

    List<Answer> getAnswer(Integer questionId);

    Integer submit(String answers, Integer questionnaireId, String ip);

    Integer addQuestionToTemp(TempDTO tempDTO);

    List<Temp> getTemp();

    Integer deleteQuestionToTemp(Integer tempId);

    Integer updateQuestionToTemp(Temp temp);

    void delete(Integer questionnaireId);
}
