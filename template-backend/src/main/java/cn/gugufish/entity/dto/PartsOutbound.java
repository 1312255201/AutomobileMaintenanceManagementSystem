package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("db_parts_outbound")
@AllArgsConstructor
@NoArgsConstructor
public class PartsOutbound implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer partId;
    Integer quantity;
    BigDecimal price;
    String customerName;
    Integer appointmentId;
    Integer operatorId;
    String remark;
    Date createTime;
}
