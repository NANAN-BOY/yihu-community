package com.yihu.dto;

import lombok.Data;

@Data
public class TempDTO {
    private String questionTitle;
    private String questionDescription;
    private Boolean questionNullable;
    private String questionType;
    private String details;
}
