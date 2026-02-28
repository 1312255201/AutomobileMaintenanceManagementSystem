package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Appointment;
import cn.gugufish.entity.dto.MaintenanceOrder;
import cn.gugufish.entity.dto.Repairman;
import cn.gugufish.entity.vo.request.OrderCreateVO;
import cn.gugufish.entity.vo.response.MaintenanceOrderVO;
import cn.gugufish.mapper.AppointmentMapper;
import cn.gugufish.mapper.MaintenanceOrderMapper;
import cn.gugufish.mapper.RepairmanMapper;
import cn.gugufish.service.MaintenanceOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MaintenanceOrderServiceImpl extends ServiceImpl<MaintenanceOrderMapper, MaintenanceOrder> implements MaintenanceOrderService {

    @Resource
    AppointmentMapper appointmentMapper;

    @Resource
    RepairmanMapper repairmanMapper;

    @Override
    public List<MaintenanceOrderVO> getOrderList() {
        List<MaintenanceOrder> orders = this.list();
        Map<Integer, String> repairmanMap = repairmanMapper.selectList(null).stream()
                .collect(Collectors.toMap(Repairman::getId, Repairman::getName));
        
        return orders.stream().map(order -> {
            MaintenanceOrderVO vo = order.asViewObject(MaintenanceOrderVO.class);
            vo.setRepairmanName(repairmanMap.get(order.getRepairmanId()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String createOrder(OrderCreateVO vo) {
        Appointment appointment = appointmentMapper.selectById(vo.getAppointmentId());
        if (appointment == null) {
            return "预约不存在";
        }
        if (appointment.getStatus() != 0) {
            return "预约状态不正确";
        }

        MaintenanceOrder order = new MaintenanceOrder();
        order.setAppointmentId(vo.getAppointmentId());
        order.setRepairmanId(vo.getRepairmanId());
        order.setStatus(0); // Pending
        order.setTotalCost(BigDecimal.ZERO);
        order.setCreateTime(new Date());
        
        if (this.save(order)) {
            // Update appointment status to confirmed (1)
            appointment.setStatus(1);
            appointmentMapper.updateById(appointment);
            return null;
        } else {
            return "创建维修单失败";
        }
    }
}
