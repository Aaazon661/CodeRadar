package org.coderadar.controller;

import lombok.Getter;

@Getter
public enum Code {

    SUCCESS(200, "success"),

    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),

    SERVER_ERROR(500, "服务器异常"),

    LOGIN_ERROR(1001, "账号或密码错误"),
    ACCOUNT_DISABLED(1002, "账号已被禁用"),

    FILE_NOT_FOUND(2001, "文件不存在"),
    REVIEW_ERROR(3001, "代码审查失败");

    private final Integer code;
    private final String msg;

    Code(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
