package com.dpi.map.cabin.common;

/**
 * @Author: chengxirui
 * @Date: 2023-04-28  09:10
 */
public class CommonException extends RuntimeException implements ResultCode {
    private String msg;
    private Integer code;

    public CommonException(Integer code, String msg) {
        super(code + ":" + msg, (Throwable)null, true, true);
        this.code = code;
        this.msg = msg;
    }

    public CommonException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMsg());
    }

    public CommonException(ResultCode resultCode, String message) {
        this(resultCode.getCode(), message + resultCode.getMsg());
    }

    public CommonException(ResultCode resultCode, Object... args) {
        this(resultCode.getCode(), String.format(resultCode.getMsg(), args));
    }

    public CommonException(ResultCode resultCode, String str, Boolean flag) {
        this(resultCode.getCode(), resultCode.getMsg() + ":" + str);
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}