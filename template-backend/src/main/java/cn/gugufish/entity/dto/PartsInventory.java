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
@TableName("db_parts_inventory")
@AllArgsConstructor
@NoArgsConstructor
public class PartsInventory implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String name;
    Integer categoryId;
    String brand;
    BigDecimal price;
    Integer quantity;
    String description;
    String precautions;
    Date createTime;
}
