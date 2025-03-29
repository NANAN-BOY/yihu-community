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
    public void setUserService() {
        staticUserMapper = userMapper;
    }

    /**
     * 生成 Token（包含用户ID、手机号和角色）
     *
     * @param id    用户ID
     * @param phone 手机号
     * @param sign  签名密钥
     * @param role  角色
     * @return Token字符串
     */
    public static String getToken(Integer id, String phone, String sign, Integer role) {
        String audience = id + "|" + phone + "|" + role; // 合并ID、phone、role
        return JWT.create()
                .withAudience(audience)
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) // 2小时后过期
                .sign(Algorithm.HMAC256(sign)); // 用 sign 作为密钥
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return User对象
     */
    public static User getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StringUtils.isNotBlank(token)) {
                // 从 Token 中提取合并的字符串
                String mergedAudience = JWT.decode(token).getAudience().get(0);

                // 分割字符串提取 id、phone、role
                String[] parts = mergedAudience.split("\\|");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Token 格式无效");
                }

                Integer id = Integer.parseInt(parts[0]);    // 用户ID
                String phone = parts[1];                   // 手机号
                Integer role = Integer.parseInt(parts[2]); // 角色

                return staticUserMapper.login(phone);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}