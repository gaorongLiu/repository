package com.changgou.test;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String,String> map=new HashMap();
        map.put("name","张三");
        int i1 = "name".hashCode();
        System.out.println(i1);
        int i = map.hashCode();
        System.out.println(i);
    }
}
