package com.yihu.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.yihu.common.AuthAccess;
import com.yihu.entity.Order;
import com.yihu.entity.User;
import com.yihu.service.OrderService;
import com.yihu.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.factory.Factory.Payment;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/alipay")
public class PayController {

    private final Config config;
    private final OrderService orderService;

    @GetMapping("/create")
    public String alipay(@RequestParam Integer type) {
        Factory.setOptions(config);
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        // 根据类型确定商品名称和价格
        String productName;
        float paymentAmount;
        if (type == 1) {
            productName = "一个月易互会员";
            paymentAmount = 15.00f;
        } else if (type == 2) {
            productName = "包年易互会员";
            paymentAmount = 120.00f;
        } else {
            throw new RuntimeException("无效的商品类型");
        }

        String orderNo = orderService.generateOrderNo();

        boolean createSuccess = orderService.createOrder(orderNo, currentUser.getId(), type, 0, paymentAmount);

        if (!createSuccess) {
            throw new RuntimeException("订单创建失败");
        }

        try {
            AlipayTradePrecreateResponse response = Payment.FaceToFace()
                    .preCreate(productName, orderNo, String.format("%.2f", paymentAmount));

            if (ResponseChecker.success(response)) {
                String qrUrl = JSONObject.parseObject(response.getHttpBody())
                        .getJSONObject("alipay_trade_precreate_response")
                        .getString("qr_code");

                QrCodeUtil.generate(qrUrl, 300, 300, new File("E://pay.jpg"));

                return response.getHttpBody();
            } else {
                throw new RuntimeException("支付宝预创建失败: " + response.subMsg);
            }
        } catch (Exception e) {
            throw new RuntimeException("支付异常: " + e.getMessage());
        }
    }

    @AuthAccess
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        String orderNo = request.getParameter("out_trade_no");
        String tradeNo = request.getParameter("trade_no");

        log.info("支付成功通知，订单号: {}, 支付宝单号: {}", orderNo, tradeNo);

        Order order = orderService.findByOrderNo(orderNo);
        if (order == null) {
            log.error("订单不存在: {}", orderNo);
            return "failure";
        }

        if (order.getStatus() == 0) {
            order.setStatus(1);
            order.setPayAt(new Date());
            order.setOtherOrderNo(tradeNo);
            order.setPaymentType(1);

            // 计算会员截止日期
            Date payAt = order.getPayAt();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(payAt);

            if (order.getType() == 1) {
                calendar.add(Calendar.MONTH, 1);
            } else if (order.getType() == 2) {
                calendar.add(Calendar.YEAR, 1);
            }

            order.setEndAt(calendar.getTime());

            orderService.updateOrder(order);

            log.info("订单支付成功处理完成: {}", orderNo);
        } else {
            log.warn("订单已处理，忽略重复回调: {}", orderNo);
        }

        return "success";
    }

    @AuthAccess
    @GetMapping("/query")
    public String query() throws Exception {
        Factory.setOptions(config);
        AlipayTradeQueryResponse response = Factory.Payment.Common().query("1901270280992219136");
        return response.getHttpBody();
    }
}