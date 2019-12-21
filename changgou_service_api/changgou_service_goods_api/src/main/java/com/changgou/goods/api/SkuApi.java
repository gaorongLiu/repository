package com.changgou.goods.api;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Sku;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

@Api(value = "商品详情管理" ,description = "管理商品详情")
public interface SkuApi {

    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("查询所有商品详情列表")
    public Result findAll();

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询商品详情数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "商品详情ID",required=true,paramType="path",dataType="string"),
    })
    public Result findById(String id);


    /***
     * 新增数据
     * @param
     * @return
     */
    @ApiOperation("新增商品详情")
    public Result add(Sku sku);


    /***
     * 修改数据
     * @param
     * @param id
     * @return
     */
    @ApiOperation("修改商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "商品详情ID",required=true, paramType = "path", dataTypeClass = String.class),
    })
    public Result update(Sku sku, String id);


    /***
     * 根据ID删除商品详情数据
     * @param id
     * @return
     */
    @ApiOperation("删除商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "商品详情ID",required=true,paramType="path",dataType="string"),
    })
    public Result delete(String id);

    /***
     * 多条件搜索商品详情数据
     * @param searchMap
     * @return
     */
    @ApiOperation("查询商品详情列表")
    public Result findList(Map searchMap);


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("条件分页查询商品详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="searchMap",value = "条件查询 JSON数据， 商品详情名称或者是商品详情首字母"),
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
