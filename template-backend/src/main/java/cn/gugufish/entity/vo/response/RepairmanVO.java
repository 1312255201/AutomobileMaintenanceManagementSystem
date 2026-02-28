package cn.gugufish.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class RepairmanVO {
    Integer id;
    Integer accountId;
    String username;
    String name;
    String phone;
    String email;
    String introduction;
    String specialization;
    Date createTime;
}
