package com.yihu.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.yihu.entity.User;
import com.yihu.exception.ServiceException;
import com.yihu.mapper.UserMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {
    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            token = request.getParameter("token");
        }

        // 检查是否存在放行注解
        if (handler instanceof HandlerMethod) {
            AuthAccess authAccess = ((HandlerMethod) handler).getMethodAnnotation(AuthAccess.class);
            if (authAccess != null) {
                return true;
            }
        }

        // Token 为空直接拦截
        if (StringUtils.isBlank(token)) {
            throw new ServiceException("401", "请登录");
        }

        // 解析 Token 并提取 phone 和 role
        String mergedAudience;
        try {
            mergedAudience = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException e) {
            throw new ServiceException("401", "Token解析失败");
        }

        // 分割合并的字符串
        String[] parts = mergedAudience.split("\\|"); // 转义竖线
        if (parts.length != 2) {
            throw new ServiceException("401", "Token格式错误");
        }
        String phone = parts[0];
        Integer role = Integer.parseInt(parts[1]); // 提取 role（根据需求决定是否使用）

        // 查询用户
        User user = userMapper.login(phone);
        if (user == null) {
            throw new ServiceException("401", "用户不存在");
        }

        // 验证 Token 签名
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new ServiceException("401", "Token无效或已过期");
        }

        return true;
    }
}
