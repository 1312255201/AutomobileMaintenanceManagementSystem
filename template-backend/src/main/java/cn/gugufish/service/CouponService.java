package cn.gugufish.service;

import cn.gugufish.entity.dto.Coupon;
import cn.gugufish.entity.vo.request.CouponCreateVO;
import cn.gugufish.entity.vo.response.CouponVO;
import cn.gugufish.entity.vo.response.UserSelectionVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface CouponService extends IService<Coupon> {
    String createCoupon(CouponCreateVO vo);
    IPage<CouponVO> getCouponList(int pageNum, int pageSize, Integer userId);
    List<UserSelectionVO> getUserListForSelection();
    String deleteCoupon(int id);
    List<CouponVO> getMyCoupons(int userId);
}
