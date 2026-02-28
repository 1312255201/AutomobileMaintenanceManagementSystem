package cn.gugufish.entity.vo.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class OrderCreateVO {
    @NotNull
    private Integer appointmentId;
    @NotNull
    private Integer repairmanId;
}
