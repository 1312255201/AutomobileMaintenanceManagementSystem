package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CouponCreateVO {
    @NotEmpty(message = "优惠券名称不能为空")
    String name;
    
    @NotNull(message = "优惠金额不能为空")
    @DecimalMin(value = "0.01", message = "优惠金额必须大于0")
    BigDecimal discountAmount;
    
    @DecimalMin(value = "0.00", message = "门槛金额不能小于0")
    BigDecimal conditionAmount = BigDecimal.ZERO;
    
    @NotEmpty(message = "必须选择发放的用户")
    List<Integer> userIds;
    
    @NotNull(message = "有效期天数不能为空")
    @DecimalMin(value = "1", message = "有效期至少为1天")
    Integer validDays;
}
