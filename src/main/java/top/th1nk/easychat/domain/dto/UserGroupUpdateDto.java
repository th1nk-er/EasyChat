package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户群信息更新Dto")
public class UserGroupUpdateDto {
    @Schema(description = "群组ID")
    private int groupId;
    @Schema(description = "群组备注")
    private String groupRemark;
    @Schema(description = "是否免打扰")
    private boolean muted;
}
