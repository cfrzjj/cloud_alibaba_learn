package com.liu.rs;

import com.liu.consts.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //lombok 自动生成getset
@AllArgsConstructor  // 有参构造函数
@NoArgsConstructor //无参函数
public class CommonResult<T> {
    private Integer code;
    private String message;
    private T     data;

    public CommonResult(Integer code,String message){
        this(code,message,null);
    }
    public static <T> CommonResult<T> ok() {
        return restResult(null, CommonConstants.SUCCESS, null);
    }

    public static <T> CommonResult<T> ok(T data) {
        return restResult(data, CommonConstants.SUCCESS, null);
    }

    public static <T> CommonResult<T> ok(T data, String msg) {
        return restResult(data, CommonConstants.SUCCESS, msg);
    }

    public static <T> CommonResult<T> failed() {
        return restResult(null, CommonConstants.FAIL, null);
    }

    public static <T> CommonResult<T> failed(String msg) {
        return restResult(null, CommonConstants.FAIL, msg);
    }

    public static <T> CommonResult<T> failed(T data) {
        return restResult(data, CommonConstants.FAIL, null);
    }

    public static <T> CommonResult<T> failed(T data, String msg) {
        return restResult(data, CommonConstants.FAIL, msg);
    }

    private static <T> CommonResult<T> restResult(T data, int code, String msg) {
        CommonResult<T> apiResult = new CommonResult<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(msg);
        return apiResult;
    }
}