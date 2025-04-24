package com.yihu.controller;


import com.yihu.common.Result;
import com.yihu.entity.Answer;
import com.yihu.entity.Question;
import com.yihu.entity.User;
import com.yihu.service.QuestionnaireService;
import com.yihu.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    @Autowired
    public QuestionnaireController(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @PutMapping("/release")
    public Result release(@RequestParam Integer activityId) {

        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        int isSuccess = questionnaireService.release(activityId);
        if (isSuccess == 1) {
            return Result.success("问卷发布成功");
        }
        return Result.error(501, "问卷发布失败");
    }

    @PutMapping("/stop")
    public Result stop(@RequestParam Integer activityId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        int isSuccess = questionnaireService.stop(activityId);
        if (isSuccess == 1) {
            return Result.success("已成功关停问卷");
        }
        return Result.error(501, "关停失败");
    }

    @GetMapping("/get-question")
    public Result getQuestion(@RequestParam Integer activityId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        String question = questionnaireService.getQuestion(activityId);
        return Result.success(question);
    }

    @GetMapping("/analysis")
    public Result analysis(@RequestParam Integer questionId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        List<Answer> answers = questionnaireService.analysis(questionId);

        return Result.success(answers);
    }


}
