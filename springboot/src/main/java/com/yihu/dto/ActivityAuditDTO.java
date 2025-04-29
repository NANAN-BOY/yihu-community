package com.yihu.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ActivityAuditDTO {
    private Integer id;
    private Integer projectId;
    private String projectName;
    private Integer status;
    private String title;
    private Integer createById;
    private String createByName;
    private Date createTime;
    private Date updateTime;
}
