package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.ClientUpdateVO;
import cn.gugufish.entity.vo.response.ClientVO;
import cn.gugufish.service.ClientService;
import cn.gugufish.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/client")
@Tag(name = "用户-个人信息管理", description = "用户获取和修改个人信息")
public class ClientController {

    @Resource
    ClientService clientService;

    @GetMapping("/info")
    @Operation(summary = "获取个人信息")
    public RestBean<ClientVO> getClientInfo(@RequestAttribute(Const.ATTR_USER_ID) int uid) {
        return RestBean.success(clientService.getClientInfo(uid));
    }

    @PostMapping("/update")
    @Operation(summary = "修改个人信息")
    public RestBean<Void> updateClientInfo(@RequestAttribute(Const.ATTR_USER_ID) int uid,
                                           @RequestBody ClientUpdateVO vo) {
        clientService.updateClientInfo(uid, vo);
        return RestBean.success();
    }
}
