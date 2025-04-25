package com.yihu.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Setter
@Getter
public class QuestionnaireIp {
    @Id
    private Integer id;
    private Integer questionnaireId;
    private String ip;

    public QuestionnaireIp(Integer questionnaireId, String ip) {
        this.questionnaireId = questionnaireId;
        this.ip = ip;
    }
}
