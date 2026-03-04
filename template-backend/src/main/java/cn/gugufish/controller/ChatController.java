package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.ChatMessageCreateVO;
import cn.gugufish.entity.vo.response.ChatMessageVO;
import cn.gugufish.service.ChatService;
import cn.gugufish.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "聊天管理", description = "在线咨询聊天")
public class ChatController {

    @Resource
    ChatService chatService;

    @PostMapping("/send")
    @Operation(summary = "发送消息")
    public RestBean<Void> sendMessage(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                      @RequestAttribute(value = "role", required = false) String role,
                                      @RequestBody @Valid ChatMessageCreateVO vo) {
        String message = chatService.sendMessage(userId, role, vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/messages/{orderId}")
    @Operation(summary = "获取消息记录")
    public RestBean<List<ChatMessageVO>> getMessages(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                     @RequestAttribute(value = "role", required = false) String role,
                                                     @PathVariable int orderId) {
        return RestBean.success(chatService.getMessages(orderId, userId, role));
    }
}
