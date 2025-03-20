package com.yihu.service.impl;

import com.yihu.entity.MemberShip;
import com.yihu.mapper.MemberShipMapper;
import com.yihu.service.MemberShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberShipServiceImpl implements MemberShipService {

    private final MemberShipMapper memberShipMapper;

    @Autowired
    public MemberShipServiceImpl(MemberShipMapper memberShipMapper) {
        this.memberShipMapper = memberShipMapper;
    }

    @Override
    public MemberShip isMemberValid(Integer userId) {
        return memberShipMapper.selectByUserID(userId);
    }

}
