package com.changgou.goods.controller;

import com.changgou.common.entity.Result;
import com.changgou.common.entity.StatusCode;
import com.changgou.goods.api.PageResult;
import com.changgou.goods.api.SkuApi;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.service.SkuService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/sku")
public class SkuController implements SkuApi {


    @Autowired
    private SkuService skuService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Sku> skuList = skuService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",skuList) ;
    }


    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        Sku sku = skuService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",sku);
    }


    /***
     * 新增数据
     * @param sku
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Sku sku){
        skuService.add(sku);
        return new Result(true,StatusCode.OK,"添加成功");
    }


    /***
     * 修改数据
     * @param sku
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Sku sku,@PathVariable String id){
        sku.setId(id);
        skuService.update(sku);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        skuService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search" )
    public Result findList(@RequestBody Map searchMap){
        List<Sku> list = skuService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestBody Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<Sku> pageList = skuService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 通过spuid 查询sku
     * @return
     */
    @GetMapping("/spu/{spuId}")
    public List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuid){
        Map<String,Object> searchMap=new HashMap<>();
        if (!"all".equals(spuid)){
            searchMap.put("spuid",spuid);
        }
        searchMap.put("status","1");
        List<Sku> list = skuService.findList(searchMap);
        return list;
    }

    /**
     * 分页导入
     * @param spuId
     * @param page
     * @return
     */
    @GetMapping("/spu/{spuId}/{page}")
    public PageResult<Sku> findSkuPageBySpuId(@PathVariable("spuId") String spuId, @PathVariable("page") Integer page){
        Map<String,Object> searchMap=new HashMap<>();
        if (!"all".equals(spuId)){
            searchMap.put("spuid",spuId);
        }
        searchMap.put("status","1");
        Page<Sku> page1 = skuService.findPage(searchMap, page, 1000);
        return new PageResult<>(page1.getTotal(),page1.getResult(),page1.getPages());
    }

}
