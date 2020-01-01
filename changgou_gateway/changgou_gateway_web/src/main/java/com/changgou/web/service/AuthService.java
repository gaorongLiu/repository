package com.changgou.web.service;

import org.springframework.http.server.reactive.ServerHttpRequest;

public interface AuthService {

    String getJtiFromcookie(ServerHttpRequest request);

    String getTokenFromRedis(String jti);
}
