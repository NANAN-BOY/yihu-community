package com.yihu.entity;

import java.util.Date;

public class ActivityNews {
    private Integer id;
    private Integer activityId;
    private String platform;
    private String link;
    private String delFlag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }


    @Override
    public String toString() {
        return "ActivityNews{" +
                "id=" + id +
                ", activityId=" + activityId +
                ", newsPlatform='" + platform + '\'' +
                ", newsLink='" + link + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
