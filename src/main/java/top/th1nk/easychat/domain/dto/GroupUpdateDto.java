package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "群信息更新Dto")
public class GroupUpdateDto {
    @Schema(description = "发起请求的用户ID")
    private int userId;
    @Schema(description = "群聊名称")
    private String groupName;
    @Schema(description = "群聊简介")
    private String groupDesc;
}
