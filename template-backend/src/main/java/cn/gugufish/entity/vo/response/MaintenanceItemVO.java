package cn.gugufish.entity.vo.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MaintenanceItemVO {
    private Integer id;
    private Integer orderId;
    private String itemName;
    private Integer itemType;
    private BigDecimal cost;
    private Integer quantity;
    private String remark;
    private Integer partId;
    private Date createTime;
}
