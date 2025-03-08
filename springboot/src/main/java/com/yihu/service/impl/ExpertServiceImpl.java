package com.yihu.service.impl;

import com.yihu.entity.InviteExpertRecord;
import com.yihu.mapper.ExpertMapper;
import com.yihu.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ExpertServiceImpl implements ExpertService {
    private final ExpertMapper expertMapper;

    @Autowired
    public ExpertServiceImpl(ExpertMapper expertMapper) {
        this.expertMapper = expertMapper;
    }

    @Override
    public int createInviteRecord(int inviteUserId, Date createAt, Date deadLine) {
        try {
            InviteExpertRecord record = new InviteExpertRecord();
            record.setInviteUserId(inviteUserId);
            record.setCreateAt(createAt);
            record.setDeadline(deadLine);

            expertMapper.createInviteRecord(record);
            return record.getId();
        } catch (Exception e) {
            e.printStackTrace(); // 添加异常打印
            return -1;
        }
    }

    @Override
    public InviteExpertRecord getRecord(int id) {
        return expertMapper.getRecord(id);
    }
}
