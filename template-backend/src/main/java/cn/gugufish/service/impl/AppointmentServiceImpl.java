package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Appointment;
import cn.gugufish.entity.vo.request.AppointmentCreateVO;
import cn.gugufish.mapper.AppointmentMapper;
import cn.gugufish.service.AppointmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Override
    public String createAppointment(int userId, AppointmentCreateVO vo) {
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setCarModel(vo.getCarModel());
        appointment.setLicensePlate(vo.getLicensePlate());
        appointment.setAppointmentTime(vo.getAppointmentTime());
        appointment.setServiceType(vo.getServiceType());
        appointment.setDescription(vo.getDescription());
        appointment.setStatus(0); // Pending
        appointment.setCreateTime(new Date());

        if (this.save(appointment)) {
            return null;
        } else {
            return "预约创建失败，请稍后重试";
        }
    }
}
