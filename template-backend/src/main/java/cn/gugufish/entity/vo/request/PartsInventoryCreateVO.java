package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartsInventoryCreateVO {
    @NotBlank(message = "配件名称不能为空")
    String name;
    @NotNull(message = "配件分类不能为空")
    Integer categoryId;
    String brand;
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    BigDecimal price;
    String description;
    String precautions;
}
