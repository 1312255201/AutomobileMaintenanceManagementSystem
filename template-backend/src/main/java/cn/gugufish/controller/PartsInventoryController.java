package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.dto.PartsInbound;
import cn.gugufish.entity.dto.PartsOutbound;
import cn.gugufish.entity.vo.request.PartsInboundVO;
import cn.gugufish.entity.vo.request.PartsInventoryCreateVO;
import cn.gugufish.entity.vo.request.PartsInventoryUpdateVO;
import cn.gugufish.entity.vo.request.PartsOutboundVO;
import cn.gugufish.entity.vo.response.PartsInventoryVO;
import cn.gugufish.service.PartsInventoryService;
import cn.gugufish.utils.Const;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/parts/inventory")
@Tag(name = "管理员-配件库存管理", description = "管理员管理配件库存及出入库")
public class PartsInventoryController {

    @Resource
    PartsInventoryService partsInventoryService;

    @PostMapping("/create")
    @Operation(summary = "添加配件信息")
    public RestBean<Void> createPart(@RequestBody @Valid PartsInventoryCreateVO vo) {
        String message = partsInventoryService.createPart(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取配件列表")
    public RestBean<IPage<PartsInventoryVO>> getPartList(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) Integer categoryId,
                                                         @RequestParam(required = false) String brand) {
        return RestBean.success(partsInventoryService.getPartList(page, size, name, categoryId, brand));
    }

    @PostMapping("/update")
    @Operation(summary = "更新配件信息")
    public RestBean<Void> updatePart(@RequestBody @Valid PartsInventoryUpdateVO vo) {
        String message = partsInventoryService.updatePart(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除配件")
    public RestBean<Void> deletePart(@RequestParam int id) {
        String message = partsInventoryService.deletePart(id);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @PostMapping("/inbound")
    @Operation(summary = "配件入库")
    public RestBean<Void> inbound(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                  @RequestBody @Valid PartsInboundVO vo) {
        String message = partsInventoryService.inbound(userId, vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @PostMapping("/outbound")
    @Operation(summary = "配件出库(销售)")
    public RestBean<Void> outbound(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                   @RequestBody @Valid PartsOutboundVO vo) {
        String message = partsInventoryService.outbound(userId, vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/inbound/list")
    @Operation(summary = "获取入库记录")
    public RestBean<IPage<PartsInbound>> getInboundList(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return RestBean.success(partsInventoryService.getInboundList(page, size));
    }

    @GetMapping("/outbound/list")
    @Operation(summary = "获取出库记录")
    public RestBean<IPage<PartsOutbound>> getOutboundList(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        return RestBean.success(partsInventoryService.getOutboundList(page, size));
    }

    @PostMapping("/outbound/delete")
    @Operation(summary = "删除出库记录(恢复库存)")
    public RestBean<Void> deleteOutbound(@RequestParam int id) {
        String message = partsInventoryService.deleteOutbound(id);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }
}
