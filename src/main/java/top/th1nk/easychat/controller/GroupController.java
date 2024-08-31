package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.dto.CreateGroupDto;
import top.th1nk.easychat.exception.GroupException;
import top.th1nk.easychat.exception.enums.GroupExceptionEnum;
import top.th1nk.easychat.service.SysGroupService;

@RestController
@RequestMapping("/group")
@Tag(name = "群聊", description = "群聊API")
public class GroupController {
    @Resource
    private SysGroupService sysGroupService;

    @Operation(summary = "创建群聊", description = "创建群聊")
    @PostMapping("/create")
    public Response<?> createGroup(@RequestBody CreateGroupDto createGroupDto) {
        if (sysGroupService.createGroup(createGroupDto)) {
            return Response.ok();
        }
        throw new GroupException(GroupExceptionEnum.GROUP_CREATE_FAIL);
    }
}
