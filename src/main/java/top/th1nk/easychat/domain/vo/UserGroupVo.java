package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserRole;

@Data
@Schema(description = "用户群信息")
public class UserGroupVo {
    @Schema(description = "群组ID")
    private Integer groupId;
    @Schema(description = "群组名称")
    private String groupName;
    @Schema(description = "群组描述信息")
    private String groupDesc;
    @Schema(description = "群组头像")
    private String avatar;
    @Schema(description = "用户角色")
    private UserRole role;
    @Schema(description = "用户给群组的备注")
    private String groupRemark;
}