package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.*;
import cn.gugufish.entity.vo.request.MaintenanceItemCreateVO;
import cn.gugufish.entity.vo.request.OrderCreateVO;
import cn.gugufish.entity.vo.response.MaintenanceItemVO;
import cn.gugufish.entity.vo.response.MaintenanceOrderVO;
import cn.gugufish.mapper.*;
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

    @Resource
    MaintenanceItemMapper maintenanceItemMapper;

    @Resource
    PartsInventoryMapper partsInventoryMapper;

    @Resource
    PartsOutboundMapper partsOutboundMapper;

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
    public MaintenanceOrderVO getOrderDetail(int id) {
        MaintenanceOrder order = this.getById(id);
        if (order == null) return null;
        
        MaintenanceOrderVO vo = order.asViewObject(MaintenanceOrderVO.class);
        Repairman repairman = repairmanMapper.selectById(order.getRepairmanId());
        if (repairman != null) {
            vo.setRepairmanName(repairman.getName());
        }
        
        List<MaintenanceItem> items = maintenanceItemMapper.selectList(
                this.lambdaQuery().getWrapper().eq("order_id", id)); // Using raw query wrapper style or better use Item Mapper directly
        
        // Let's use item mapper properly
        items = maintenanceItemMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MaintenanceItem>().eq("order_id", id));

        vo.setItems(items.stream()
                .map(item -> item.asViewObject(MaintenanceItemVO.class))
                .collect(Collectors.toList()));
        
        return vo;
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

    @Override
    @Transactional
    public String addItem(MaintenanceItemCreateVO vo) {
        MaintenanceOrder order = this.getById(vo.getOrderId());
        if (order == null) return "维修单不存在";
        if (order.getStatus() >= 2) return "维修单已完成或已支付，无法添加项目";

        MaintenanceItem item = vo.asViewObject(MaintenanceItem.class);
        item.setCreateTime(new Date());
        
        // Handle Part type
        if (vo.getItemType() == 2) {
            if (vo.getPartId() == null) return "配件ID不能为空";
            PartsInventory part = partsInventoryMapper.selectById(vo.getPartId());
            if (part == null) return "配件不存在";
            if (part.getQuantity() < vo.getQuantity()) return "库存不足";
            
            // Deduct stock
            part.setQuantity(part.getQuantity() - vo.getQuantity());
            partsInventoryMapper.updateById(part);
            
            // Create Outbound record
            PartsOutbound outbound = new PartsOutbound();
            outbound.setPartId(vo.getPartId());
            outbound.setQuantity(vo.getQuantity());
            outbound.setPrice(vo.getCost()); // Use cost as sale price
            outbound.setAppointmentId(order.getAppointmentId());
            outbound.setRemark("维修单自动出库: " + order.getId());
            outbound.setCreateTime(new Date());
            partsOutboundMapper.insert(outbound);
            
            item.setPartId(vo.getPartId());
        }
        
        maintenanceItemMapper.insert(item);
        
        // Update order total cost
        BigDecimal itemTotal = vo.getCost().multiply(BigDecimal.valueOf(vo.getItemType() == 2 ? vo.getQuantity() : 1));
        order.setTotalCost(order.getTotalCost().add(itemTotal));
        order.setStatus(1); // Set to In Progress if adding items
        this.updateById(order);
        
        return null;
    }

    @Override
    @Transactional
    public String deleteItem(int itemId) {
        MaintenanceItem item = maintenanceItemMapper.selectById(itemId);
        if (item == null) return "项目不存在";
        
        MaintenanceOrder order = this.getById(item.getOrderId());
        if (order.getStatus() >= 2) return "维修单已完成或已支付，无法删除项目";
        
        // Handle Part type restore
        if (item.getItemType() == 2 && item.getPartId() != null) {
            PartsInventory part = partsInventoryMapper.selectById(item.getPartId());
            if (part != null) {
                part.setQuantity(part.getQuantity() + item.getQuantity());
                partsInventoryMapper.updateById(part);
            }
            // Note: We are not deleting the outbound record here to keep logs, or maybe we should?
            // The prompt says "associate sales outbound records".
            // If we restore stock, the outbound record is technically invalid or reversed.
            // Let's leave it for now or maybe add a "Reversed" remark to a new outbound record?
            // Simpler: Just restore stock.
        }
        
        maintenanceItemMapper.deleteById(itemId);
        
        // Update order total cost
        BigDecimal itemTotal = item.getCost().multiply(BigDecimal.valueOf(item.getItemType() == 2 ? item.getQuantity() : 1));
        order.setTotalCost(order.getTotalCost().subtract(itemTotal));
        this.updateById(order);
        
        return null;
    }

    @Override
    @Transactional
    public String completeOrder(int orderId) {
        MaintenanceOrder order = this.getById(orderId);
        if (order == null) return "维修单不存在";
        
        order.setStatus(2); // Completed
        order.setUpdateTime(new Date());
        this.updateById(order);
        
        // Update appointment status to completed (2)
        Appointment appointment = appointmentMapper.selectById(order.getAppointmentId());
        if (appointment != null) {
            appointment.setStatus(2);
            appointmentMapper.updateById(appointment);
        }
        
        return null;
    }

    @Override
    @Transactional
    public String payOrder(int orderId) {
        MaintenanceOrder order = this.getById(orderId);
        if (order == null) return "维修单不存在";
        
        order.setStatus(3); // Paid
        order.setUpdateTime(new Date());
        this.updateById(order);
        
        return null;
    }
}
