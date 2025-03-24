package com.yihu.common;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentService {

    private final Config config;

    /**
     * 生成预支付订单
     *
     * @param productName   商品名称
     * @param orderNo       订单号
     * @param paymentAmount 支付金额
     * @return 预支付订单响应信息
     */
    public String createOrder(String productName, String orderNo, float paymentAmount) {
        Factory.setOptions(config);
        try {
            AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace()
                    .preCreate(productName, orderNo, String.format("%.2f", paymentAmount));

            if (ResponseChecker.success(response)) {
                String qrUrl = JSONObject.parseObject(response.getHttpBody())
                        .getJSONObject("alipay_trade_precreate_response")
                        .getString("qr_code");

//                QrCodeUtil.generate(qrUrl, 300, 300, new File("E://pay.jpg"));

                return response.getHttpBody();
            } else {
                log.error("支付宝预创建失败: {}", response.subMsg);
                throw new RuntimeException("支付宝预创建失败: " + response.subMsg);
            }
        } catch (Exception e) {
            log.error("支付异常: {}", e.getMessage());
            throw new RuntimeException("支付异常: " + e.getMessage());
        }
    }

    /**
     * 查询订单支付状态
     *
     * @param orderNo 订单号
     * @return 订单查询响应信息
     * @throws Exception 异常信息
     */
    public String queryOrderStatus(String orderNo) throws Exception {
        Factory.setOptions(config);
        AlipayTradeQueryResponse response = Factory.Payment.Common().query(orderNo);
        return response.getHttpBody();
    }

}
