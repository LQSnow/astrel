package top.lqsnow.astrel.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.lqsnow.astrel.common.api.ApiResponse;
import top.lqsnow.astrel.common.error.ErrorCode;
import top.lqsnow.astrel.common.exception.BizException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常：我们主动抛出的，属于可预期错误
     */
    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBizException(BizException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("BizException, code={}, message={}", errorCode.getCode(), e.getMessage());
        return ApiResponse.error(errorCode, e.getMessage());
    }

    /**
     * 参数校验失败 / 请求体格式错误
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ApiResponse<Void> handleBadRequest(Exception e) {
        log.warn("Bad request: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.BAD_REQUEST, "请求参数错误");
    }

    /**
     * 兜底异常处理：任何没被上面捕获的异常都会到这里
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return ApiResponse.error(ErrorCode.INTERNAL_ERROR);
    }
}
