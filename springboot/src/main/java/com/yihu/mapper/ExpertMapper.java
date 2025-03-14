package com.yihu.mapper;

import com.yihu.entity.InviteExpertRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExpertMapper {
    int createInviteRecord(InviteExpertRecord record);

    InviteExpertRecord getRecord(int id);

    List<InviteExpertRecord> getHistoryRecord();

    Boolean isUserAgree(@Param("id") int id);

    int deleteRecord(@Param("id") int id);

    int refuse(int id, int isAgree, String reason);

    int accept(int id, int isAgree, int expertId);

    List<InviteExpertRecord> getCreateRecord();
}
