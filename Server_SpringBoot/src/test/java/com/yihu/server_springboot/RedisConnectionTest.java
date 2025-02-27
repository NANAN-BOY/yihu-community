package com.yihu.server_springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisConnectionTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testRedisConnection() {
        // 测试 Redis 是否能正常工作
        String key = "testConnection";
        String value = "Hello Redis";

        // 尝试设置一个值
        redisTemplate.opsForValue().set(key, value);

        // 从 Redis 获取该值
        String result = redisTemplate.opsForValue().get(key);

        // 验证是否正确保存并读取
        System.out.println(result);
    }
}
