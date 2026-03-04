package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.*;
import cn.gugufish.entity.vo.request.ChatMessageCreateVO;
import cn.gugufish.entity.vo.response.ChatMessageVO;
import cn.gugufish.mapper.*;
import cn.gugufish.service.ChatService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatService {

    @Resource
    MaintenanceOrderMapper maintenanceOrderMapper;

    @Resource
    AppointmentMapper appointmentMapper;

    @Resource
    AccountMapper accountMapper;

    @Resource
    RepairmanMapper repairmanMapper;

    @Resource
    OrderReviewMapper orderReviewMapper;

    @Override
    public String sendMessage(int senderId, String senderRole, ChatMessageCreateVO vo) {
        // 1. Verify Order Existence
        MaintenanceOrder order = maintenanceOrderMapper.selectById(vo.getOrderId());
        if (order == null) return "维修单不存在";

        // 2. Check Permission based on Role
        if ("admin".equals(senderRole)) {
            // Admin can chat in any order
        } else if ("repairman".equals(senderRole)) {
            // Repairman must be the one assigned (need to map senderId (account id) to repairman id)
            QueryWrapper<Repairman> rWrapper = new QueryWrapper<>();
            rWrapper.eq("account_id", senderId);
            Repairman repairman = repairmanMapper.selectOne(rWrapper);
            if (repairman == null || !repairman.getId().equals(order.getRepairmanId())) {
                return "无权在此维修单发送消息";
            }
        } else {
            // User must be the owner of the appointment
            Appointment appointment = appointmentMapper.selectById(order.getAppointmentId());
            if (appointment == null || !appointment.getUserId().equals(senderId)) {
                return "无权在此维修单发送消息";
            }
        }

        // 3. Check if chat is closed (after review)
        QueryWrapper<OrderReview> reviewWrapper = new QueryWrapper<>();
        reviewWrapper.eq("order_id", vo.getOrderId());
        if (orderReviewMapper.exists(reviewWrapper)) {
            return "订单已评价，无法发送新消息";
        }
        
        // 4. Get Sender Info
        String senderName = "Unknown";
        String avatar = null;
        
        Account account = accountMapper.selectById(senderId);
        if (account != null) {
            senderName = account.getUsername();
            //avatar = account.getAvatar(); // Assuming Account has avatar, if not need to check AccountDetail or similar
            // If sender is repairman, maybe use repairman name?
            if ("repairman".equals(senderRole)) {
                 QueryWrapper<Repairman> rWrapper = new QueryWrapper<>();
                 rWrapper.eq("account_id", senderId);
                 Repairman r = repairmanMapper.selectOne(rWrapper);
                 if (r != null) senderName = r.getName();
            } else if ("admin".equals(senderRole)) {
                senderName = "管理员";
            }
        }

        // 5. Save Message
        ChatMessage message = new ChatMessage();
        message.setOrderId(vo.getOrderId());
        message.setSenderId(senderId);
        message.setSenderRole(senderRole);
        message.setSenderName(senderName);
        message.setAvatar(avatar);
        message.setContent(vo.getContent());
        message.setType(vo.getType());
        message.setCreateTime(new Date());

        if (this.save(message)) {
            return null;
        } else {
            return "消息发送失败";
        }
    }

    @Override
    public List<ChatMessageVO> getMessages(int orderId, int userId, String role) {
        // Permission check (similar to send, but maybe looser? No, strict.)
        MaintenanceOrder order = maintenanceOrderMapper.selectById(orderId);
        if (order == null) return List.of();

        if ("admin".equals(role)) {
            // OK
        } else if ("repairman".equals(role)) {
             QueryWrapper<Repairman> rWrapper = new QueryWrapper<>();
             rWrapper.eq("account_id", userId);
             Repairman repairman = repairmanMapper.selectOne(rWrapper);
             if (repairman == null || !repairman.getId().equals(order.getRepairmanId())) {
                 return List.of(); // No permission
             }
        } else {
            Appointment appointment = appointmentMapper.selectById(order.getAppointmentId());
            if (appointment == null || !appointment.getUserId().equals(userId)) {
                return List.of(); // No permission
            }
        }

        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.orderByAsc("create_time");
        
        return this.list(queryWrapper).stream().map(msg -> {
            ChatMessageVO vo = new ChatMessageVO();
            BeanUtils.copyProperties(msg, vo);
            vo.setIsSelf(msg.getSenderId().equals(userId) && msg.getSenderRole().equals(role)); // Simple self check
            // Note: senderId might conflict if admin and user have same ID (different tables? No, account table is shared usually).
            // But role check adds safety.
            return vo;
        }).collect(Collectors.toList());
    }
}
