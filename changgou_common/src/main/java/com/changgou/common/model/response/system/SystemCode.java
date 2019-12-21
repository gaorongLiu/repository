package com.changgou.common.model.response.system;


import com.changgou.common.model.response.ResultCode;
import lombok.ToString;

@ToString
public enum SystemCode implements ResultCode {

    SYSTEM_NOLOGINNAME_ERROR(false,26001,"登录用户名为空"),
    SYSTEM_NOPASSWORD_ERROR(false,26002,"登录密码为空"),
    SYSTEM_ILLEGALITY_ERROR(false,26003,"用户非法"),
    SYSTEM_PASSWORD_ERROR(false,26004,"密码错误");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private SystemCode(boolean success, int code, String message){
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
