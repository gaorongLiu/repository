package com.changgou.web.gateway.filter;

import com.changgou.web.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 身份认证过滤
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    public static final String Authorization="Authorization";

    @Autowired
    private AuthService authService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取当前请求
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //访问相对路径
        String path = request.getURI().getPath();
        //登录路径直接放行
        if ("/api/oauth/login".equals(path) || "/api/oauth/toLogin".equals(path)){
            return chain.filter(exchange);
        }
        //判断cookie是否存在jti
        String jti=authService.getJtiFromcookie(request);
        if (StringUtils.isEmpty(jti)){
            //拒绝访问.请求转跳
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //判断redis中token是否存在
        String redisTokn=authService.getTokenFromRedis(jti);
        if (StringUtils.isEmpty(redisTokn)){
            //拒接访问.请求转跳
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //通过，请求头增强。放行
        request.mutate().header(Authorization,"Bear "+redisTokn);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
