package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.SupplierCreateVO;
import cn.gugufish.entity.vo.request.SupplierUpdateVO;
import cn.gugufish.entity.vo.response.SupplierVO;
import cn.gugufish.service.SupplierService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/supplier")
@Tag(name = "管理员-供应商管理", description = "管理员管理供应商信息")
public class SupplierController {

    @Resource
    SupplierService supplierService;

    @PostMapping("/create")
    @Operation(summary = "添加供应商")
    public RestBean<Void> createSupplier(@RequestBody @Valid SupplierCreateVO vo) {
        String message = supplierService.createSupplier(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取供应商列表")
    public RestBean<IPage<SupplierVO>> getSupplierList(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) String name) {
        return RestBean.success(supplierService.getSupplierList(page, size, name));
    }

    @PostMapping("/update")
    @Operation(summary = "更新供应商信息")
    public RestBean<Void> updateSupplier(@RequestBody @Valid SupplierUpdateVO vo) {
        String message = supplierService.updateSupplier(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除供应商")
    public RestBean<Void> deleteSupplier(@RequestParam int id) {
        String message = supplierService.deleteSupplier(id);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }
}
