package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_client")
public class Client implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private String name;
    private Integer gender;
    private String phone;
    private String wechat;
    private String address;
    private Date createTime;
    private Date updateTime;
}
