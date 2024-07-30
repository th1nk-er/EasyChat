package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户好友列表vo")
public class FriendListVo {
    @Schema(description = "结果数量")
    private long total;
    @Schema(description = "页码大小")
    private long pageSize;
    @Schema(description = "好友列表")
    private List<UserFriendVo> records;
}
