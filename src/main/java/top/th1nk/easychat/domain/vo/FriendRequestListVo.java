package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.th1nk.easychat.domain.SysUserAddFriend;
import top.th1nk.easychat.enums.UserSex;

import java.util.List;

@Schema(description = "好友申请列表VO")
@Data
public class FriendRequestListVo {

    @Schema(description = "结果数量")
    private long total;
    @Schema(description = "页码大小")
    private long pageSize;
    @Schema(description = "搜索结果")
    private List<Record> records;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Record extends SysUserAddFriend {
        @Schema(description = "用户头像")
        private String avatar;
        @Schema(description = "用户昵称")
        private String nickname;
        @Schema(description = "用户名")
        private String username;
        @Schema(description = "用户性别")
        private UserSex sex;
    }
}
