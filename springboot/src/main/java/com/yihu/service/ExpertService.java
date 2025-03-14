package com.yihu.service;

import com.github.pagehelper.PageInfo;
import com.yihu.entity.InviteExpertRecord;
import com.yihu.entity.User;

import java.util.Date;
import java.util.List;

public interface ExpertService {
    int createInviteRecord(int inviteUserId, Date createAt,Date deadLine);

    InviteExpertRecord getRecord(int id);

    PageInfo<InviteExpertRecord> getHistoryRecord(Integer pageNum, Integer pageSize);

    int deleteRecord(int id);

    int refuse(int id, int isAgree, String reason);

    int accept(int id, int isAgree, int expertId);

    PageInfo<InviteExpertRecord> getCreateRecord(Integer pageNum, Integer pageSize);
}
