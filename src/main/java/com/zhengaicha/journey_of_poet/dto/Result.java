package com.zhengaicha.journey_of_poet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 通用返回结果，服务端响应的数据都封装成此对象
 *
 */
@Data
@AllArgsConstructor
public class Result {
    private Boolean success;
    private String errorMsg; // 错误信息
    private Object resultData; // 数据
    private Long total;


    public static Result success() {
        return new Result(true, null, null, null);
    }

    public static Result success(Object data) {
        return new Result(true, null, data, null);
    }

    public static Result success(List<?> data, Long total) {
        return new Result(true, null, data, total);
    }

    public static Result error(String errorMsg) {
        return new Result(false, errorMsg, null, null);
    }

}
