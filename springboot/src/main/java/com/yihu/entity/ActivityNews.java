package com.yihu.entity;

import java.util.Date;

public class ActivityNews {
    private Integer id;
    private Integer activityId;
    private String newsPlatform;
    private String newsLink;
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

    public String getNewsPlatform() {
        return newsPlatform;
    }

    public void setNewsPlatform(String newsPlatform) {
        this.newsPlatform = newsPlatform;
    }

    public String getNewsLink() {
        return newsLink;
    }

    public void setNewsLink(String newsLink) {
        this.newsLink = newsLink;
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
                ", newsPlatform='" + newsPlatform + '\'' +
                ", newsLink='" + newsLink + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
