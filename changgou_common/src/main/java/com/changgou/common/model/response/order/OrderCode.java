package com.changgou.common.model.response.order;


import com.changgou.common.model.response.ResultCode;
import lombok.ToString;

@ToString
public enum OrderCode implements ResultCode {

    ORDER_NOT_EXIST(false,5000,"订单不存在"),
    ORDERID_NOT_EXIST(false,50001,"订单id不存在"),
    SHUPPINGCODE_OR_NAME_EMPTY(false,50002,"物流单号或物流名称不存在，请联系物流"),
    ORDER_NOT_SEND(false,50003,"订单未发货");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private OrderCode(boolean success, int code, String message){
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
