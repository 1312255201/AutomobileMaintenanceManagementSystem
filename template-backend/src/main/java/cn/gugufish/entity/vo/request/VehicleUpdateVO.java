package cn.gugufish.entity.vo.request;

import cn.gugufish.entity.BaseData;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class VehicleUpdateVO implements BaseData {
    @NotNull
    private Integer id;
    private String licensePlate;
    private String vin;
    private String engineNumber;
    private String brand;
    private String model;
    private String color;
    private Integer mileage;
    private String year;
}
