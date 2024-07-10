package top.th1nk.easychat.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "API响应实体类")
public class Response {
    @Schema(description = "状态码")
    private int code;
    @Schema(description = "响应消息")
    private String message;
    @Schema(description = "响应数据")
    private Object data;

    public Response(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Response ok(Object data) {
        return new Response(200, "OK", data);
    }

    public static Response ok() {
        return ok(null);
    }

    public static Response error(String message) {
        return error(500, message);
    }

    public static Response error(int code, String message) {
        return new Response(code, message, null);
    }

    public static Response error() {
        return error("未知错误");
    }
}
