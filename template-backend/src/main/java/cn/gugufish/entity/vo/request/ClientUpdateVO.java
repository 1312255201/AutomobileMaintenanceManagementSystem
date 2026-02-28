package cn.gugufish.entity.vo.request;

import lombok.Data;

@Data
public class ClientUpdateVO {
    private String name;
    private Integer gender;
    private String phone;
    private String wechat;
    private String address;
}
