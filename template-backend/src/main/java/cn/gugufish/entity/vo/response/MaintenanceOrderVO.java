package cn.gugufish.entity.vo.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class MaintenanceOrderVO {
    private Integer id;
    private Integer appointmentId;
    private Integer repairmanId;
    private String repairmanName;
    private String carModel;
    private String licensePlate;
    private String description;
    private Integer status;
    private BigDecimal totalCost;
    private Date createTime;
    private List<MaintenanceItemVO> items;
}
