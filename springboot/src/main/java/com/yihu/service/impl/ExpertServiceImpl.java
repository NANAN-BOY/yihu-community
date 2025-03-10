package com.yihu.service.impl;

import com.yihu.entity.InviteExpertRecord;
import com.yihu.mapper.ExpertMapper;
import com.yihu.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    @Override
    public List<InviteExpertRecord> getHistoryRecord() {
        return expertMapper.getHistoryRecord();
    }

    @Override
    public int deleteRecord(int id) {
        Boolean isAgree = expertMapper.isUserAgree(id);
        if (!isAgree){
            int isSuccess = expertMapper.deleteRecord(id);
            if (isSuccess > 0) {
                return 0;//取消成功
            }else {
                return -2;//取消失败
            }
        }else {
            return -1;//已经有同意或者拒绝的记录无法被删除
        }
    }

    @Override
    public int refuse(int id, int isAgree, String reason) {
        int isSuccess = expertMapper.refuse(id,isAgree,reason);
        if (isSuccess > 0) {
            return 0;//拒绝成功
        }else {
            return -1;//拒绝失败
        }
    }

    @Override
    @Transactional
    public int accept(int id, int isAgree, int expertId) {
        int isSuccess = expertMapper.accept(id,isAgree,expertId);
        if (isSuccess > 0) {
            return 0;//同意成功
        }else {
            return -1;//同意失败
        }
    }
}
