package cn.gugufish.service;

import cn.gugufish.entity.dto.Appointment;
import cn.gugufish.entity.vo.request.AppointmentCreateVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AppointmentService extends IService<Appointment> {
    String createAppointment(int userId, AppointmentCreateVO vo);
}
