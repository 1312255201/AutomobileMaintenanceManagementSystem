package cn.gugufish.entity.vo.response;

import lombok.Data;
import java.util.Date;

@Data
public class VehicleVO {
    private Integer id;
    private String licensePlate;
    private String vin;
    private String engineNumber;
    private String brand;
    private String model;
    private String color;
    private Integer mileage;
    private String year;
    private Date createTime;
}
