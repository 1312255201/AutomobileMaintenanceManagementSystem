package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.CouponCreateVO;
import cn.gugufish.entity.vo.response.CouponVO;
import cn.gugufish.entity.vo.response.UserSelectionVO;
import cn.gugufish.service.CouponService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import cn.gugufish.utils.Const;

@RestController
@RequestMapping("/api/coupon")
@Tag(name = "优惠券管理", description = "优惠券发放与管理")
public class CouponController {

    @Resource
    CouponService couponService;

    @GetMapping("/my")
    @Operation(summary = "用户获取自己的优惠券")
    public RestBean<List<CouponVO>> getMyCoupons(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        return RestBean.success(couponService.getMyCoupons(userId));
    }

    @PostMapping("/create")
    @Operation(summary = "发放优惠券(批量)")
    public RestBean<Void> createCoupon(@RequestBody @Valid CouponCreateVO vo) {
        String message = couponService.createCoupon(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取优惠券列表")
    public RestBean<IPage<CouponVO>> getCouponList(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) Integer userId) {
        return RestBean.success(couponService.getCouponList(page, size, userId));
    }

    @GetMapping("/users")
    @Operation(summary = "获取可发放用户列表")
    public RestBean<List<UserSelectionVO>> getUserList() {
        return RestBean.success(couponService.getUserListForSelection());
    }

    @PostMapping("/delete")
    @Operation(summary = "删除/撤销优惠券")
    public RestBean<Void> deleteCoupon(@RequestParam int id) {
        String message = couponService.deleteCoupon(id);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }
}
