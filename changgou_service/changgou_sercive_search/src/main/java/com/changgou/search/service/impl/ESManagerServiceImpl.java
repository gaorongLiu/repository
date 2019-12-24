package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.common.exception.ExceptionCast;
import com.changgou.common.model.response.search.SearchCode;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ESManagerServiceImpl implements ESManagerService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ESManagerMapper esManagerMapper;


    /**
     * 创建索引库
     */
    @Override
    public void createMappingAndIndex() {
        elasticsearchTemplate.createIndex(SkuInfo.class);
        //创建mapping
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    /**
     * 全部分页导入
     */
    @Override
    public void importAll() {

        List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
        String skuCount = skuFeign.findSkuCount();
        //查询sku总数据
        int skuNum = Integer.valueOf(skuCount);
        //一次上传多少条
        int pagecount= (skuNum%2000) == 0 ? (skuNum/2000) : (skuNum/2000) + 1;

        if (skuList == null || skuList.size() <= 0) {
            ExceptionCast.cast(SearchCode.SEARCH_FOR_NULL);
        }
        //skulist转化为json
        String s = JSON.toJSONString(skuList);
        //将json转换未skuinfo方便封装
        List<SkuInfo> skuInfos = JSON.parseArray(s, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos) {
            //将规格信息转换为map
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        //导入索引库
        esManagerMapper.saveAll(skuInfos);
    }

    //根据spuid查询skuList,添加到索引库
    @Override
    public void importDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if (skuList == null || skuList.size() <= 0) {
            ExceptionCast.cast(SearchCode.SEARCH_FOR_NULL);
        }
        //将集合转换为json
        String jsonSkuList = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            //将规格信息进行转换
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }
        esManagerMapper.saveAll(skuInfoList);
    }

    @Override
    public void delDataBySpuId(String spuId) {

    }

    //创建索引库结构

}
