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

import java.util.List;

@RestController
@RequestMapping("/api/parts/inventory")
@Tag(name = "配件库存管理", description = "配件库存及出入库")
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
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestAttribute(value = "role", required = false) String role,
                                                          @RequestAttribute(value = "userId", required = false) Integer userId) {
        if ("repairman".equals(role)) {
            // repairman can only see their own sales
            // Assuming userId is what we need (the operatorId)
            return RestBean.success(partsInventoryService.getOutboundListByRepairman(page, size, userId));
        }
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

    @GetMapping("/low-stock")
    @Operation(summary = "获取低库存配件")
    public RestBean<List<PartsInventoryVO>> getLowStockParts(@RequestParam(defaultValue = "10") int threshold) {
        return RestBean.success(partsInventoryService.getLowStockParts(threshold));
    }

    @PostMapping("/inbound/batch")
    @Operation(summary = "批量入库(一键采购)")
    public RestBean<Void> batchInbound(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                       @RequestBody List<PartsInboundVO> list) {
        try {
            partsInventoryService.batchInbound(userId, list);
            return RestBean.success();
        } catch (RuntimeException e) {
            return RestBean.failure(400, e.getMessage());
        }
    }
}
