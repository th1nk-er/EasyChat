package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.vo.UserConversationVo;
import top.th1nk.easychat.service.SysUserConversationService;

import java.util.List;

@RestController
@RequestMapping("/conversation")
@Tag(name = "用户对话模块", description = "用户对话API")
public class UserConversationController {
    @Resource
    private SysUserConversationService sysUserConversationService;

    @Operation(summary = "获取用户对话列表", description = "获取用户对话列表")
    @GetMapping("/list/{pageNum}")
    public Response<List<UserConversationVo>> getConversations(@PathVariable int pageNum) {
        return Response.ok(sysUserConversationService.getConversations(pageNum));
    }
}
