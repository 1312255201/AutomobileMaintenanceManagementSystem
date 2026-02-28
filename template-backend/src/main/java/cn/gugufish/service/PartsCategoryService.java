package cn.gugufish.service;

import cn.gugufish.entity.dto.PartsCategory;
import cn.gugufish.entity.vo.request.PartsCategoryCreateVO;
import cn.gugufish.entity.vo.request.PartsCategoryUpdateVO;
import cn.gugufish.entity.vo.response.PartsCategoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PartsCategoryService extends IService<PartsCategory> {
    String createCategory(PartsCategoryCreateVO vo);
    IPage<PartsCategoryVO> getCategoryList(int pageNum, int pageSize, String name);
    String updateCategory(PartsCategoryUpdateVO vo);
    String deleteCategory(int id);
}
