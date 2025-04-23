package com.yihu.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Answer {
    @Id
    private Integer answerId;
    private Integer questionId;
    private String questionTitle;
    private String questionType;
    private String writeValue;
    private String ip;
    private Date fillTime;
}
