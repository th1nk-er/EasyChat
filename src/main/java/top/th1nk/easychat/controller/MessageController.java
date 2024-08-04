package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.service.SysChatMessageService;

import java.util.List;

@RestController("/message")
@Tag(name = "消息模块", description = "消息模块API")
public class MessageController {
    @Resource
    private SysChatMessageService sysChatMessageService;

    @Operation(summary = "获取消息历史", description = "获取消息历史")
    @GetMapping("/history/{receiverId}/{currentPage}")
    public Response<List<SysChatMessage>> getMessages(@PathVariable int receiverId, @PathVariable int currentPage) {
        return Response.ok(sysChatMessageService.getMessages(receiverId, currentPage));
    }
}