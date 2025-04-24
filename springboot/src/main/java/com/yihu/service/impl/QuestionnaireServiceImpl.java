package com.yihu.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yihu.entity.*;
import com.yihu.mapper.*;
import com.yihu.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final AnswerMapper answerMapper;
    private final QuestionnaireMapper questionnaireMapper;
    private final TempMapper tempMapper;
    private final QuestionMapper questionMapper;
    private final QuestionnaireIpMapper questionnaireIpMapper;

    @Autowired
    public QuestionnaireServiceImpl(AnswerMapper answerMapper,
                                    QuestionnaireMapper questionnaireMapper,
                                    TempMapper tempMapper,
                                    QuestionMapper questionMapper,
                                    QuestionnaireIpMapper questionnaireIpMapper) {
        this.answerMapper = answerMapper;
        this.questionnaireMapper = questionnaireMapper;
        this.tempMapper = tempMapper;
        this.questionMapper = questionMapper;
        this.questionnaireIpMapper = questionnaireIpMapper;
    }


    @Override
    public List<Answer> analysis(Integer questionId) {
        return answerMapper.analysis(questionId);
    }

    @Override
    @Transactional
    public Boolean create(Integer activityId, Integer userId) {

        Questionnaire questionnaire = new Questionnaire(userId,
                activityId,
                new Date(),
                0,
                null,
                0,
                null,
                0);

        if (questionnaireMapper.create(questionnaire) > 0) {
            for (Temp temp : tempMapper.selectTemp()) {
                Question question = new Question(questionnaire.getQuestionnaireId(),
                        temp.getQuestionTitle(),
                        temp.getQuestionDescription(),
                        temp.getQuestionNullable(),
                        temp.getQuestionType(),
                        temp.getDetails());
                questionMapper.createQuestion(question);
            }
            return true;
        }
        return false;
    }

    @Override
    public Integer release(Integer activityId) {
        int isSuccess = questionnaireMapper.startQuestionnaire(activityId);
        if (isSuccess > 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public Integer stop(Integer activityId) {
        int isSuccess = questionnaireMapper.stopQuestionnaire(activityId);
        if (isSuccess > 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public String getQuestion(Integer activityId) {
        Gson gson = new Gson();
        Integer QuestionIdDigit = 1000; // 问卷id的位数
        JsonArray resList = new JsonArray();

        Integer questionnaireId = questionnaireMapper.findQuestionnaireIdByActivityId(activityId);
        List<Question> questionList = questionMapper.findAllByQuestionnaireId(questionnaireId);
        for (Question oneQuestion : questionList) {

            JsonObject oneRes = gson.fromJson(gson.toJson(oneQuestion), JsonObject.class);
            oneRes.addProperty("isBoxSelected", false);
            oneRes.addProperty("questionTitle", oneQuestion.getQuestionTitle());
            oneRes.addProperty("questionDescription", oneQuestion.getQuestionDescription());
            oneRes.addProperty("questionIndex", oneQuestion.getQuestionId() % QuestionIdDigit);
            oneRes.addProperty("questionNullable", oneQuestion.getQuestionNullable());
            oneRes.addProperty("questionType", oneQuestion.getQuestionType());

            JsonObject temp = gson.fromJson(oneQuestion.getDetails(), JsonObject.class);

            processDetails(oneRes, temp);

            resList.add(oneRes);
        }
        JsonObject res = new JsonObject();
        res.add("questionList", resList);
        return gson.toJson(res);
    }

    private void processDetails(JsonObject oneRes, JsonObject temp) {
        if (temp != null) {
            oneRes.add("questionOptions", temp.get("questionOptions").getAsJsonArray());
            oneRes.add("frontOptions", temp.get("frontOptions").getAsJsonArray());
            oneRes.addProperty("frontChoose", temp.get("frontChoose").getAsBoolean());
            oneRes.addProperty("numberType", temp.get("numberType").getAsString());
            oneRes.addProperty("defaultNumber", temp.get("defaultNumber").getAsInt());
            oneRes.addProperty("gradeMax", temp.get("gradeMax").getAsInt());
            oneRes.addProperty("date", temp.get("date").getAsString());
            oneRes.addProperty("textDescription", temp.get("textDescription").getAsString());
        }

    }

}
