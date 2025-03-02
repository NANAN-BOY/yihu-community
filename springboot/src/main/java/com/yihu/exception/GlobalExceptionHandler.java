package com.yihu.exception;

import com.yihu.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice("com.yihu.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody//返json串
    public Result error(Exception e) {
        return Result.error();
    }

    //自定义业务错误
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result error(CustomException e) {
        return Result.error(e.getCode(),e.getMsg());
    }
}
