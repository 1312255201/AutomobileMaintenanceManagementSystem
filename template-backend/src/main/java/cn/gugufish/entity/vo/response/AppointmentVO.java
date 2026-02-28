package cn.gugufish.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 预约信息响应VO
 */
@Data
public class AppointmentVO {
    Integer id;
    String carModel;
    String licensePlate;
    Date appointmentTime;
    String serviceType;
    String description;
    Integer status;
    Date createTime;
}
