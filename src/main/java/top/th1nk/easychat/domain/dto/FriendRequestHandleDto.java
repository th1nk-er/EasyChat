package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.AddUserStatus;

@Data
@Schema(description = "好友请求处理DTO")
public class FriendRequestHandleDto {
    @Schema(description = "好友请求id")
    private int id;
    @Schema(description = "处理状态")
    private AddUserStatus status;
    @Schema(description = "备注")
    private String remark;
}
