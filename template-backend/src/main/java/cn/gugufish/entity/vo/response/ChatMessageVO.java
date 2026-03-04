package cn.gugufish.entity.vo.response;

import lombok.Data;
import java.util.Date;

@Data
public class ChatMessageVO {
    private Integer id;
    private Integer orderId;
    private Integer senderId;
    private String senderRole;
    private String senderName;
    private String avatar;
    private String content;
    private String type;
    private Date createTime;
    
    // Helper for frontend
    private Boolean isSelf;
}
