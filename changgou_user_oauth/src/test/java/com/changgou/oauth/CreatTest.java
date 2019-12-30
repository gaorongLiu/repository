package com.changgou.oauth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateCrtKey;
import java.util.HashMap;
import java.util.Map;

public class CreatTest {
    @Test
    public void test(){
        //证书文件路径
       String  key_location="changgou";
       //密钥库密码
        String key_password="changgou";
        //密钥密码
        String keypwd="changgou";
        //密钥别名
        String alias="changgou";

        //访问证书路径
        ClassPathResource resource=new ClassPathResource("changgou.jks");

        //创建密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory=new KeyStoreKeyFactory(resource,key_password.toCharArray());
        //通过工厂读取密钥对
        KeyPair keyPair=keyStoreKeyFactory.getKeyPair(alias,keypwd.toCharArray());
        //获取私钥
        RSAPrivateCrtKey rsaPrivateCrtKey=(RSAPrivateCrtKey)keyPair.getPrivate();
        //定义payload
        Map<String,Object> tokenMap =new HashMap<>();
        tokenMap.put("id","1");
        tokenMap.put("name","itheima");
        tokenMap.put("roles","ROLE_VIP,ROLE_USER");
        //生成jwt令牌
        Jwt encode = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(rsaPrivateCrtKey));
        //取出令牌
        String claims = encode.getClaims();
        System.out.println(claims);

    }
}
