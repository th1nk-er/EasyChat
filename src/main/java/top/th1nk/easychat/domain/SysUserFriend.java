package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ec_user_friend
 */
@TableName(value ="ec_user_friend")
@Data
public class SysUserFriend implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 好友的用户ID
     */
    private Integer friendId;

    /**
     * 好友备注
     */
    private String remark;

    /**
     * 添加好友的时间
     */
    private Date createTime;

    /**
     * 是否已删除 0-未删除 1-已删除
     */
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}