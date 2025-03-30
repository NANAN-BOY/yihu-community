package com.yihu.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Business {
    private Integer id;
    private String orderNo;
    private Integer applyUserId;//订单发布者
    private Integer acceptExpertId;//接单专家
    private Integer frequency;//服务次数
    private Date createAt;//创建时间
    private Date endAt;//结束时间

    public Business() {
    }

    public Business(String orderNo, Integer applyUserId, Integer acceptExpertId, Integer frequency, Date createAt) {
        this.orderNo = orderNo;
        this.applyUserId = applyUserId;
        this.acceptExpertId = acceptExpertId;
        this.frequency = frequency;
        this.createAt = createAt;
    }
}
