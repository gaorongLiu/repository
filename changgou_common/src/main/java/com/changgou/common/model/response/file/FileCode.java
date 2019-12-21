package com.changgou.common.model.response.file;


import com.changgou.common.model.response.ResultCode;
import lombok.ToString;

@ToString
public enum FileCode implements ResultCode {

    File_NULL_ERROR(false,25001,"上传文件不能为空");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private FileCode(boolean success,int code, String message){
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
