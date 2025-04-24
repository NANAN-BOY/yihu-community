package com.yihu.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Getter
@Setter
public class Questionnaire {
    @Id
    private Integer questionnaireId;
    private Integer userId;
    private Integer activityId;
    private Date createTime;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private String title;
    private Integer fillCount;
    private String description;
    private Integer delete_flag;

    public Questionnaire(Integer activityId, Integer userId, Date createTime, Integer status,
                         String title, Integer fillCount, String description, Integer delete_flag) {
        this.activityId = activityId;
        this.userId = userId;
        this.createTime = createTime;
        this.status = status;
        this.title = title;
        this.fillCount = fillCount;
        this.description = description;
        this.delete_flag = delete_flag;
    }
}
