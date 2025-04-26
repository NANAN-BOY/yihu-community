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
    private Date createTime;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Integer fillCount;
    private Integer deleteFlag;

    public Questionnaire(Date createTime, Integer status,
                         Integer fillCount, Integer deleteFlag) {
        this.createTime = createTime;
        this.status = status;
        this.fillCount = fillCount;
        this.deleteFlag = deleteFlag;
    }
}
