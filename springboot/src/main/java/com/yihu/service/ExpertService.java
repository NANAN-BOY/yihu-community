package com.yihu.service;

import com.yihu.entity.InviteExpertRecord;

import java.util.Date;
import java.util.List;

public interface ExpertService {
    int createInviteRecord(int inviteUserId, Date createAt,Date deadLine);

    InviteExpertRecord getRecord(int id);

    List<InviteExpertRecord> getHistoryRecord();
}
