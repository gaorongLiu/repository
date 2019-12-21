package com.changgou.goods.api;


import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Category;
import io.swagger.annotations.*;

import java.util.Map;

@Api(value = "商品分类管理",description = "管理商品分类")
public interface CategoryApi {

    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("查询所有类别列表")
    public Result findAll();

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询类别数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "类别ID",required=true,paramType="path",dataType="int"),
    })
    public Result findById(Integer id);


    /***
     * 新增数据
     * @param
     * @return
     */
    @ApiOperation("新增类别")
    public Result add(Category category);


    /***
     * 修改数据
     * @param
     * @param id
     * @return
     */
    @ApiOperation("修改类别")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "类别ID",required=true, paramType = "path", dataTypeClass = Integer.class),
    })
    public Result update(Category category, Integer id);


    /***
     * 根据ID删除类别数据
     * @param id
     * @return
     */
    @ApiOperation("删除类别")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "类别ID",required=true,paramType="path",dataType="int"),
    })
    public Result delete(Integer id);

    /***
     * 多条件搜索类别数据
     * @param searchMap
     * @return
     */
    @ApiOperation("查询类别列表")
    public Result findList(Map searchMap);


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("条件分页查询类别列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="searchMap",value = "条件查询 JSON数据， 类别名称或者是类别首字母"),
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
