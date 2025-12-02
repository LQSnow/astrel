package top.lqsnow.astrel.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // ======== 通用（1xxx） ========
    OK("0", "OK"),
    BAD_REQUEST("ASTREL-1002", "请求参数错误"),
    INTERNAL_ERROR("ASTREL-1001", "服务器内部错误"),

    // ======== 认证相关（2xxx） ========
    AUTH_USERNAME_EXISTS("ASTREL-2001", "用户名已存在"),
    AUTH_INVITE_CODE_INVALID("ASTREL-2002", "邀请码无效或已被使用"),
    AUTH_INVALID_CREDENTIALS("ASTREL-2003", "用户名或密码错误"),
    AUTH_UNAUTHORIZED("ASTREL-2004", "未登录或token无效"),
    AUTH_ACCOUNT_LOCKED("ASTREL-2005", "账号已被封禁");


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
