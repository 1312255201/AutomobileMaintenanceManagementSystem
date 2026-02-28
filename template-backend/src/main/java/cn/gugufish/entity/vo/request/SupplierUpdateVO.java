package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierUpdateVO extends SupplierCreateVO {
    @NotNull(message = "ID不能为空")
    Integer id;
}
