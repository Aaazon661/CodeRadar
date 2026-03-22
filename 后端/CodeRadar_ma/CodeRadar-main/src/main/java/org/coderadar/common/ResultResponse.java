package org.coderadar.common;

import lombok.Data;

/**
 * 统一响应结果封装类
 * @param <T> 数据类型
 */
@Data
public class ResultResponse<T> {
    private Integer code;
    private String message;
    private T data;

    public ResultResponse() {
    }

    public ResultResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ResultResponse<T> success() {
        return new ResultResponse<>(Code.SUCCESS.getCode(), Code.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ResultResponse<T> success(T data) {
        return new ResultResponse<>(Code.SUCCESS.getCode(), Code.SUCCESS.getMessage(), data);
    }

    /**
     * 失败响应（使用Code枚举）
     */
    public static <T> ResultResponse<T> fail(Code code) {
        return new ResultResponse<>(code.getCode(), code.getMessage(), null);
    }

    /**
     * 失败响应（自定义code和message）
     */
    public static <T> ResultResponse<T> fail(Integer code, String message) {
        return new ResultResponse<>(code, message, null);
    }

    /**
     * 失败响应（自定义message，使用默认错误码）
     */
    public static <T> ResultResponse<T> fail(String message) {
        return new ResultResponse<>(Code.SERVER_ERROR.getCode(), message, null);
    }
}
