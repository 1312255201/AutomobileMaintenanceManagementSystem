package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.VehicleCreateVO;
import cn.gugufish.entity.vo.request.VehicleUpdateVO;
import cn.gugufish.entity.vo.response.VehicleVO;
import cn.gugufish.service.VehicleService;
import cn.gugufish.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/vehicle")
@Tag(name = "用户-车辆管理", description = "用户车辆信息的增删改查")
public class VehicleController {

    @Resource
    VehicleService vehicleService;

    @GetMapping("/list")
    @Operation(summary = "获取我的车辆列表")
    public RestBean<List<VehicleVO>> getVehicleList(@RequestAttribute(Const.ATTR_USER_ID) int uid) {
        return RestBean.success(vehicleService.getVehicleList(uid));
    }

    @PostMapping("/create")
    @Operation(summary = "添加车辆")
    public RestBean<Void> createVehicle(@RequestAttribute(Const.ATTR_USER_ID) int uid,
                                        @RequestBody @Valid VehicleCreateVO vo) {
        return messageHandle(() -> vehicleService.createVehicle(uid, vo));
    }

    @PostMapping("/update")
    @Operation(summary = "更新车辆信息")
    public RestBean<Void> updateVehicle(@RequestAttribute(Const.ATTR_USER_ID) int uid,
                                        @RequestBody @Valid VehicleUpdateVO vo) {
        return messageHandle(() -> vehicleService.updateVehicle(uid, vo));
    }

    @PostMapping("/delete")
    @Operation(summary = "删除车辆")
    public RestBean<Void> deleteVehicle(@RequestAttribute(Const.ATTR_USER_ID) int uid,
                                        @RequestParam int id) {
        return messageHandle(() -> vehicleService.deleteVehicle(uid, id));
    }

    private <T> RestBean<T> messageHandle(java.util.function.Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
