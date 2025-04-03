package com.yihu.controller;

import com.github.pagehelper.PageInfo;
import com.yihu.common.AuthAccess;
import com.yihu.common.PaymentService;
import com.yihu.common.ProductType;
import com.yihu.common.Result;
import com.yihu.dto.OrderQueryDTO;
import com.yihu.entity.Business;
import com.yihu.entity.Order;
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
@RequestMapping("/api/order")
public class OrderController {
    private final PaymentService paymentService;
    private final OrderService orderService;

    @GetMapping("/pay/create")
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
    @PostMapping("/pay/notify")
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
    @GetMapping("/query")//“查询订单状态”
    public Result query(@RequestParam String orderNo,
                        @RequestParam(required = false) Boolean forceAlipay) throws Exception {
        // 默认优先查本地数据库
        if (forceAlipay == null || !forceAlipay) {
            Order order = orderService.findByOrderNo(orderNo);
            if (order.getStatus() == 1 || order.getStatus() == 2 || order.getStatus() == 3) {
                return Result.success("支付成功");
            } else {
                return Result.error("支付失败");
            }
        }

        String response = paymentService.queryOrderStatus(orderNo);
        return Result.success(response);
    }

    @GetMapping("/queryOrder")//“查询订单详情”
    public Result queryOrder(@RequestParam String orderNo) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }

        Order order = orderService.findByOrderNo(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getBuyerId().equals(currentUser.getId()) || order.getPayeeId().equals(currentUser.getId())) {
            return Result.success(order);
        } else {
            return Result.error("订单不属于当前用户");
        }
    }

    @PostMapping("/get-myOrderList")//查看“我”生成的订单
    public Result getMyOrderList(@RequestBody OrderQueryDTO orderQueryDTO,
                                 @RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权，请登录");
        }
        PageInfo<Order> pageInfo = orderService.queryOrder(orderQueryDTO, currentUser.getId(), pageNum, pageSize);
        if (pageInfo.getList().isEmpty()) {
            return Result.error(404, "未找到相关订单");
        }
        return Result.success(pageInfo);
    }

    @GetMapping("/get-preemptOrderList")//查看”我“接受的订单
    public Result getPreemptOrderList(@RequestParam(defaultValue = "2") int status,
                                      @RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权，请登录");
        }
        PageInfo<Order> pageInfo = orderService.queryPreemptOrder(currentUser.getId(), status, pageNum, pageSize);
        if (pageInfo.getList().isEmpty()) {
            return Result.error(404, "未找到相关订单");
        }
        return Result.success(pageInfo);
    }

    @GetMapping("/get-business")//查询我购买的服务
    public Result getBusiness(@RequestParam String orderNo) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权，请登录");
        }
        Business business = orderService.getBusiness(orderNo, currentUser.getId());
        if (business == null) {
            return Result.error(404, "未找到相关服务");
        }
        return Result.success(business);
    }

    @GetMapping("/get-myBusiness")//查看“我”接受的服务
    public Result getBusinessList(@RequestParam String orderNo) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权，请登录");
        }
        Business business = orderService.getMyBusiness(orderNo, currentUser.getId());
        if (business == null) {
            return Result.error(404, "未找到相关服务");
        }
        return Result.success(business);
    }

    @GetMapping("/get-businessById")//根据id查询服务
    public Result getBusinessById(@RequestParam Integer id) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权，请登录");
        }
        Business business = orderService.getBusinessById(id, currentUser.getId());
        if (business == null) {
            return Result.error(404, "未找到相关服务");
        }
        return Result.success(business);
    }

    @PostMapping("/finishOrder")//完成订单
    public Result finishOrder(@RequestParam String orderNo) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, "未授权，请登录");
        }
        int isSuccess = orderService.finishOrder(orderNo, currentUser.getId());
        if (isSuccess == 0) {
            orderService.sendMessage(orderNo);
            return Result.success("成功完成订单");
        } else if (isSuccess == -1) {
            return Result.error(404, "订单不存在");
        } else if (isSuccess == -2) {
            return Result.error(401, "身份不正确");
        } else if (isSuccess == 1) {
            return Result.error(403, "服务已被完成");
        } else if (isSuccess == 2) {
            return Result.error(403, "订单已完成");
        }
        return Result.error(500, "完成订单失败");
    }

}
