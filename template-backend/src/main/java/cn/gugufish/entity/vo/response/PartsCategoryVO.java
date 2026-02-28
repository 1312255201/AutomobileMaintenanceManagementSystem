package cn.gugufish.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class PartsCategoryVO {
    Integer id;
    String name;
    String description;
    Date createTime;
}
