package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Appointment;
import cn.gugufish.entity.dto.Account;
import cn.gugufish.entity.dto.MaintenanceOrder;
import cn.gugufish.entity.vo.request.AppointmentCreateVO;
import cn.gugufish.entity.vo.response.AppointmentVO;
import cn.gugufish.entity.vo.response.MaintenanceOrderVO;
import cn.gugufish.mapper.AccountMapper;
import cn.gugufish.mapper.AppointmentMapper;
import cn.gugufish.mapper.MaintenanceOrderMapper;
import cn.gugufish.service.AppointmentService;
import cn.gugufish.service.MaintenanceOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Resource
    AccountMapper accountMapper;

    @Resource
    MaintenanceOrderMapper maintenanceOrderMapper;
    
    @Resource
    MaintenanceOrderService maintenanceOrderService;

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

    @Override
    public List<AppointmentVO> getActiveAppointments() {
        // Assume status 0 (Pending) and 1 (Confirmed) are "active" repair orders that might need parts
        // In a real system, you might have a "Repairing" status (e.g., status 2 could be Repairing, 3 Completed)
        // For now, let's fetch appointments that are not Completed (2) or Cancelled (3)
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("status", 0, 1); // Pending or Confirmed
        queryWrapper.orderByDesc("create_time");
        
        return this.list(queryWrapper).stream().map(appointment -> {
            AppointmentVO vo = new AppointmentVO();
            BeanUtils.copyProperties(appointment, vo);
            // Optionally fetch user info if needed for display
            Account account = accountMapper.selectById(appointment.getUserId());
            if (account != null) {
                // We might want to append username to description or add a field to VO
                // For simplicity, let's just use the VO as is, but maybe add user info to description for dropdown
                vo.setDescription(vo.getDescription() + " (User: " + account.getUsername() + ")");
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Object getOrderInfo(int appointmentId) {
        QueryWrapper<MaintenanceOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("appointment_id", appointmentId);
        MaintenanceOrder order = maintenanceOrderMapper.selectOne(queryWrapper);
        if (order == null) return null;
        
        // Return full detail for billing
        return maintenanceOrderService.getOrderDetail(order.getId());
    }

    @Override
    public String payOrder(int orderId, Integer couponId) {
        return maintenanceOrderService.payOrderWithCoupon(orderId, couponId);
    }
}
