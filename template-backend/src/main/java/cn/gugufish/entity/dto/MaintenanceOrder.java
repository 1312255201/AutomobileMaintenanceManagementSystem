package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("db_maintenance_order")
public class MaintenanceOrder implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer appointmentId;
    private Integer repairmanId;
    private Integer status; // 0: Pending, 1: In Progress, 2: Completed, 3: Paid
    private BigDecimal totalCost;
    private Date createTime;
    private Date updateTime;
}
