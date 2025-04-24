package com.yihu.controller;


import com.yihu.common.Result;
import com.yihu.entity.Answer;
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

    @GetMapping("/analysis")
    public Result analysis(@RequestParam Integer questionId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        List<Answer> answers = questionnaireService.analysis(questionId);

        return Result.success(answers);
    }

    @PostMapping("/create")
    public Result create(@RequestParam Integer activityId,
                         @RequestParam String title,
                         @RequestParam String description) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        Boolean isSuccess = questionnaireService.create(activityId, currentUser.getId(), title, description);
        if (isSuccess) {
            return Result.success();
        } else {
            return Result.error(500, "创建失败");
        }
    }

}
