package com.yihu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class InviteExpertRecord {
    private Integer id;
    private Integer inviteUserId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date deadline;
    private Integer isAgree;
    private Integer expertId;
    private String refuseReason;

    public InviteExpertRecord() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInviteUserId() {
        return inviteUserId;
    }

    public void setInviteUserId(Integer inviteUserId) {
        this.inviteUserId = inviteUserId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(Integer isAgree) {
        this.isAgree = isAgree;
    }

    public Integer getExpertId() {
        return expertId;
    }

    public void setExpertId(Integer expertId) {
        this.expertId = expertId;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public InviteExpertRecord(Integer id, Integer inviteUserId, Date createAt, Date deadline, Integer isAgree, Integer expertId, String refuseReason) {
        this.id = id;
        this.inviteUserId = inviteUserId;
        this.createAt = createAt;
        this.deadline = deadline;
        this.isAgree = isAgree;
        this.expertId = expertId;
        this.refuseReason = refuseReason;
    }

    public InviteExpertRecord(Integer inviteUserId, Date createAt, Date deadline) {
        this.inviteUserId = inviteUserId;
        this.createAt = createAt;
        this.deadline = deadline;
    }
}
