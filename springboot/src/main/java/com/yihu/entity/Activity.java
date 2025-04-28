package com.yihu.entity;

import java.util.Date;

public class Activity {
    private Integer id;
    private Integer projectId;
    private String title;
    private String noticeContent;
    private  Integer staffCount;
    private  Integer volunteerCount;
    private  Integer serviceObjectCount;
    private Integer questionnaireId;
    private  Integer status;
    private  String delFlag;
    private Date createTime;
    private Integer createById;
    private Date updateTime;
    private  Integer updateById;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
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

    public Integer getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Integer questionnaireId) {
        this.questionnaireId = questionnaireId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateById() {
        return createById;
    }

    public void setCreateById(Integer createById) {
        this.createById = createById;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateById() {
        return updateById;
    }

    public void setUpdateById(Integer updateById) {
        this.updateById = updateById;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", title='" + title + '\'' +
                ", noticeContent='" + noticeContent + '\'' +
                ", staffCount=" + staffCount +
                ", volunteerCount=" + volunteerCount +
                ", serviceObjectCount=" + serviceObjectCount +
                ", questionnaireId=" + questionnaireId +
                ", status=" + status +
                ", delFlg='" + delFlag + '\'' +
                ", createTime=" + createTime +
                ", createById=" + createById +
                ", updateTime=" + updateTime +
                ", updateById=" + updateById +
                '}';
    }
}
