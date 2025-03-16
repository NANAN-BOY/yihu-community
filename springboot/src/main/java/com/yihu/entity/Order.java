package com.yihu.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Order {
    private String orderNo;//商户订单号
    private Integer buyerId;//购买者id
    private Integer type;//订单类型
    private Integer paymentType;//支付类型
    private String otherOrderNo;//第三方订单号
    private float paymentAmount;//支付金额
    private Integer payeeId;//收款方id(公司:0 其它:用户id余额)
    private float drawProportion;//抽成比例
    private float receivedAmount;;//应收金额（抽成完后收的金额）
    private Integer status;//订单状态（未支付：0 已支付：1 进行中：2 已完结：3）
    private Date createAt;//创建时间
    private Date payAt;//支付时间
    private Date endAt;//结束时间
    private Integer deleteFlag;
    private Date deleteAt;
    private Integer deleteById;

    public Order(String orderNo, Integer buyerId, Integer type, Integer paymentType, String otherOrderNo,
                 float paymentAmount, Integer payeeId, float drawProportion, float receivedAmount,
                 Integer status, Date createAt, Date payAt, Date endAt, Integer deleteFlag, Date deleteAt, Integer deleteById) {
        this.orderNo = orderNo;
        this.buyerId = buyerId;
        this.type = type;
        this.paymentType = paymentType;
        this.otherOrderNo = otherOrderNo;
        this.paymentAmount = paymentAmount;
        this.payeeId = payeeId;
        this.drawProportion = drawProportion;
        this.receivedAmount = receivedAmount;
        this.status = status;
        this.createAt = createAt;
        this.payAt = payAt;
        this.endAt = endAt;
        this.deleteFlag = deleteFlag;
        this.deleteAt = deleteAt;
        this.deleteById = deleteById;
    }
}
