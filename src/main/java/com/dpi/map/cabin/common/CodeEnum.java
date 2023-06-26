package com.dpi.map.cabin.common;

/**
 * @Author: chengxirui
 * @Date: 2023-04-28  08:49
 */
public enum CodeEnum implements ResultCode {
    SYSTEM_OK(0, "success"),
    SYSTEM_NO_SUCH_PARAMENT_ERROR(1002, "为空"),
    ;
    private Integer code;
    private String msg;

    private CodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}

