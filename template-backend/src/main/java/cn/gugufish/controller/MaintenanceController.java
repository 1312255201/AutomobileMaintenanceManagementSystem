package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.OrderCreateVO;
import cn.gugufish.entity.vo.response.MaintenanceOrderVO;
import cn.gugufish.service.MaintenanceOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/maintenance")
@Tag(name = "管理员-维修管理", description = "维修单管理")
public class MaintenanceController {

    @Resource
    MaintenanceOrderService maintenanceOrderService;

    @GetMapping("/list")
    @Operation(summary = "获取维修单列表")
    public RestBean<List<MaintenanceOrderVO>> getOrderList() {
        return RestBean.success(maintenanceOrderService.getOrderList());
    }

    @PostMapping("/create")
    @Operation(summary = "创建维修单（处理预约）")
    public RestBean<Void> createOrder(@RequestBody @Valid OrderCreateVO vo) {
        return messageHandle(() -> maintenanceOrderService.createOrder(vo));
    }

    private <T> RestBean<T> messageHandle(java.util.function.Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
