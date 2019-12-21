package com.changgou;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;

public class test {
    @Test
    public void test(){
        long l = System.currentTimeMillis()+100000;
        Date date = new Date(l);
        JwtBuilder builder = Jwts.builder()
                .setId("888")  //设置唯一编号
                .setSubject("小白")  //设置主题
                .setIssuedAt(new Date())  //设置签发日期
                .setExpiration(date) //设置过期时间
                .claim("roles","admin")//设置角色
                //载荷
                .signWith(SignatureAlgorithm.HS256,"itcast");//设置签名，使用HS256算法并设置SecretKey（字符串）
                //构建 并返回一个字符串
        System.out.println(builder.compact());


    }
    @Test
    public void test2(){
        String compactJwt="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJpYXQiOjE1NzY4NDg3OTMsImV4cCI6MTU3Njg0ODg5Mywicm9sZXMiOiJhZG1pbiJ9.Uwa_QQkJDTxcFRuDjrWJjbAqCtqZyxZsMbICnmoDaCc";
        Claims itcast = Jwts.parser().setSigningKey("itcast").parseClaimsJws(compactJwt).getBody();
        System.out.println(itcast);
    }
}
