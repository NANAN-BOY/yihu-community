package com.yihu.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yihu.entity.User;
import com.yihu.mapper.UserMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import cn.hutool.core.date.DateUtil;
import java.util.Date;

@Component
public class TokenUtils {
    private static UserMapper staticUserMapper;

    @Resource
    UserMapper userMapper;

    @PostConstruct
    public void setUserService(){
        staticUserMapper = userMapper;
    }

    /**
     * 生成token
     *
     * @return token
     */
    public static String getToken(String phone, String sign, Integer role) {
        String audience = phone + "|" + role; // 使用"|"分隔符合并
        return JWT.create().withAudience(audience)// 将phone 保存到 token 里面
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2))// 2小时后过期
                .sign(Algorithm.HMAC256(sign));// 用password作为token的密钥
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return user对象
     */
    public static User getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StringUtils.isNotBlank(token)) {
                // 获取合并后的字符串
                String mergedAudience = JWT.decode(token).getAudience().get(0);

                // 分割字符串提取phone和role
                String[] parts = mergedAudience.split("\\|"); // 注意转义竖线
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid token audience format");
                }
                String phone = parts[0];  // 提取phone部分
                Integer role = Integer.parseInt(parts[1]); // 提取role部分

                // 将纯phone传递给login方法
                return staticUserMapper.login(phone);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
