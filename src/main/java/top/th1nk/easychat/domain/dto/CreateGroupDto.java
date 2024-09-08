package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建群聊请求Dto")
public class CreateGroupDto {
    @Schema(description = "用户ID")
    private int userId;
    @Schema(description = "群聊名称")
    private String groupName;
    @Schema(description = "邀请的好友ID")
    private List<Integer> friendIds;
}
