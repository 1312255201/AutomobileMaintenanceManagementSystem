package cn.gugufish.entity.vo.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponVO {
    private Integer id;
    private String name;
    private BigDecimal discountAmount;
    private BigDecimal conditionAmount;
    private Integer userId;
    private String username;
    private Date validStart;
    private Date validEnd;
    private Integer status;
    private Date createTime;
}
