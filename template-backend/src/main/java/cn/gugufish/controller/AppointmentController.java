package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.AppointmentCreateVO;
import cn.gugufish.entity.vo.response.AppointmentVO;
import cn.gugufish.service.AppointmentService;
import cn.gugufish.utils.Const;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointment")
@Tag(name = "预约管理", description = "用户发起预约等操作")
public class AppointmentController {

    @Resource
    AppointmentService appointmentService;

    @PostMapping("/create")
    @Operation(summary = "发起预约")
    public RestBean<Void> createAppointment(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                            @RequestBody @Valid AppointmentCreateVO vo) {
        String message = appointmentService.createAppointment(userId, vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取预约列表")
    public RestBean<IPage<AppointmentVO>> getAppointmentList(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return RestBean.success(appointmentService.getAppointmentList(userId, page, size));
    }
}
