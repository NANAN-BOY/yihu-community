package com.yihu.mapper;


import com.yihu.entity.Communication;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommunicationMapper {

    List<Communication> findByUserId(@Param("businessId") Integer businessId,
                                     @Param("sendUserId") Integer sendUserId,
                                     @Param("receiveUserId") Integer receiveUserId);

    void insertCommunication(Communication communication);
}
