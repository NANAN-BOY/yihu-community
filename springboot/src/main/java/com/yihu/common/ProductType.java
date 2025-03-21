package com.yihu.common;

import lombok.Getter;

@Getter
public enum ProductType {
    ONE_MONTH_MEMBERSHIP(1, "一个月易互会员", 15.00f),
    ANNUAL_MEMBERSHIP(2, "包年易互会员", 120.00f),
    EXPERT_SERVICE(0, "专家定制服务", 2000.00f);

    private final int type;
    private final String productName;
    private final float paymentAmount;

    ProductType(int type, String productName, float paymentAmount) {
        this.type = type;
        this.productName = productName;
        this.paymentAmount = paymentAmount;
    }

    public static ProductType getByType(int type) {
        for (ProductType productType : values()) {
            if (productType.type == type) {
                return productType;
            }
        }
        return null;
    }
}
