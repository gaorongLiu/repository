package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.common.exception.ExceptionCast;
import com.changgou.common.model.response.Sku.SkuCode;
import com.changgou.common.model.response.search.SearchCode;
import com.changgou.goods.api.PageResult;
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

       // List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
        //分页数据
        PageResult<Sku> pageResult = skuFeign.findSkuPageBySpuId("all", 1);

        if (pageResult == null || pageResult.getTotal() == 0 || pageResult.getPages() == 0) {
            ExceptionCast.cast(SearchCode.SEARCH_FOR_NULL);
        }
        //总页数
        Integer pages = pageResult.getPages();
        for (int i = 1; i <=pages ; i++) {
            List<Sku> skuList = skuFeign.findSkuPageBySpuId("all", i).getRows();

            //skulist转化为json
            String s = JSON.toJSONString(skuList);
            //将json转换成skuinfo方便封装
            List<SkuInfo> skuInfos = JSON.parseArray(s, SkuInfo.class);
            for (SkuInfo skuInfo : skuInfos) {
                Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
                skuInfo.setSpecMap(map);
            }
            esManagerMapper.saveAll(skuInfos);
            System.out.println("导入"+i+"页");
        }

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
        List<Sku> skuListBySpuId = skuFeign.findSkuListBySpuId(spuId);
        if (skuListBySpuId.isEmpty() || skuListBySpuId.size()<0){
            ExceptionCast.cast(SkuCode.SKU_FIND_EMPTY);
        }
        for (Sku sku : skuListBySpuId) {
            esManagerMapper.deleteById(Long.valueOf(sku.getId()));
        }

    }
    //创建索引库结构

}
