package com.yihu.mapper;

import com.yihu.entity.InviteExpertRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface ExpertMapper {
    int createInviteRecord(InviteExpertRecord record);

    InviteExpertRecord getRecord(int id);
}
