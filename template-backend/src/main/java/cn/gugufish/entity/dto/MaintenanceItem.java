package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("db_maintenance_item")
public class MaintenanceItem implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer orderId;
    private String itemName;
    private Integer itemType; // 1: Labor, 2: Part
    private BigDecimal cost;
    private Integer quantity;
    private String remark;
    private Integer partId;
    private Date createTime;
}
