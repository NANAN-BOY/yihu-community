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
import java.util.Date;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/alipay")
public class PayController {

    private final Config config;
    private final OrderService orderService; // 注入订单服务

    @GetMapping("/create")
    public String alipay(@RequestParam Integer type, @RequestParam float paymentAmount, @RequestParam Integer status) {
        Factory.setOptions(config);
        User currentUser = TokenUtils.getCurrentUser(); // 获取当前用户信息
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        // 生成唯一订单号
        String orderNo = orderService.generateOrderNo();

        // 创建订单
        boolean createSuccess = orderService.createOrder(orderNo,currentUser.getId(),type,status,paymentAmount);

        if (!createSuccess) {
            throw new RuntimeException("订单创建失败");
        }

        // 调用支付宝接口（金额格式化为两位小数）
        try {
            AlipayTradePrecreateResponse response = Payment.FaceToFace()
                    .preCreate("易互会员", orderNo, String.format("%.2f", paymentAmount));

            if (ResponseChecker.success(response)) {
                // 获取支付宝返回的二维码URL
                String qrUrl = JSONObject.parseObject(response.getHttpBody())
                        .getJSONObject("alipay_trade_precreate_response")
                        .getString("qr_code");

                // 生成二维码（保存或返回前端）
                QrCodeUtil.generate(qrUrl, 300, 300, new File("E://pay.jpg"));

                return response.getHttpBody();
            } else {
                throw new RuntimeException("支付宝预创建失败: " + response.subMsg);
            }
        } catch (Exception e) {
            // 可选：标记订单为失败状态
            throw new RuntimeException("支付异常: " + e.getMessage());
        }
    }

    @AuthAccess
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        // 获取支付宝回调参数
        String orderNo = request.getParameter("out_trade_no");
        String tradeNo = request.getParameter("trade_no"); // 支付宝订单号

        log.info("支付成功通知，订单号: {}, 支付宝单号: {}", orderNo, tradeNo);

        // 根据订单号查询订单
        Order order = orderService.findByOrderNo(orderNo);
        if (order == null) {
            log.error("订单不存在: {}", orderNo);
            return "failure";
        }

        // 幂等性检查：只有未支付订单才处理
        if (order.getStatus() == 0) {
            // 更新订单状态为已支付
            order.setStatus(1);
            order.setPayAt(new Date());
            order.setOtherOrderNo(tradeNo); // 保存支付宝订单号
            order.setPaymentType(1); // 支付方式：支付宝
            orderService.updateOrder(order);

            // TODO 触发业务逻辑（如发货）
            log.info("订单支付成功处理完成: {}", orderNo);
        } else {
            log.warn("订单已处理，忽略重复回调: {}", orderNo);
        }

        return "success"; // 必须返回success告知支付宝已处理
    }

    @AuthAccess
    @GetMapping("/query")
    public String query() throws Exception {
        Factory.setOptions(config);
        AlipayTradeQueryResponse response = Factory.Payment.Common().query("1901270280992219136");
        return response.getHttpBody();
    }

}
