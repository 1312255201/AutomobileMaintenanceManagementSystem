package cn.gugufish.service;

import cn.gugufish.entity.dto.Appointment;
import cn.gugufish.entity.vo.request.AppointmentCreateVO;
import cn.gugufish.entity.vo.response.AppointmentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AppointmentService extends IService<Appointment> {
    String createAppointment(int userId, AppointmentCreateVO vo);
    IPage<AppointmentVO> getAppointmentList(int userId, int pageNum, int pageSize);
    List<AppointmentVO> getActiveAppointments();
    Object getOrderInfo(int appointmentId);
    String payOrder(int orderId, Integer couponId);
}
