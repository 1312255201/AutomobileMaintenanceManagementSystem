package cn.gugufish.service;

import cn.gugufish.entity.dto.ChatMessage;
import cn.gugufish.entity.vo.request.ChatMessageCreateVO;
import cn.gugufish.entity.vo.response.ChatMessageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ChatService extends IService<ChatMessage> {
    String sendMessage(int senderId, String senderRole, ChatMessageCreateVO vo);
    List<ChatMessageVO> getMessages(int orderId, int userId, String role);
}
