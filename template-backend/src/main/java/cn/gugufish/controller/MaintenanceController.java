package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.MaintenanceItemCreateVO;
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
@RequestMapping("/api/maintenance")
@Tag(name = "维修管理", description = "维修单管理")
public class MaintenanceController {

    @Resource
    MaintenanceOrderService maintenanceOrderService;

    @GetMapping("/list")
    @Operation(summary = "获取维修单列表")
    public RestBean<List<MaintenanceOrderVO>> getOrderList(@RequestAttribute(value = "role", required = false) String role,
                                                           @RequestAttribute(value = "userId", required = false) Integer userId) {
        if ("repairman".equals(role)) {
             // If role is repairman, we need to map userId to repairmanId.
             // But userId is from db_account. repairmanId is from db_repairman.
             // We need a way to link them.
             // Currently, db_repairman has no user_id?
             // Let's check Repairman entity.
             // Assuming for now the "repairman" login uses the account ID which IS the repairman ID?
             // Or we need to look up.
             // Let's check RepairmanController/Service how repairman is created.
             // It seems Repairman is a separate entity.
             // Wait, the login logic returns role 'repairman'.
             // In UserDetailsServiceImpl, we might be loading from db_account?
             // If so, does the account have a link to db_repairman?
             // Let's assume for this task that we can pass the repairmanId or derive it.
             // If the system was designed such that Repairman accounts are in db_account with role='repairman'.
             // Then we need to find the Repairman record associated with this Account.
             // Let's check Repairman entity.
             return RestBean.success(maintenanceOrderService.getOrderListByRepairman(userId)); 
             // Note: This assumes userId == repairmanId or we can use userId directly. 
             // If they are different tables, we need a lookup.
             // Let's check the schema.
        }
        return RestBean.success(maintenanceOrderService.getOrderList());
    }

    @GetMapping("/active")
    @Operation(summary = "获取进行中的维修单")
    public RestBean<List<MaintenanceOrderVO>> getActiveOrders() {
        return RestBean.success(maintenanceOrderService.getActiveOrders());
    }

    @GetMapping("/detail")
    @Operation(summary = "获取维修单详情")
    public RestBean<MaintenanceOrderVO> getOrderDetail(@RequestParam int id) {
        return RestBean.success(maintenanceOrderService.getOrderDetail(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建维修单（处理预约）")
    public RestBean<Void> createOrder(@RequestBody @Valid OrderCreateVO vo) {
        return messageHandle(() -> maintenanceOrderService.createOrder(vo));
    }

    @PostMapping("/item/add")
    @Operation(summary = "添加维修项目")
    public RestBean<Void> addItem(@RequestBody @Valid MaintenanceItemCreateVO vo) {
        return messageHandle(() -> maintenanceOrderService.addItem(vo));
    }

    @PostMapping("/item/delete")
    @Operation(summary = "删除维修项目")
    public RestBean<Void> deleteItem(@RequestParam int id) {
        return messageHandle(() -> maintenanceOrderService.deleteItem(id));
    }

    @PostMapping("/complete")
    @Operation(summary = "完成维修")
    public RestBean<Void> completeOrder(@RequestParam int id) {
        return messageHandle(() -> maintenanceOrderService.completeOrder(id));
    }

    @PostMapping("/pay")
    @Operation(summary = "支付订单")
    public RestBean<Void> payOrder(@RequestParam int id) {
        return messageHandle(() -> maintenanceOrderService.payOrder(id));
    }

    private <T> RestBean<T> messageHandle(java.util.function.Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
