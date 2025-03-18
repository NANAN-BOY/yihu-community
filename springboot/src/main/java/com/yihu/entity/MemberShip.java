package com.yihu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class MemberShip {
    private Integer memberShipId;
    private String orderNo;
    private Integer userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date buyDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date deadline;
    private Integer grade;

    public MemberShip(String orderNo, Integer userId, Date buyDate, Date deadline, Integer grade) {
        this.orderNo = orderNo;
        this.userId = userId;
        this.buyDate = buyDate;
        this.deadline = deadline;
        this.grade = grade;
    }
}
