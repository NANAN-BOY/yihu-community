package com.yihu.mapper;


import com.yihu.entity.Communication;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommunicationMapper {

    List<Communication> findByUserId(@Param("sendUserId") String sendUserId,
                                     @Param("receiveUserId") String receiveUserId);

    void insertCommunication(Communication communication);
}
