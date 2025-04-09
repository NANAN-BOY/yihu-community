package com.yihu.entity;

import java.util.Date;

public class Activity {
    private Integer id;
    private String title;
    private String noticeContent;
    private  Integer staffCount;
    private  Integer volunteerCount;
    private  Integer serviceObjectCount;
    private  Integer status;
    private  String delFlag;
    private Date lastUpdateTime;
    private  Integer lastUpdateById;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public Integer getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(Integer staffCount) {
        this.staffCount = staffCount;
    }

    public Integer getVolunteerCount() {
        return volunteerCount;
    }

    public void setVolunteerCount(Integer volunteerCount) {
        this.volunteerCount = volunteerCount;
    }

    public Integer getServiceObjectCount() {
        return serviceObjectCount;
    }

    public void setServiceObjectCount(Integer serviceObjectCount) {
        this.serviceObjectCount = serviceObjectCount;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Date getlastUpdateTime() {
        return lastUpdateTime;
    }

    public void setlastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getlastUpdateById() {
        return lastUpdateById;
    }

    public void setlastUpdateById(Integer lastUpdateById) {
        this.lastUpdateById = lastUpdateById;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", noticeContent='" + noticeContent + '\'' +
                ", staffCount=" + staffCount +
                ", volunteerCount=" + volunteerCount +
                ", serviceObjectCount=" + serviceObjectCount +
                ", status=" + status +
                ", delFlg='" + delFlag + '\'' +
                ", updateTime=" + lastUpdateTime +
                ", updateById=" + lastUpdateById +
                '}';
    }
}
