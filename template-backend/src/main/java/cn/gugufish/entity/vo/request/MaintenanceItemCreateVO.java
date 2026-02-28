package cn.gugufish.entity.vo.request;

import cn.gugufish.entity.BaseData;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MaintenanceItemCreateVO implements BaseData {
    @NotNull
    private Integer orderId;
    @NotNull
    private String itemName;
    @NotNull
    private Integer itemType; // 1: Labor, 2: Part
    @NotNull
    private BigDecimal cost;
    private Integer quantity;
    private String remark;
    private Integer partId; // Required if itemType is 2
}
