package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @TableName ec_group
 */
@TableName(value = "ec_group")
@Data
public class SysGroup implements Serializable {
    /**
     * 群组ID
     */
    @TableId(type = IdType.AUTO)
    private Integer groupId;

    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 群组描述信息
     */
    private String groupDesc;

    /**
     * 群组头像
     */
    private String avatar;

    /**
     * 是否已删除
     */
    private Integer deleted;

    /**
     * 群组创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}