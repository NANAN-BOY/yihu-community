package com.yihu.mapper;

import com.yihu.entity.InviteExpertRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ExpertMapper {
    int createInviteRecord(InviteExpertRecord record);

    InviteExpertRecord getRecord(@Param("id") int id);

    List<InviteExpertRecord> getHistoryRecord();

    Boolean isUserAgree(@Param("id") int id);

    int deleteRecord(@Param("id") int id);

    int refuse(@Param("id") int id,@Param("isAgree") int isAgree,@Param("reason") String reason);

    int accept(@Param("id") int id,@Param("isAgree") int isAgree,@Param("expertId") int expertId);

    List<InviteExpertRecord> getCreateRecord();

    List<InviteExpertRecord> getByTime(@Param("startTime") Date startTime,@Param("endTime") Date endTime);
}
