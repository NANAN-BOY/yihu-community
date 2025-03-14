package com.yihu.common;

import com.alipay.easysdk.kernel.Config;
import com.yihu.utils.AliPayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliPayConfig {

    @Bean
    public Config config(AliPayProperties payProperties){
        Config config = new Config();
        config.protocol = payProperties.getProtocol();
        config.gatewayHost = payProperties.getGatewayHost();
        config.signType = payProperties.getSignType();
        config.appId = payProperties.getAppId();
        config.merchantPrivateKey = payProperties.getMerchantPrivateKey();
        config.alipayPublicKey = payProperties.getAlipayPublicKey();
        config.notifyUrl = payProperties.getNotifyUrl();
        config.encryptKey = "";
        return config;
    }
}
