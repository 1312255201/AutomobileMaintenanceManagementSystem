package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Account;
import cn.gugufish.entity.dto.MaintenanceOrder;
import cn.gugufish.entity.dto.OrderReview;
import cn.gugufish.entity.vo.request.ReviewCreateVO;
import cn.gugufish.entity.vo.response.ReviewVO;
import cn.gugufish.mapper.AccountMapper;
import cn.gugufish.mapper.AppointmentMapper;
import cn.gugufish.mapper.MaintenanceOrderMapper;
import cn.gugufish.mapper.OrderReviewMapper;
import cn.gugufish.service.ReviewService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReviewServiceImpl extends ServiceImpl<OrderReviewMapper, OrderReview> implements ReviewService {

    @Resource
    MaintenanceOrderMapper maintenanceOrderMapper;
    
    @Resource
    AppointmentMapper appointmentMapper;
    
    @Resource
    AccountMapper accountMapper;

    @Override
    public String createReview(int userId, ReviewCreateVO vo) {
        // Verify Order
        MaintenanceOrder order = maintenanceOrderMapper.selectById(vo.getOrderId());
        if (order == null) return "维修单不存在";
        
        // Check if order is paid (Status 3) or at least completed (Status 2)
        // User requested "Paid can be reviewed", so we should check for status 3
        if (order.getStatus() < 3) {
            return "维修单未支付，无法评价";
        }
        
        // Verify User (via Appointment)
        cn.gugufish.entity.dto.Appointment appointment = appointmentMapper.selectById(order.getAppointmentId());
        if (appointment == null || !appointment.getUserId().equals(userId)) {
            return "无权评价此订单";
        }
        
        // Check if already reviewed
        QueryWrapper<OrderReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", vo.getOrderId());
        if (this.count(queryWrapper) > 0) {
            return "您已评价过此订单";
        }
        
        OrderReview review = new OrderReview();
        review.setOrderId(vo.getOrderId());
        review.setUserId(userId);
        review.setRating(vo.getRating());
        review.setComment(vo.getComment());
        review.setCreateTime(new Date());
        
        if (this.save(review)) {
            return null;
        } else {
            return "评价提交失败";
        }
    }

    @Override
    public ReviewVO getReviewByOrderId(int orderId) {
        QueryWrapper<OrderReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        OrderReview review = this.getOne(queryWrapper);
        
        if (review == null) return null;
        
        ReviewVO vo = new ReviewVO();
        BeanUtils.copyProperties(review, vo);
        
        Account user = accountMapper.selectById(review.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        
        return vo;
    }
}
