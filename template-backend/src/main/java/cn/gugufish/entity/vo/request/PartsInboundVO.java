package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartsInboundVO {
    @NotNull(message = "配件ID不能为空")
    Integer partId;
    @NotNull(message = "入库数量不能为空")
    @Min(value = 1, message = "入库数量必须大于0")
    Integer quantity;
    @NotNull(message = "入库单价不能为空")
    @DecimalMin(value = "0.01", message = "入库单价必须大于0")
    BigDecimal price;
    Integer supplierId;
    String remark;
}
