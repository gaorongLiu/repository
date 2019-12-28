package com.changgou.search.controller;

import com.changgou.common.entity.Page;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.ESManagerService;
import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/search")
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
    @ResponseBody
    public Map search(@RequestParam Map<String,String> paramMap){
        handlerSearchMap(paramMap);
        Map search = searchService.search(paramMap);
        return search;
    }

    /**
     * 页面服务
     * @param model
     * @param searchMap
     * @return
     */
    @GetMapping("/list")
    public String search(Model model,@RequestParam Map<String, String> searchMap){

        //去除特殊符号
        handlerSearchMap(searchMap);
        //请求的map数据，输入值的回显
        model.addAttribute("searchMap",searchMap);
        //后台返回的数据，请求的数据显示
        Map resultMap = searchService.search(searchMap);
        model.addAttribute("resultMap",resultMap);
        //拼接url
        StringBuilder url=new StringBuilder("/search/list");
        if (searchMap!=null && searchMap.size()>0 ){
            url.append("?");
            for (String key : searchMap.keySet()) {
                //搜索条件不为分页、排序信息
                if (!"sortRule".equals(key) && !"sortField".equals(key) && !"pageNum".equals(key)){
                    url.append(key).append("=").append(searchMap.get(key)).append("&");
                    //http://localhost:9009/search/list?keywords=手机&spec_网络制式=4G&
                    String urlString = url.toString();
                    //去掉最后一个&
                    urlString=urlString.substring(0,urlString.length()-1);
                    model.addAttribute("url",urlString);
                }else {
                    model.addAttribute("url",url);
                }
            }
        }
        //分页
        Page<SkuInfo> page = new Page<SkuInfo>(
                Long.parseLong(String.valueOf( resultMap.get("total"))),
                Integer.parseInt(String.valueOf(resultMap.get("pageNum"))),
                Page.pageSize
        );
        model.addAttribute("page",page);
        return "search";

    }

}
