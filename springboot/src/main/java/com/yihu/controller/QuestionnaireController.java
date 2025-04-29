package com.yihu.controller;


import com.yihu.common.AuthAccess;
import com.yihu.common.Result;
import com.yihu.dto.AnswerDTO;
import com.yihu.dto.TempDTO;
import com.yihu.entity.Answer;
import com.yihu.entity.Temp;
import com.yihu.entity.User;
import com.yihu.service.QuestionnaireService;
import com.yihu.utils.TokenUtils;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
    public Result release(@RequestParam Integer questionnaireId) {

        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        int isSuccess = questionnaireService.release(questionnaireId);
        if (isSuccess == 1) {
            return Result.success("问卷发布成功");
        }
        return Result.error(501, "问卷发布失败");
    }

    @PutMapping("/stop")
    public Result stop(@RequestParam Integer questionnaireId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        int isSuccess = questionnaireService.stop(questionnaireId);
        if (isSuccess == 1) {
            return Result.success("已成功关停问卷");
        }
        return Result.error(501, "关停失败");
    }

    @AuthAccess
    @GetMapping("/get-question")
    public Result getQuestion(@RequestParam Integer questionnaireId) {
        String question = questionnaireService.getQuestion(questionnaireId);
        return Result.success(question);
    }

    @GetMapping("/analysis")
    public Result analysis(@RequestParam Integer questionId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        String answers = questionnaireService.analysis(questionId);
        if (answers == null) {
            return Result.error(404, "未找到该问题");
        }
        return Result.success(answers);
    }

    @GetMapping("/get-answer")
    public Result getAnswer(@RequestParam Integer questionId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }

        List<Answer> answers = questionnaireService.getAnswer(questionId);
        return Result.success(answers);
    }

    @AuthAccess
    @PostMapping("/submit")
    public Result submit(@RequestBody String answers,
                         @RequestParam Integer questionnaireId,
                         @RequestParam String ip) {

        if (questionnaireId == null || questionnaireId <= 0) {
            return Result.error(400, "问卷ID无效");
        }
        if (StringUtils.isBlank(ip)) {
            return Result.error(400, "IP地址不能为空");
        }

        // 调用服务层提交逻辑
        Integer resultCode = questionnaireService.submit(answers, questionnaireId, ip);

        return switch (resultCode) {
            case 1 -> Result.success("问卷提交成功");
            case -1 -> Result.error(409, "已重复提交，每个IP仅限提交一次");
            case -2 -> Result.error(404, "无效的活动ID，未找到对应问卷");
            case -3 -> Result.error(404, "目标问卷不存在");
            case -4 -> Result.error(403, "问卷已关闭，当前不可提交");
            case -5 -> Result.error(400, "包含不支持的题目类型，无法处理");
            case -6 -> Result.error(400, "答案数据格式错误（JSON解析失败）");
            case -99 -> Result.error(500, "系统异常，请稍后重试");
            default -> Result.error(500, "未知错误，请联系管理员");
        };
    }

    @PostMapping("/add-question")
    public Result addQuestionToTemp(@RequestBody TempDTO tempDTO) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }
        if (currentUser.getRole() != 1) {
            return Result.error(403, "无权限");
        }
        int isSuccess = questionnaireService.addQuestionToTemp(tempDTO);
        if (isSuccess == 1) {
            return Result.success("添加成功");
        }
        return Result.error(501, "添加失败");
    }

    @GetMapping("/get-temp")
    public Result getTemp() {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }
        List<Temp> temp = questionnaireService.getTemp();
        return Result.success(temp);
    }

    @DeleteMapping("/delete-question")
    public Result deleteQuestionToTemp(@RequestParam Integer tempId) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }
        if (currentUser.getRole() != 1) {
            return Result.error(403, "无权限");
        }
        int isSuccess = questionnaireService.deleteQuestionToTemp(tempId);
        if (isSuccess == 1) {
            return Result.success("删除成功");
        }
        return Result.error(501, "删除失败");
    }

    @PutMapping("/update-question")
    public Result updateQuestionToTemp(@RequestBody Temp temp) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "请登录");
        }
        if (currentUser.getRole() != 1) {
            return Result.error(403, "无权限");
        }
        int isSuccess = questionnaireService.updateQuestionToTemp(temp);
        if (isSuccess == 1) {
            return Result.success("修改成功");
        }
        return Result.error(501, "修改失败");
    }

    @AuthAccess
    @PostMapping("/create")
    public Result create() {
        return Result.success(questionnaireService.create());
    }

//    @AuthAccess
//    @DeleteMapping("/delete")
//    public Result delete(@RequestParam Integer questionnaireId) {
//
//        return Result.success(questionnaireService.delete(questionnaireId));
//    }

}
