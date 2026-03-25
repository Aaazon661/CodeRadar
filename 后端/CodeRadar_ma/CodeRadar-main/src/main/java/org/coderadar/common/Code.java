package org.coderadar.common;

/**
 * 统一状态码枚举
 */
public enum Code {
    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    LOGIN_ERROR(401, "账号或密码错误"),
    ACCOUNT_DISABLED(403, "账号已被禁用"),

    // 业务错误 4xx
    FILE_NOT_FOUND(404, "文件不存在"),
    REVIEW_ERROR(400, "代码审查失败"),

    // 服务器错误 5xx
    SERVER_ERROR(500, "服务器内部错误"),
    DATABASE_ERROR(500, "数据库操作失败");

    private final Integer code;
    private final String message;

    Code(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
