package top.lqsnow.astrel.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // ======== 通用（1xxx） ========
    OK("0", "OK"),
    BAD_REQUEST("ASTREL-1002", "请求参数错误"),
    INTERNAL_ERROR("ASTREL-1001", "服务器内部错误");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
