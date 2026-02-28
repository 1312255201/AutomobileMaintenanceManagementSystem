package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.response.AppointmentVO;
import cn.gugufish.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/appointment")
@Tag(name = "管理员-预约管理", description = "管理员获取预约信息")
public class AdminAppointmentController {

    @Resource
    AppointmentService appointmentService;

    @GetMapping("/active")
    @Operation(summary = "获取进行中的预约/维修单")
    public RestBean<List<AppointmentVO>> getActiveAppointments() {
        return RestBean.success(appointmentService.getActiveAppointments());
    }
}
