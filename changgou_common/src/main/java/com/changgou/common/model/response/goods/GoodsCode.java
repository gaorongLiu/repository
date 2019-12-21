package com.changgou.common.model.response.goods;


import com.changgou.common.model.response.ResultCode;
import lombok.ToString;

@ToString
public enum GoodsCode implements ResultCode {

    GOODS_BRAND_ADD_ERROR(false,22001,"商品添加失败"),
    GOODS_NOT_FIND_ERROR(false,22002,"未找到该商品"),
    GOODS_HAS_BEEN_BEND(false,22003,"该商品已被删除"),
    GOODS_HAS_BEEN_UPMARKETABLE(false,22004,"该商品已下架"),
    GOODS_NOT_CHECK(false,22005,"该商未审核"),
    GOODS_NOT_SOLDOUT(false,22006,"该商未下架，无法删除"),
    GOODS_NOT_AT_DELETEAREA(false,22006,"未在回收区无法清除"),
    GOODS_NOT_DELETE(false,22007,"该商品未被删除");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private GoodsCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }
    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }


}
