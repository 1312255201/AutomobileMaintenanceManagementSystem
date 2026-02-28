package cn.gugufish.entity.vo.request;

import cn.gugufish.entity.BaseData;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class VehicleCreateVO implements BaseData {
    @NotBlank(message = "License plate cannot be empty")
    private String licensePlate;
    private String vin;
    private String engineNumber;
    private String brand;
    private String model;
    private String color;
    private Integer mileage;
    private String year;
}
