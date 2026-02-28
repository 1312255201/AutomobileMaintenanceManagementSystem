package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.PartsCategory;
import cn.gugufish.entity.vo.request.PartsCategoryCreateVO;
import cn.gugufish.entity.vo.request.PartsCategoryUpdateVO;
import cn.gugufish.entity.vo.response.PartsCategoryVO;
import cn.gugufish.mapper.PartsCategoryMapper;
import cn.gugufish.service.PartsCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PartsCategoryServiceImpl extends ServiceImpl<PartsCategoryMapper, PartsCategory> implements PartsCategoryService {

    @Override
    public String createCategory(PartsCategoryCreateVO vo) {
        if (this.baseMapper.exists(new QueryWrapper<PartsCategory>().eq("name", vo.getName()))) {
            return "分类名称已存在";
        }
        
        PartsCategory category = new PartsCategory();
        BeanUtils.copyProperties(vo, category);
        category.setCreateTime(new Date());
        
        if (this.save(category)) {
            return null;
        } else {
            return "创建失败";
        }
    }

    @Override
    public IPage<PartsCategoryVO> getCategoryList(int pageNum, int pageSize, String name) {
        Page<PartsCategory> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PartsCategory> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("create_time");
        
        this.page(page, queryWrapper);
        
        return page.convert(category -> {
            PartsCategoryVO vo = new PartsCategoryVO();
            BeanUtils.copyProperties(category, vo);
            return vo;
        });
    }

    @Override
    public String updateCategory(PartsCategoryUpdateVO vo) {
        PartsCategory category = this.getById(vo.getId());
        if (category == null) return "分类不存在";
        
        if (!category.getName().equals(vo.getName()) && 
            this.baseMapper.exists(new QueryWrapper<PartsCategory>().eq("name", vo.getName()))) {
            return "分类名称已存在";
        }
        
        BeanUtils.copyProperties(vo, category);
        
        if (this.updateById(category)) {
            return null;
        } else {
            return "更新失败";
        }
    }

    @Override
    public String deleteCategory(int id) {
        if (this.removeById(id)) {
            return null;
        } else {
            return "删除失败";
        }
    }
}
