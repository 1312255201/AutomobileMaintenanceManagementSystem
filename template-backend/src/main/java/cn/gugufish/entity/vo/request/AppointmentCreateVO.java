package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 预约创建请求VO
 */
@Data
public class AppointmentCreateVO {
    @NotBlank(message = "车型不能为空")
    String carModel;
    @NotBlank(message = "车牌号不能为空")
    String licensePlate;
    @NotNull(message = "预约时间不能为空")
    @Future(message = "预约时间必须是将来时间")
    Date appointmentTime;
    @NotBlank(message = "服务类型不能为空")
    String serviceType;
    String description;
}
