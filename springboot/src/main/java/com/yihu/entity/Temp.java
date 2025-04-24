package com.yihu.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Temp {
    @Id
    private Integer tempId;
    private String questionTitle;
    private String questionDescription;
    private Boolean questionNullable;
    private String questionType;
    private String details;
}
