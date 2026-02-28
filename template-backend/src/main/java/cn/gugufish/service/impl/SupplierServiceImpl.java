package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Supplier;
import cn.gugufish.entity.vo.request.SupplierCreateVO;
import cn.gugufish.entity.vo.request.SupplierUpdateVO;
import cn.gugufish.entity.vo.response.SupplierVO;
import cn.gugufish.mapper.SupplierMapper;
import cn.gugufish.service.SupplierService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Override
    public String createSupplier(SupplierCreateVO vo) {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(vo, supplier);
        if (supplier.getStatus() == null) {
            supplier.setStatus(0); // Default active
        }
        supplier.setCreateTime(new Date());
        
        if (this.save(supplier)) {
            return null;
        } else {
            return "供应商创建失败";
        }
    }

    @Override
    public IPage<SupplierVO> getSupplierList(int pageNum, int pageSize, String name) {
        Page<Supplier> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("create_time");
        
        this.page(page, queryWrapper);
        
        return page.convert(supplier -> {
            SupplierVO vo = new SupplierVO();
            BeanUtils.copyProperties(supplier, vo);
            return vo;
        });
    }

    @Override
    public String updateSupplier(SupplierUpdateVO vo) {
        Supplier supplier = this.getById(vo.getId());
        if (supplier == null) return "供应商不存在";
        
        BeanUtils.copyProperties(vo, supplier);
        
        if (this.updateById(supplier)) {
            return null;
        } else {
            return "更新失败";
        }
    }

    @Override
    public String deleteSupplier(int id) {
        if (this.removeById(id)) {
            return null;
        } else {
            return "删除失败";
        }
    }
}
