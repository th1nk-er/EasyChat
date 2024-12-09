package top.th1nk.easychat.exception;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import top.th1nk.easychat.config.easychat.ChatProperties;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.exception.enums.CommonExceptionEnum;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Resource
    private ChatProperties chatProperties;

    /**
     * 自定义异常
     *
     * @param e 自定义异常
     */
    @ExceptionHandler(CustomException.class)
    public Response<?> commonException(CustomException e) {
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * 权限不足
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public Response<?> authorityException() {
        return Response.error("权限不足");
    }

    /**
     * 参数错误
     */
    @ExceptionHandler(
            {
                    MethodArgumentTypeMismatchException.class,
                    MissingServletRequestParameterException.class,
                    HttpMessageNotReadableException.class
            })
    public Response<?> parameterException() {
        return Response.error("参数错误");
    }

    /**
     * 404
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Response<?> noResourceFoundException() {
        return Response.error(CommonExceptionEnum.RESOURCE_NOT_FOUND.getCode(), CommonExceptionEnum.RESOURCE_NOT_FOUND.getMessage());
    }

    /**
     * 不支持的Method
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<?> httpRequestMethodNotSupportedException() {
        return Response.error(CommonExceptionEnum.METHOD_NOT_ALLOWED.getCode(), CommonExceptionEnum.METHOD_NOT_ALLOWED.getMessage());
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/upload/" + chatProperties.getFileDir())) {
            // 文件下载异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("File download error");
        }
        log.error("内部异常", e);
        return ResponseEntity.ok(Response.error("Unknown error"));
    }
}
