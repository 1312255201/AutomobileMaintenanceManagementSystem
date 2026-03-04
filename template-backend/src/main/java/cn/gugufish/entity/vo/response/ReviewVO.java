package cn.gugufish.entity.vo.response;

import lombok.Data;
import java.util.Date;

@Data
public class ReviewVO {
    private Integer id;
    private Integer orderId;
    private Integer userId;
    private String username;
    private Integer rating;
    private String comment;
    private Date createTime;
}
