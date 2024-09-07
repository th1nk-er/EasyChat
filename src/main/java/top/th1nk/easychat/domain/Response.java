package top.th1nk.easychat.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "API响应实体类")
public class Response<T> {
    @Schema(description = "状态码")
    private int code;
    @Schema(description = "响应消息")
    private String message;
    @Schema(description = "响应数据")
    private T data;

    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(200, "OK", data);
    }

    public static <T> Response<T> ok() {
        return ok(null);
    }

    public static <T> Response<T> error(String message) {
        return error(500, message);
    }

    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }

    public static <T> Response<T> error() {
        return error("操作失败");
    }
}
