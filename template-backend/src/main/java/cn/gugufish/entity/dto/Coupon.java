package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("db_coupon")
public class Coupon implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private BigDecimal discountAmount;
    private BigDecimal conditionAmount;
    private Integer userId;
    private Date validStart;
    private Date validEnd;
    private Integer status; // 0: Unused, 1: Used, 2: Expired
    private Date createTime;
}
