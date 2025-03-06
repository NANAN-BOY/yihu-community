package com.yihu.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor()) // 配置拦截器规则
                .addPathPatterns("/**") ;// 拦截所有请求，通过判断token来获取用户信息
        super.addInterceptors(registry);
    }

    @Bean
    public HandlerInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }
}
