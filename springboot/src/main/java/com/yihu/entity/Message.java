package com.yihu.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Message {
    private Integer id;
    private Integer businessId;
    private Integer sendUserId;
    private Integer receiveUserId;
    private String content;
    private Date time;
}