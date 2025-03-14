package com.yihu.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.yihu.common.AuthAccess;
import jakarta.servlet.http.HttpServletRequest;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.factory.Factory.Payment;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@Slf4j
@AllArgsConstructor
public class PayController {

    private final Config config;

    @AuthAccess
    @GetMapping("/alipay")
    public String alipay() throws Exception{
        Factory.setOptions(config);

        AlipayTradePrecreateResponse response;
        try {
            // 2. 发起API调用（以创建当面付收款二维码为例）
            response = Payment.FaceToFace()
                    .preCreate("易互会员", "1213131313133", "100.00");
            // 3. 处理响应或异常
            if (ResponseChecker.success(response)) {
                log.info("调用成功");
            } else {
                System.err.println("调用失败，原因：" + response.msg + "，" + response.subMsg);
            }
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        //解析结果
        String httpBody = response.getHttpBody();
        //转JSON对象
        JSONObject jsonObject = JSONObject.parseObject(httpBody);
        //log.info(JSONObject.toJSONString(jsonObject));
        String qrUrl = jsonObject.getJSONObject("alipay_trade_precreate_response").get("qr_code").toString();
        //生成二维码
        QrCodeUtil.generate(qrUrl,300,300,new File("E://pay.jpg"));
        return httpBody;
    }

    @AuthAccess
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){
        log.info("收到支付成功通知");
        String out_trade_no = request.getParameter("out_trade_no");
        log.info("流水号：{}",out_trade_no);
        //TODO 后续业务流程 发货
        //回调了多次怎么办？  考虑接口的幂等性    分布式锁 乐观锁  先判断再处理
        return "success";
    }

    @AuthAccess
    @GetMapping("/query")
    public String query() throws Exception {
        Factory.setOptions(config);
        AlipayTradeQueryResponse response = Factory.Payment.Common().query("1213131313131");
        return response.getHttpBody();
    }
}
