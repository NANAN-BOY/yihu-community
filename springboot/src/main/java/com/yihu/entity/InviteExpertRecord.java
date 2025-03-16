package com.yihu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
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
