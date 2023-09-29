package com.zhengaicha.sycamore_street.common;

import lombok.Data;

/**
 * 通用返回结果，服务端响应的数据都封装成此对象
 *
 * @param <T>
 */
@Data
public class Result<T> {
    private Integer code; // 状态码 （操作成功为 1，失败为0或其他）
    private String msg; // 错误信息
    private T data; // 数据


    //操作成功
    public static <T> Result<T> success(T object){
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 1;
        return result;
    }

    //操作失败
    public static <T> Result<T> error(String msg){
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }
}
