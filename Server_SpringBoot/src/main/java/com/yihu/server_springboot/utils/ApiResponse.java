package com.yihu.server_springboot.utils;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    //成功响应（不带数据）
    public static <T> ApiResponse<T> success(String msg) {
        return new ApiResponse<>(200, msg, null);
    }
    // 成功响应（带数据）
    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(200, msg, data);
    }
    // 失败响应
    public static <T> ApiResponse<T> error(int code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
