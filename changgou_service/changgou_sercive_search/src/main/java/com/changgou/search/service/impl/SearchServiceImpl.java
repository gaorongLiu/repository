package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
@Autowired
private ElasticsearchTemplate estemp;

//设置每页查询条数据
public final static Integer PAGE_SIZE = 20;

    /**
     * 商品全局搜索
     * @param paramMap
     * @return
     * @throws Exception
     */
    @Override
    public Map search(Map<String, String> paramMap)  {
        Map<String, Object> resultMap = new HashMap<>();

        //有条件才查询es
        if (paramMap!=null){
            //复合查询
            BoolQueryBuilder boolQuery= QueryBuilders.boolQuery();
            if (!StringUtils.isEmpty(paramMap.get("keyword"))){
                boolQuery.must(QueryBuilders.matchQuery("name",paramMap.get("keyword")).operator(Operator.AND));
            }
            //品牌查询
            if (StringUtils.isNotEmpty(paramMap.get("brand"))){
                boolQuery.filter(QueryBuilders.termQuery("brandName",paramMap.get("brand")));
            }
            //原生                                                //设置查询条件，此处可以使用QueryBuilders创建多种查询
            NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder().withQuery(boolQuery);
            //品牌聚合（分组）查询
            String skuBrand="skuBrand";
            /**添加聚合*/
            //相当于mysql中  select brand_name as brandName  from 表名 where name like %手机% group by brand
            nativeSearchQueryBuilder.addAggregation(/**聚合分组后的结果列名*/ AggregationBuilders.terms(skuBrand)./**要操作的域名*/field("brandName"));

            //分页查询
            /**
             * 参数一：searchQuery对象
             * 参数二：封装的类型
             * 参数三：操作返回对象
             */
            AggregatedPage<SkuInfo> skuInfos = estemp.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                    List<T> list = new ArrayList<>();
                    //数据命中集合
                    SearchHits hits = response.getHits();
                    if (hits != null) {
                        for (SearchHit hit : hits) {
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                            list.add((T) skuInfo);
                        }
                    }
                    return new AggregatedPageImpl<T>(list, pageable, hits.getTotalHits(), response.getAggregations());
                }
            });
           //封装返回对象
            //封装总记录数
            resultMap.put("total",skuInfos.getTotalElements());
            //封装总页数
            resultMap.put("totalPages",skuInfos.getTotalPages());
            //封装结果集合
            resultMap.put("rows",skuInfos.getContent());
            //封装品牌聚合结果
            StringTerms terms= (StringTerms) skuInfos.getAggregation(skuBrand);
            List<String> collect = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("brandList",collect);
            return resultMap;
        }
        return null;
    }
}
