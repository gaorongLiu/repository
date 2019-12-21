package com.changgou.goods.api;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Spec;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

@Api(value = "商品参数管理",description = "管理商品参数")
public interface SpecApi {
    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("查询所有商品参数列表")
    public Result findAll();

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询商品参数数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "商品参数ID",required=true,paramType="path",dataType="int"),
    })
    public Result findById(Integer id);


    /***
     * 新增数据
     * @param Spec
     * @return
     */
    @ApiOperation("新增商品参数")
    public Result add(Spec Spec);


    /***
     * 修改数据
     * @param 
     * @param id
     * @return
     */
    @ApiOperation("修改商品参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "商品参数ID",required=true, paramType = "path", dataTypeClass = Integer.class),
    })
    public Result update(Spec spec, Integer id);


    /***
     * 根据ID删除商品参数数据
     * @param id
     * @return
     */
    @ApiOperation("删除商品参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "商品参数ID",required=true,paramType="path",dataType="int"),
    })
    public Result delete(Integer id);

    /***
     * 多条件搜索商品参数数据
     * @param searchMap
     * @return
     */
    @ApiOperation("查询商品参数列表")
    public Result findList(Map searchMap);


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("条件分页查询商品参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="searchMap",value = "条件查询 JSON数据， 商品参数名称或者是商品参数首字母"),
            //paramType="path"  以地址的形式提交参数
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message="Indicates ..."),
//            @ApiResponse(code = 404, message = "not found error")
//    })
    public Result findPage(Map searchMap, int page, int size);
}
