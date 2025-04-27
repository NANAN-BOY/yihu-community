package com.yihu.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AnswerDTO {
    private Integer questionId;
    private String questionTitle;
    private String questionType;
    private String writeValue;
    private Date fillTime;

    public AnswerDTO(Integer questionId, String questionTitle, String questionType, String writeValue, Date fillTime) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionType = questionType;
        this.writeValue = writeValue;
        this.fillTime = fillTime;
    }
}
