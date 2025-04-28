package com.yihu.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Temp {
    private Integer tempId;
    private String questionTitle;
    private String questionDescription;
    private Boolean questionNullable;
    private String questionType;
    private String details;

    public Temp() {
    }

    public Temp(String questionTitle, String questionDescription, Boolean questionNullable, String questionType, String details) {
        this.questionTitle = questionTitle;
        this.questionDescription = questionDescription;
        this.questionNullable = questionNullable;
        this.questionType = questionType;
        this.details = details;
    }
}
