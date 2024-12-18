package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserSex;

@Data
@Schema(description = "陌生人信息")
public class StrangerVo {
    @Schema(description = "用户id")
    private Integer id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "用户性别")
    private UserSex sex;
    @Schema(description = "用户头像")
    private String avatar;
}
