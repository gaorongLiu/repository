package com.changgou.goods.api;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(value = "日志管理",description = "日志相关")
public interface LogApi {

    @ApiOperation("查看全部日志")
    public Result findAll();

    @ApiOperation("根据id查数据")
    @ApiImplicitParam(name = "id",value = "日志编号",required = true,paramType = "path",dataType = "long")
    public Result findById(Long id);

    @ApiOperation(value = "新增数据")
    public Result add(Log log);

}
