package com.changgou.search.service;

import java.util.Map;

public interface SearchService {
    /**
     * 商品搜索页面数据
     * @param paramMap
     * @return
     * @throws Exception
     */
    public Map search(Map<String, String> paramMap);

}
