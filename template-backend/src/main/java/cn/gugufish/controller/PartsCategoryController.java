package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.PartsCategoryCreateVO;
import cn.gugufish.entity.vo.request.PartsCategoryUpdateVO;
import cn.gugufish.entity.vo.response.PartsCategoryVO;
import cn.gugufish.service.PartsCategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/parts/category")
@Tag(name = "管理员-配件分类管理", description = "管理员管理配件分类信息")
public class PartsCategoryController {

    @Resource
    PartsCategoryService partsCategoryService;

    @PostMapping("/create")
    @Operation(summary = "添加配件分类")
    public RestBean<Void> createCategory(@RequestBody @Valid PartsCategoryCreateVO vo) {
        String message = partsCategoryService.createCategory(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取配件分类列表")
    public RestBean<IPage<PartsCategoryVO>> getCategoryList(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(required = false) String name) {
        return RestBean.success(partsCategoryService.getCategoryList(page, size, name));
    }

    @PostMapping("/update")
    @Operation(summary = "更新配件分类信息")
    public RestBean<Void> updateCategory(@RequestBody @Valid PartsCategoryUpdateVO vo) {
        String message = partsCategoryService.updateCategory(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除配件分类")
    public RestBean<Void> deleteCategory(@RequestParam int id) {
        String message = partsCategoryService.deleteCategory(id);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }
}
