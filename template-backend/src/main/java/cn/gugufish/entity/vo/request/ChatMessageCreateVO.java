package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessageCreateVO {
    @NotNull
    private Integer orderId;
    
    @NotNull
    private String content;
    
    @NotNull
    private String type; // text, image
}
