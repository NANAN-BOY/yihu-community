package com.yihu.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Question {
    @Id
    private Integer questionId;
    private Integer questionnaireId;
    private String questionTitle;
    private String questionDescription;
    private Boolean questionNullable;
    private String questionType;
    private String details;
}
