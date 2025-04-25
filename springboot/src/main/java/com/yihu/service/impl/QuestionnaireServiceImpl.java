package com.yihu.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yihu.entity.*;
import com.yihu.mapper.*;
import com.yihu.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    @Transactional
    public String analysis(Integer questionId) {
        Gson gson = new Gson();
        List<Answer> answerList = answerMapper.analysis(questionId);

        Question question = questionMapper.findQuestionByQuestionId(questionId);
        String questionType = question.getQuestionType();


        if (questionType.equals("single_check")) {
            Map<String, Integer> resValueMap = new HashMap<>();
            JsonObject temp = gson.fromJson(question.getDetails(), JsonObject.class);
            JsonArray questionOptionsJsonArray = temp.get("questionOptions").getAsJsonArray();
            for (JsonElement questionOptionJson : questionOptionsJsonArray
            ) {
                String questionOption = questionOptionJson.getAsString();
                resValueMap.put(questionOption, 0);
            }
            System.out.println(temp);
            System.out.println(resValueMap);
            for (Answer oneAnswer : answerList
            ) {
                String value = oneAnswer.getWriteValue();
                Integer oldCount = resValueMap.get(value);
                resValueMap.put(value, oldCount + 1);
            }
            return gson.toJson(resValueMap);
        } else if (questionType.equals("multi_check")) {
            Map<String, Integer> resValueMap = new HashMap<>();
            JsonObject temp = gson.fromJson(question.getDetails(), JsonObject.class);
            JsonArray questionOptionsJsonArray = temp.get("questionOptions").getAsJsonArray();
            for (JsonElement questionOptionJson : questionOptionsJsonArray
            ) {
                String questionOption = questionOptionJson.getAsString();
                resValueMap.put(questionOption, 0);
            }
            for (Answer oneAnswer : answerList
            ) {
                JsonArray valueList = gson.fromJson(oneAnswer.getWriteValue(), JsonArray.class);
                for (JsonElement value : valueList
                ) {
                    Integer oldCount = resValueMap.get(value.getAsString());
                    resValueMap.put(value.getAsString(), oldCount + 1);
                }
            }
            return gson.toJson(resValueMap);
        } else if (questionType.equals("number") || questionType.equals("grade")) {
            Map<String, Double> resValueMap = new HashMap<>();
            List<Double> valueList = new ArrayList<>();
            Double sum = 0.0;
            for (Answer oneAnswer : answerList
            ) {
                Double value = gson.fromJson(oneAnswer.getWriteValue(), Double.class);
                valueList.add(value);
                sum += value;
            }
            valueList.sort((a, b) -> (int) (a - b));
            System.out.println(valueList);

            if (valueList.isEmpty()) {
                resValueMap.put("最大值", 0.0);
                resValueMap.put("最小值", 0.0);
                resValueMap.put("平均值", 0.0);
                resValueMap.put("中位数", 0.0);
            } else {
                resValueMap.put("最大值", valueList.get(valueList.size() - 1));
                resValueMap.put("最小值", valueList.get(0));
                resValueMap.put("平均值", sum / valueList.size());
                resValueMap.put("中位数", valueList.get(valueList.size() / 2));
            }

            return gson.toJson(resValueMap);
        }
        return null;
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

    /**
     * questionIndex: this.questionList.length,
     * isBoxSelected: true,
     * questionTitle: "请输入标题",
     * questionNullable: false,
     * questionDescription: "请输入描述",
     * questionType: type,
     * questionOptions: [],
     * frontOptions: [[]],
     * frontChoose: false,
     * numberType: 'integer',
     * defaultNumber: 0,
     * gradeMax: 5,
     * date: new Date(),
     * textDescription: '',
     */
    @Override
    public String getQuestion(Integer activityId) {
        Gson gson = new Gson();
        Integer QuestionIdDigit = 1000; // 问卷id的位数
        JsonArray resList = new JsonArray();

        Integer questionnaireId = questionnaireMapper.findQuestionnaireIdByActivityId(activityId);
        List<Question> questionList = questionMapper.getQuestionList(questionnaireId);
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

    @Override
    public List<Answer> getAnswer(Integer questionId) {
        return answerMapper.analysis(questionId);
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
