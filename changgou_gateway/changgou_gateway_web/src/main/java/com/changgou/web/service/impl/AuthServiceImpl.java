package com.changgou.web.service.impl;

import com.changgou.web.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 判断cookie中的jti是否存在
     * @param request
     * @return
     */
    @Override
    public String getJtiFromcookie(ServerHttpRequest request) {
        HttpCookie cookie = request.getCookies().getFirst("uid");
        if (cookie!=null){
            return cookie.getValue();
        }
        return null;
    }

    /**
     * 判断redis中的令牌是否过期
     * @param jti
     * @return
     */
    @Override
    public String getTokenFromRedis(String jti) {
        String s = redisTemplate.boundValueOps(jti).get();

        return s;
    }
}
