package com.changgou.search.controller;

import com.changgou.search.service.ESManagerService;
import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/sku_search")
public class SearchController {

    @Autowired
    private SearchService searchService;
    @Autowired
    private ESManagerService esManagerService;

    //对搜索入参带有特殊符号进行处理
    public void handlerSearchMap(Map<String,String> searchMap){
        if (null!=searchMap){
            Set<Map.Entry<String, String>> entries = searchMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if (entry.getKey().startsWith("spec_")){
                    searchMap.put(entry.getKey(),entry.getValue().replace("+","%2B"));
                }
            }
        }
    }

    /**
     * 全文检索
     * @param paramMap
     * @return
     */
    @GetMapping
    public Map search(@RequestParam Map<String,String> paramMap){
        handlerSearchMap(paramMap);
        Map search = searchService.search(paramMap);
        return search;
    }
}
