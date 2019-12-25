package com.changgou.common.model.response.Sku;


import com.changgou.common.model.response.ResultCode;
import lombok.ToString;

@ToString
public enum SkuCode implements ResultCode {

    SKU_FIND_EMPTY(false,28001,"查询结果为空，请重新查询");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息ne
    String message;
    private SkuCode(boolean success, int code, String message){
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
