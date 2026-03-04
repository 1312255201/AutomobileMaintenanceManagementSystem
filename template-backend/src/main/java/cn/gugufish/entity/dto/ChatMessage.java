package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_chat_message")
public class ChatMessage implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer orderId;
    private Integer senderId;
    private String senderRole;
    private String senderName;
    private String avatar;
    private String content;
    private String type;
    private Date createTime;
}
