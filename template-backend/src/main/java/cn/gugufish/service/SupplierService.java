package cn.gugufish.service;

import cn.gugufish.entity.dto.Supplier;
import cn.gugufish.entity.vo.request.SupplierCreateVO;
import cn.gugufish.entity.vo.request.SupplierUpdateVO;
import cn.gugufish.entity.vo.response.SupplierVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SupplierService extends IService<Supplier> {
    String createSupplier(SupplierCreateVO vo);
    IPage<SupplierVO> getSupplierList(int pageNum, int pageSize, String name);
    String updateSupplier(SupplierUpdateVO vo);
    String deleteSupplier(int id);
}
