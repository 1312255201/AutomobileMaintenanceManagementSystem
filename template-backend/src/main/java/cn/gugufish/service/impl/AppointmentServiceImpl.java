package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Appointment;
import cn.gugufish.entity.vo.request.AppointmentCreateVO;
import cn.gugufish.entity.vo.response.AppointmentVO;
import cn.gugufish.mapper.AppointmentMapper;
import cn.gugufish.service.AppointmentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public IPage<AppointmentVO> getAppointmentList(int userId, int pageNum, int pageSize) {
        Page<Appointment> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        
        Page<Appointment> resultPage = this.page(page, queryWrapper);
        
        return resultPage.convert(appointment -> {
            AppointmentVO vo = new AppointmentVO();
            BeanUtils.copyProperties(appointment, vo);
            return vo;
        });
    }
}
