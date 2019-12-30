package com.changgou.oauth;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class TokenTest {
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testCreateToken() {
        //采用客户端负载均衡，从eureka获取认证服务的ip和端口
        ServiceInstance serviceInstance = loadBalancerClient.choose("user_auth");
        URI uri = serviceInstance.getUri();
        //申请令牌地址
        String authurl = uri + "/oauth/token";
        //1.header信息，包括了http basic认证信息
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        //进行base64编码 并将编码后的认证数据放到文件中
        String httpbasic = httpbasic("changgou", "changgou");
        headers.add("Authorization", httpbasic);
        //2.指定认证类型账号密码
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "password");
        body.add("username", "itheima");
        body.add("password", "123456");
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<MultiValueMap<String, String>>(body, headers);
        //指定restTemplate 当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        //远程调用申请令牌
        ResponseEntity<Map> exchange=restTemplate.exchange(authurl, HttpMethod.POST,multiValueMapHttpEntity,Map.class);
        Map body1 = exchange.getBody();
        System.out.println(body1);
    }

    private String httpbasic(String clientId, String clientsecret) {
        //将客户端id和客户端密码拼接，按“客户端id：客户端密码”
        String String = clientId + ":" + clientsecret;
        //Base64为编码
        byte[] encode = Base64Utils.encode(String.getBytes());
        return "Basic " + new String(encode);
    }
}
