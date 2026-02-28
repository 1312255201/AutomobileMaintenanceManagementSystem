package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.RepairmanCreateVO;
import cn.gugufish.entity.vo.response.RepairmanVO;
import cn.gugufish.service.RepairmanService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/repairman")
@Tag(name = "管理员-维修工管理", description = "管理员管理维修工信息")
public class RepairmanController {

    @Resource
    RepairmanService repairmanService;

    @PostMapping("/create")
    @Operation(summary = "添加维修工")
    public RestBean<Void> createRepairman(@RequestBody @Valid RepairmanCreateVO vo) {
        String message = repairmanService.createRepairman(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取维修工列表")
    public RestBean<IPage<RepairmanVO>> getRepairmanList(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return RestBean.success(repairmanService.getRepairmanList(page, size));
    }

    @PostMapping("/update")
    @Operation(summary = "更新维修工信息")
    public RestBean<Void> updateRepairman(@RequestBody @Valid RepairmanVO vo) {
        String message = repairmanService.updateRepairman(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除维修工")
    public RestBean<Void> deleteRepairman(@RequestParam int id) {
        String message = repairmanService.deleteRepairman(id);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }
}
