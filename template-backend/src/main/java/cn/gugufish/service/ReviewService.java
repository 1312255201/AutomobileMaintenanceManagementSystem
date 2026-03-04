package cn.gugufish.service;

import cn.gugufish.entity.dto.OrderReview;
import cn.gugufish.entity.vo.request.ReviewCreateVO;
import cn.gugufish.entity.vo.response.ReviewVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ReviewService extends IService<OrderReview> {
    String createReview(int userId, ReviewCreateVO vo);
    ReviewVO getReviewByOrderId(int orderId);
}
