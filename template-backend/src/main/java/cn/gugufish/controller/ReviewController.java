package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.ReviewCreateVO;
import cn.gugufish.entity.vo.response.ReviewVO;
import cn.gugufish.service.ReviewService;
import cn.gugufish.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@Tag(name = "评价管理", description = "用户评价维修服务")
public class ReviewController {

    @Resource
    ReviewService reviewService;

    @PostMapping("/create")
    @Operation(summary = "提交评价")
    public RestBean<Void> createReview(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                       @RequestBody @Valid ReviewCreateVO vo) {
        String message = reviewService.createReview(userId, vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "获取维修单的评价")
    public RestBean<ReviewVO> getReview(@PathVariable int orderId) {
        return RestBean.success(reviewService.getReviewByOrderId(orderId));
    }
}
