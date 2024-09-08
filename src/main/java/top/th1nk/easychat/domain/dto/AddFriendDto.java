package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "添加好友信息")
public class AddFriendDto {
    @Schema(description = "用户id")
    private int userId;
    @Schema(description = "好友id")
    private int addId;
    @Schema(description = "附加消息")
    private String addInfo;
}
