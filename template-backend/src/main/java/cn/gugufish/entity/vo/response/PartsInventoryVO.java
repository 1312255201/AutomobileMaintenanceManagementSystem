package cn.gugufish.entity.vo.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PartsInventoryVO {
    Integer id;
    String name;
    Integer categoryId;
    String categoryName;
    String brand;
    BigDecimal price;
    Integer quantity;
    String description;
    String precautions;
    Date createTime;
}
