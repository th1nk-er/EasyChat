package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "群组VO")
public class GroupVo {
    @Schema(description = "群组ID")
    private Integer groupId;
    @Schema(description = "群组名称")
    private String groupName;
    @Schema(description = "群组描述信息")
    private String groupDesc;
    @Schema(description = "群组头像")
    private String avatar;
    @Schema(description = "群组创建时间")
    private LocalDateTime createTime;
    @Schema(description = "群组成员数量")
    private Integer memberCount;
}
