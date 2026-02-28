package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderPayVO {
    @NotNull
    private Integer orderId;
    private Integer couponId; // Optional
}
