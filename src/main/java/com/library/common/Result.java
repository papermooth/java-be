package com.library.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code; // 响应码：0成功，1失败
    private String message; // 响应消息
    private T data; // 响应数据

    // 成功响应
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    // 失败响应
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMessage(message);
        return result;
    }
}