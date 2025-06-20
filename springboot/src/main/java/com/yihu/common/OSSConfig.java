package com.yihu.common;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSSConfig {

    @Bean
    public OSS ossClient(@Value("${aliyun.oss.endpoint}") String endpoint,
                         @Value("${aliyun.oss.access-key-id}") String accessKeyId,
                         @Value("${aliyun.oss.access-key-secret}") String accessKeySecret) {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
