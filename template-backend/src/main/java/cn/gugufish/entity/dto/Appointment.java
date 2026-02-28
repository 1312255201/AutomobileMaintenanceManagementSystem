package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 预约信息实体类
 */
@Data
@TableName("db_appointment")
@AllArgsConstructor
@NoArgsConstructor
public class Appointment implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer userId;
    String carModel;
    String licensePlate;
    Date appointmentTime;
    String serviceType;
    String description;
    Integer status;
    Date createTime;
}
