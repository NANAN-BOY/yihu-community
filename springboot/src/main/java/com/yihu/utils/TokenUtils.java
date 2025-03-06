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
    public static String getToken(String phone, String sign) {
        return JWT.create().withAudience(phone)// 将phone 保存到 token 里面
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2))// 2小时后过期
                .sign(Algorithm.HMAC256(sign));// 用password作为token的密钥
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return user对象
     */
    public static User getCurrentUser() {
        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StringUtils.isNotBlank(token)) {
                String phone = JWT.decode(token).getAudience().get(0);
                return staticUserMapper.login(String.valueOf(phone));
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }
}
