package cn.gugufish.service;

import cn.gugufish.entity.dto.Client;
import cn.gugufish.entity.vo.request.ClientUpdateVO;
import cn.gugufish.entity.vo.response.ClientVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ClientService extends IService<Client> {
    ClientVO getClientInfo(int uid);
    void updateClientInfo(int uid, ClientUpdateVO vo);
}
