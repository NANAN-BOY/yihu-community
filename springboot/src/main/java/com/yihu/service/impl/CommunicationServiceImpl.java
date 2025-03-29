package com.yihu.service.impl;

import com.yihu.entity.Communication;
import com.yihu.mapper.CommunicationMapper;
import com.yihu.service.CommunicationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunicationServiceImpl implements CommunicationService {

    @Resource
    private CommunicationMapper communicationMapper;

    @Override
    public Communication add(Communication communication) {
        communicationMapper.insertCommunication(communication);
        return communication;
    }

    @Override
    public List<Communication> findByUsername(String fromUser, String toUser) {
        return communicationMapper.findByUserId(fromUser, toUser);
    }
}
