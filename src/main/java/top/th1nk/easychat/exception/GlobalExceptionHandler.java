package top.th1nk.easychat.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.enums.CommonExceptionEnum;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 登录异常
     */
    @ExceptionHandler(LoginException.class)
    public Response loginException(LoginException e) {
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * 注册异常
     */
    @ExceptionHandler(RegisterException.class)
    public Response registerException(RegisterException e) {
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * 普通异常
     */
    @ExceptionHandler(CommonException.class)
    public Response commonException(CommonException e) {
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * 404
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Response noResourceFoundException(NoResourceFoundException e) {
        return Response.error(CommonExceptionEnum.RESOURCE_NOT_FOUND.getCode(), CommonExceptionEnum.RESOURCE_NOT_FOUND.getMessage());
    }

    /**
     * 未知异常
     */
    @ExceptionHandler(Exception.class)
    public Response exception(Exception e) {
        log.error("内部异常", e);
        return Response.error("Unknown error");
    }
}
