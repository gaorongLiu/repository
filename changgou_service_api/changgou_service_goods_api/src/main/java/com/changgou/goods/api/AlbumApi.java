package com.changgou.goods.api;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Album;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

@Api(value = "XX管理接口",description = "XXX管理接口，提供页面的增删改查")
public interface AlbumApi {

    @ApiOperation(value = "查询所有的数据")
    public Result findAll();


    @ApiImplicitParam(name = "id",value = "号码" ,required = true,paramType = "path",dataType = "long")
    @ApiOperation(value = "根据id插叙数据")
    public Result findById(Long id);

    @ApiOperation(value = "新增数据")
    public Result add(Album album);

    @ApiOperation(value = "修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "品牌ID",required=true, paramType = "path", dataTypeClass =Long.class)
    })
    public  Result update(Album album,Long id);

    @ApiOperation(value = "删除数据")
    @ApiImplicitParam(name = "id",value = "id",paramType = "path",dataType = "long",required = true)
    public Result delete(Long id);

    @ApiOperation(value = "多条件搜索")
    public Result findList(Map searchMap);

    @ApiOperation("分页条件搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页码",required = true,paramType = "path",dataType = "int"),
            @ApiImplicitParam(name="size",value = "每页显示多少条",required = true,paramType = "path",dataType = "int")
    })
    public Result findPage(Map searchMap, int page, int size);
}
