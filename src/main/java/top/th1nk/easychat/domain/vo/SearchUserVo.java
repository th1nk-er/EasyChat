package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "搜索用户后返回的用户vo")
public class SearchUserVo {
    @Schema(description = "结果数量")
    private long total;
    @Schema(description = "页码大小")
    private long pageSize;
    @Schema(description = "搜索结果")
    private List<StrangerVo> records;
}
