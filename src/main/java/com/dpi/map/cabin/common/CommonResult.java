package com.dpi.map.cabin.common;

import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.util.Objects;

/**
 * @Author: chengxirui
 * @Date: 2023-04-28  08:47
 */
public class CommonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    private T data;

    CommonResult() {
    }

    private CommonResult(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    private CommonResult(ResultCode resultCode, String msg) {
        this.code = resultCode.getCode();
        this.msg = msg;
    }

    private CommonResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public static <T> CommonResult<T> error(ResultCode resultCode) {
        return new CommonResult<>(resultCode);
    }

    public static CommonResult error(Integer code, String msg) {
        return new CommonResult(code, msg);
    }

    public static CommonResult error(ResultCode resultCode, String msg) {
        return new CommonResult(resultCode, msg);
    }

    public static <T> CommonResult<T> error(T data) {
        CommonResult<T> result = new CommonResult<>(CodeEnum.SYSTEM_OK);
        result.setData(data);
        return result;
    }

    public static <T> CommonResult<T> info(Integer code, String msg) {
        return new CommonResult<>(code, msg);
    }


    public static <T> CommonResult<T> ok(T data) {
        CommonResult<T> result = new CommonResult<>(CodeEnum.SYSTEM_OK);
        result.setData(data);
        return result;
    }

    public static <T> CommonResult<T> ok() {
        return new CommonResult<>(CodeEnum.SYSTEM_OK);
    }


    public CommonResult setData(T data) {
        this.data = data;
        return this;
    }

    @SuppressWarnings("unchecked")
    public T getData() {
        return this.data;
    }

    public boolean hasSuccess() {
        return Objects.equals(CodeEnum.SYSTEM_OK.getCode(), code);
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
