package com.yihu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Communication {
    private Integer id;
    private Integer tempId;
    private Integer businessId;
    private Integer sendUserId;
    private Integer receiveUserId;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date time;
    private Integer status;
}
