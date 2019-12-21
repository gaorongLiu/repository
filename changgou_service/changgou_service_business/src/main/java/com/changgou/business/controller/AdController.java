package com.changgou.business.controller;
import com.changgou.business.api.AdApi;
import com.changgou.business.pojo.Ad;
import com.changgou.business.service.AdService;
import com.changgou.common.entity.PageResult;
import com.changgou.common.entity.Result;
import com.changgou.common.entity.StatusCode;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/ad")
public class AdController implements AdApi {


    @Autowired
    private AdService adService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Ad> adList = adService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",adList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        Ad ad = adService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",ad);
    }


    /***
     * 新增数据
     * @param ad
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Ad ad){
        adService.add(ad);
        return new Result(true,StatusCode.OK,"添加成功");
    }


    /***
     * 修改数据
     * @param ad
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Ad ad,@PathVariable Integer id){
        ad.setId(id);
        adService.update(ad);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        adService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search" )
    public Result findList(@RequestBody Map searchMap){
        List<Ad> list = adService.findList(searchMap);
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
    public Result findPage(@RequestBody Map searchMap, @PathVariable Integer page, @PathVariable  Integer size){
        Page<Ad> pageList = adService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }


}