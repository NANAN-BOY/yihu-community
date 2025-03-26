package com.yihu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yihu.entity.Business;
import com.yihu.entity.InviteExpertRecord;
import com.yihu.entity.Order;
import com.yihu.mapper.BusinessMapper;
import com.yihu.mapper.ExpertMapper;
import com.yihu.mapper.OrderMapper;
import com.yihu.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class ExpertServiceImpl implements ExpertService {
    private final ExpertMapper expertMapper;
    private final OrderMapper orderMapper;
    private final BusinessMapper businessMapper;

    @Autowired
    public ExpertServiceImpl(ExpertMapper expertMapper, OrderMapper orderMapper, BusinessMapper businessMapper) {
        this.expertMapper = expertMapper;
        this.orderMapper = orderMapper;
        this.businessMapper = businessMapper;
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
    public PageInfo<InviteExpertRecord> getHistoryRecord(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(expertMapper.getHistoryRecord());
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

    @Override
    public PageInfo<InviteExpertRecord> getCreateRecord(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(expertMapper.getCreateRecord());
    }

    @Override
    public PageInfo<InviteExpertRecord> getByTime(Integer pageNum, Integer pageSize, Date startTime, Date endTime) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(expertMapper.getByTime(startTime,endTime));
    }

    @Override
    public PageInfo<Order> getOrderList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(orderMapper.getOrderList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int preemptOrder(String orderNo, int buyerId, Integer expertId) {
        Date currentDate = new Date(); // 获取当前日期时间
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order.getStatus() != 1) {
            return -2;//订单已被接受
        }
        int isSuccess = orderMapper.updateExpertOrder(
                orderNo,
                expertId,
                order.getStatus() + 1,//新状态＝旧状态+1
                order.getStatus());//旧状态
        if (isSuccess > 0) {
            Business business = new Business(orderNo, buyerId, expertId, 1, currentTimestamp);
            int isBusiness = businessMapper.insertBusiness(business);
            if (isBusiness > 0) {
                return 0;//成功
            } else {
                return -1;//失败
            }
        } else {
            return -2;//失败
        }
    }
}
