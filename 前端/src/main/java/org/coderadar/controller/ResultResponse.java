package org.coderadar.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse<T> {

    private Integer code;
    private String msg;
    private T data;

    // 成功返回（带数据）
    public static <T> ResultResponse<T> success(T data) {
        return new ResultResponse<>(Code.SUCCESS.getCode(),
                Code.SUCCESS.getMsg(),
                data);
    }

    // 成功返回（无数据）
    public static <T> ResultResponse<T> success() {
        return new ResultResponse<>(Code.SUCCESS.getCode(),
                Code.SUCCESS.getMsg(),
                null);
    }
}