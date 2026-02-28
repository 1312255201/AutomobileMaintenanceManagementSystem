package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Appointment;
import cn.gugufish.entity.dto.PartsInbound;
import cn.gugufish.entity.dto.PartsInventory;
import cn.gugufish.entity.dto.PartsOutbound;
import cn.gugufish.entity.dto.PartsCategory;
import cn.gugufish.entity.dto.Account;
import cn.gugufish.entity.vo.request.PartsInboundVO;
import cn.gugufish.entity.vo.request.PartsInventoryCreateVO;
import cn.gugufish.entity.vo.request.PartsInventoryUpdateVO;
import cn.gugufish.entity.vo.request.PartsOutboundVO;
import cn.gugufish.entity.vo.response.PartsInventoryVO;
import cn.gugufish.mapper.PartsInboundMapper;
import cn.gugufish.mapper.PartsInventoryMapper;
import cn.gugufish.mapper.PartsOutboundMapper;
import cn.gugufish.mapper.PartsCategoryMapper;
import cn.gugufish.mapper.AppointmentMapper;
import cn.gugufish.mapper.AccountMapper;
import cn.gugufish.service.PartsInventoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PartsInventoryServiceImpl extends ServiceImpl<PartsInventoryMapper, PartsInventory> implements PartsInventoryService {

    @Resource
    PartsInboundMapper partsInboundMapper;

    @Resource
    PartsOutboundMapper partsOutboundMapper;

    @Resource
    PartsCategoryMapper partsCategoryMapper;

    @Resource
    AppointmentMapper appointmentMapper;

    @Resource
    AccountMapper accountMapper;

    @Override
    public String createPart(PartsInventoryCreateVO vo) {
        PartsInventory part = new PartsInventory();
        BeanUtils.copyProperties(vo, part);
        part.setQuantity(0); // Initial quantity 0
        part.setCreateTime(new Date());
        
        if (this.save(part)) {
            return null;
        } else {
            return "创建失败";
        }
    }

    @Override
    public IPage<PartsInventoryVO> getPartList(int pageNum, int pageSize, String name, Integer categoryId, String brand) {
        Page<PartsInventory> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PartsInventory> queryWrapper = new QueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        if (brand != null && !brand.isEmpty()) {
            queryWrapper.like("brand", brand);
        }
        queryWrapper.orderByDesc("create_time");
        
        this.page(page, queryWrapper);
        
        return page.convert(part -> {
            PartsInventoryVO vo = new PartsInventoryVO();
            BeanUtils.copyProperties(part, vo);
            PartsCategory category = partsCategoryMapper.selectById(part.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
            return vo;
        });
    }

    @Override
    public String updatePart(PartsInventoryUpdateVO vo) {
        PartsInventory part = this.getById(vo.getId());
        if (part == null) return "配件不存在";
        
        BeanUtils.copyProperties(vo, part);
        
        if (this.updateById(part)) {
            return null;
        } else {
            return "更新失败";
        }
    }

    @Override
    public String deletePart(int id) {
        if (this.removeById(id)) {
            return null;
        } else {
            return "删除失败";
        }
    }

    @Override
    @Transactional
    public String inbound(int operatorId, PartsInboundVO vo) {
        PartsInventory part = this.getById(vo.getPartId());
        if (part == null) return "配件不存在";
        
        // Update Inventory
        part.setQuantity(part.getQuantity() + vo.getQuantity());
        if (!this.updateById(part)) {
            throw new RuntimeException("库存更新失败");
        }
        
        // Create Record
        PartsInbound inbound = new PartsInbound();
        BeanUtils.copyProperties(vo, inbound);
        inbound.setOperatorId(operatorId);
        inbound.setCreateTime(new Date());
        
        if (partsInboundMapper.insert(inbound) > 0) {
            return null;
        } else {
            throw new RuntimeException("入库记录创建失败");
        }
    }

    @Override
    @Transactional
    public String outbound(int operatorId, PartsOutboundVO vo) {
        PartsInventory part = this.getById(vo.getPartId());
        if (part == null) return "配件不存在";
        
        if (part.getQuantity() < vo.getQuantity()) {
            return "库存不足";
        }

        Appointment appointment = appointmentMapper.selectById(vo.getAppointmentId());
        if (appointment == null) {
            return "关联的预约单不存在";
        }
        
        // Update Inventory
        part.setQuantity(part.getQuantity() - vo.getQuantity());
        if (!this.updateById(part)) {
            throw new RuntimeException("库存更新失败");
        }
        
        // Create Record
        PartsOutbound outbound = new PartsOutbound();
        BeanUtils.copyProperties(vo, outbound);
        outbound.setOperatorId(operatorId);
        outbound.setAppointmentId(vo.getAppointmentId());
        
        // Auto-fill customer name from appointment user
        Account user = accountMapper.selectById(appointment.getUserId());
        if (user != null) {
            outbound.setCustomerName(user.getUsername());
        } else {
            outbound.setCustomerName("Unknown (ID: " + appointment.getUserId() + ")");
        }
        
        outbound.setCreateTime(new Date());
        
        if (partsOutboundMapper.insert(outbound) > 0) {
            return null;
        } else {
            throw new RuntimeException("出库记录创建失败");
        }
    }

    @Override
    public IPage<PartsInbound> getInboundList(int pageNum, int pageSize) {
        Page<PartsInbound> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PartsInbound> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return partsInboundMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<PartsOutbound> getOutboundList(int pageNum, int pageSize) {
        Page<PartsOutbound> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PartsOutbound> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return partsOutboundMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public String deleteOutbound(int id) {
        PartsOutbound outbound = partsOutboundMapper.selectById(id);
        if (outbound == null) return "记录不存在";
        
        // Restore Inventory
        PartsInventory part = this.getById(outbound.getPartId());
        if (part != null) {
            part.setQuantity(part.getQuantity() + outbound.getQuantity());
            this.updateById(part);
        }
        
        if (partsOutboundMapper.deleteById(id) > 0) {
            return null;
        } else {
            throw new RuntimeException("删除失败");
        }
    }
}
