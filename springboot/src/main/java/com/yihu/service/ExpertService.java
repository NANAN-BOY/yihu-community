package com.yihu.service;

import java.util.Date;

public interface ExpertService {
    int createInviteRecord(int inviteUserId, Date createAt,Date deadLine);
}
