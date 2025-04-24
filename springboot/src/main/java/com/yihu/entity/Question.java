package com.yihu.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Question {
    @Id
    private Integer questionId;
    private Integer questionnaireId;
    private String questionTitle;
    private String questionDescription;
    private Boolean questionNullable;
    private String questionType;
    private String details;

    public Question(Integer questionnaireId, String questionTitle, String questionDescription,
                    Boolean questionNullable, String questionType, String details) {
        this.questionnaireId = questionnaireId;
        this.questionTitle = questionTitle;
        this.questionDescription = questionDescription;
        this.questionNullable = questionNullable;
        this.questionType = questionType;
        this.details = details;
    }
}
