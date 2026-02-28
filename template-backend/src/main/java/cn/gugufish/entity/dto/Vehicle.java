package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_vehicle")
public class Vehicle implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private String licensePlate;
    private String vin;
    private String engineNumber;
    private String brand;
    private String model;
    private String color;
    private Integer mileage;
    private String year;
    private Date createTime;
    private Date updateTime;
}
