package cn.gugufish.service;

import cn.gugufish.entity.dto.Appointment;
import cn.gugufish.entity.vo.request.AppointmentCreateVO;
import cn.gugufish.entity.vo.response.AppointmentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AppointmentService extends IService<Appointment> {
    String createAppointment(int userId, AppointmentCreateVO vo);
    IPage<AppointmentVO> getAppointmentList(int userId, int pageNum, int pageSize);
}
