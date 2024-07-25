package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserSex;

import java.util.List;

@Data
@Schema(description = "搜索用户后返回的用户vo")
public class SearchUserVo {
    @Schema(description = "结果数量")
    private long total;
    @Schema(description = "页码大小")
    private long pageSize;
    @Schema(description = "搜索结果")
    private List<Record> records;

    @Data
    public static class Record {
        @Schema(description = "用户id")
        private Integer id;
        @Schema(description = "用户名")
        private String username;
        @Schema(description = "用户昵称")
        private String nickname;
        @Schema(description = "用户性别")
        private UserSex sex;
    }
}