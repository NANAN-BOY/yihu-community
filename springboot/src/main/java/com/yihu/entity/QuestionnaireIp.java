package com.yihu.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class QuestionnaireIp {
    @Id
    private Integer id;
    private Integer questionnaireId;
    private String ip;
}
