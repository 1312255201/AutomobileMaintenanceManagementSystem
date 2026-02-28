package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Account;
import cn.gugufish.entity.dto.Coupon;
import cn.gugufish.entity.vo.request.CouponCreateVO;
import cn.gugufish.entity.vo.response.CouponVO;
import cn.gugufish.entity.vo.response.UserSelectionVO;
import cn.gugufish.mapper.AccountMapper;
import cn.gugufish.mapper.CouponMapper;
import cn.gugufish.service.CouponService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Resource
    AccountMapper accountMapper;

    @Override
    @Transactional
    public String createCoupon(CouponCreateVO vo) {
        if (vo.getUserIds() == null || vo.getUserIds().isEmpty()) {
            return "请选择至少一个用户";
        }

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, vo.getValidDays());
        Date validEnd = calendar.getTime();

        for (Integer userId : vo.getUserIds()) {
            Coupon coupon = new Coupon();
            coupon.setName(vo.getName());
            coupon.setDiscountAmount(vo.getDiscountAmount());
            coupon.setConditionAmount(vo.getConditionAmount());
            coupon.setUserId(userId);
            coupon.setValidStart(now);
            coupon.setValidEnd(validEnd);
            coupon.setStatus(0); // Unused
            coupon.setCreateTime(now);
            this.save(coupon);
        }

        return null;
    }

    @Override
    public IPage<CouponVO> getCouponList(int pageNum, int pageSize, Integer userId) {
        Page<Coupon> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("create_time");

        Page<Coupon> resultPage = this.page(page, queryWrapper);
        
        // Get user info map
        List<Integer> userIds = resultPage.getRecords().stream()
                .map(Coupon::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Integer, String> userMap;
        if (!userIds.isEmpty()) {
            userMap = accountMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(Account::getId, Account::getUsername));
        } else {
            userMap = Map.of();
        }

        return resultPage.convert(coupon -> {
            CouponVO vo = new CouponVO();
            BeanUtils.copyProperties(coupon, vo);
            vo.setUsername(userMap.getOrDefault(coupon.getUserId(), "Unknown"));
            
            // Check expiry
            if (coupon.getStatus() == 0 && new Date().after(coupon.getValidEnd())) {
                vo.setStatus(2); // Expired (Display only, DB update could be done via scheduled task)
            }
            
            return vo;
        });
    }

    @Override
    public List<UserSelectionVO> getUserListForSelection() {
        // Only select normal users (role = user)
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", "user");
        
        return accountMapper.selectList(queryWrapper).stream().map(account -> {
            UserSelectionVO vo = new UserSelectionVO();
            BeanUtils.copyProperties(account, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public String deleteCoupon(int id) {
        if (this.removeById(id)) {
            return null;
        } else {
            return "删除失败";
        }
    }

    @Override
    public List<CouponVO> getMyCoupons(int userId) {
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        // Only show Unused (0) and Used (1), or Expired (2)?
        // Let's show all, but sort by status (0 first) and expiry
        queryWrapper.orderByAsc("status").orderByAsc("valid_end");
        
        return this.list(queryWrapper).stream().map(coupon -> {
            CouponVO vo = new CouponVO();
            BeanUtils.copyProperties(coupon, vo);
            
            // Check expiry for display
            if (coupon.getStatus() == 0 && new Date().after(coupon.getValidEnd())) {
                vo.setStatus(2);
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
