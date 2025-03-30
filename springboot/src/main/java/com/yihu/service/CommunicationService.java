package com.yihu.service;

import com.yihu.entity.Communication;

import java.util.List;

public interface CommunicationService {
    Communication add(Communication communication);

    List<Communication> findByUsername(Integer businessId,
                                       Integer sendUserId,
                                       Integer receiveUserId);
}
