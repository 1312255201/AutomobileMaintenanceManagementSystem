package cn.gugufish.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class ClientVO {
    private Integer id;
    private String name;
    private Integer gender; // 0: Unknown, 1: Male, 2: Female
    private String phone;
    private String wechat;
    private String address;
    private Date createTime;
}
