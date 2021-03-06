package com.changgou.goods.controller;

import com.changgou.common.entity.PageResult;
import com.changgou.common.entity.Result;
import com.changgou.common.entity.StatusCode;
import com.changgou.goods.api.SpuApi;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.service.SpuService;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/spu")
public class SpuController implements SpuApi {


    @Autowired
    private SpuService spuService;

    /**
     * 添加spu&sku
     *
     * @param goods
     * @return
     */
    @PostMapping("/add")
    public Result addGoods(@RequestBody Goods goods) {
        spuService.add(goods);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        List<Spu> spuList = spuService.findAll();
        return new Result(true, StatusCode.OK, "查询成功", spuList);
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id) {
        Goods goodsById = spuService.findGoodsById(id);
        return new Result(true, StatusCode.OK, "查询成功", goodsById);
    }


    /***
     * 新增数据
     * @param spu
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Spu spu) {
        spuService.add(spu);
        return new Result(true, StatusCode.OK, "添加成功");
    }


    /***
     * 修改数据
     * @param
     * @param
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Goods goods) {
        spuService.update(goods);
        return new Result(true, StatusCode.OK, "修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        spuService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findList(@RequestBody Map searchMap) {
        List<Spu> list = spuService.findList(searchMap);
        return new Result(true, StatusCode.OK, "查询成功", list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result findPage(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Spu> pageList = spuService.findPage(searchMap, page, size);
        PageResult pageResult = new PageResult(pageList.getTotal(), pageList.getResult());
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    /**
     * 审核并上架
     *
     * @param id
     * @return
     */
    @PostMapping("/audit/{id}")
    public Result audit(@PathVariable String id) {
        spuService.audit(id);
        return new Result(true, StatusCode.OK, "审核完成，已上架");

    }

    @PostMapping("/pull/{id}")
    public Result pull(@PathVariable String id) {
        spuService.pull(id);
        return new Result(true, StatusCode.OK, "已下架");

    }

    /**
     * 上架商品
     *
     * @param id
     * @return
     */
    @PostMapping("/put/{id}")
    public Result put(@PathVariable String id) {
        spuService.put(id);
        return new Result(true, StatusCode.OK, "已上架");

    }

    @PostMapping("/delete/{id}")
    public Result dyDelete(@PathVariable String id) {
        spuService.dyDelete(id);
        return new Result(true, StatusCode.OK, "以删除");
    }

    @PostMapping("/unDelete/{id}")
    public Result unDelete(@PathVariable String id) {
        spuService.unDelete(id);
        return new Result(true, StatusCode.OK, "已还原");
    }
    @PostMapping("/redelete/{id}")
    public  Result reDelete(@PathVariable String id){
        spuService.reDelete(id);
        return new Result(true, StatusCode.OK, "商品已清除");
    }
    @GetMapping("/findSpuById/{id}")
    public Result<Spu> findSpuById(@PathVariable("id") String id){
        Spu spu = spuService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",spu);
    }
}
