package com.yihu.controller;

import com.yihu.common.AuthAccess;
import com.yihu.common.PaymentService;
import com.yihu.common.ProductType;
import com.yihu.common.Result;
import com.yihu.entity.User;
import com.yihu.service.OrderService;
import com.yihu.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/pay")
public class PayController {
    private final PaymentService paymentService;
    private final OrderService orderService;

    @GetMapping("/create")
    public String createOrder(@RequestParam Integer type) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        ProductType productType = ProductType.getByType(type);
        if (productType == null) {
            throw new RuntimeException("无效的商品类型");
        }

        String productName = productType.getProductName();
        float paymentAmount = productType.getPaymentAmount();

        String orderNo = orderService.generateOrderNo();

        boolean createSuccess = orderService.createOrder(orderNo, currentUser.getId(), type, 0, paymentAmount);

        if (!createSuccess) {
            throw new RuntimeException("订单创建失败");
        }

        return paymentService.createOrder(productName, orderNo, paymentAmount);
    }

    @AuthAccess
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        String orderNo = request.getParameter("out_trade_no");
        String tradeNo = request.getParameter("trade_no");

        log.info("支付成功通知，订单号: {}, 支付宝单号: {}", orderNo, tradeNo);

        boolean updateSuccess = orderService.updateOrder(orderNo, tradeNo);

        if (updateSuccess) {
            log.info("订单支付成功处理完成: {}", orderNo);
            return "success";
        } else {
            log.error("订单不存在或处理失败: {}", orderNo);
            return "failure";
        }
    }

    @AuthAccess
    @GetMapping("/query")
    public Result query(@RequestParam String orderNo,
                        @RequestParam(required = false) Boolean forceAlipay) throws Exception {
        // 默认优先查本地数据库
        if (forceAlipay == null || !forceAlipay) {
            Object order = orderService.findByOrderNo(orderNo);
            if (order != null) {
                return Result.success(order);
            }
        }

        String response = paymentService.queryOrderStatus(orderNo);
        return Result.success(response);
    }


}
