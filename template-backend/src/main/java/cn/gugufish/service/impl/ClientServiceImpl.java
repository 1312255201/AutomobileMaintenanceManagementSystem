package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Client;
import cn.gugufish.entity.vo.request.ClientUpdateVO;
import cn.gugufish.entity.vo.response.ClientVO;
import cn.gugufish.mapper.ClientMapper;
import cn.gugufish.service.ClientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    @Override
    public ClientVO getClientInfo(int uid) {
        Client client = this.lambdaQuery().eq(Client::getUid, uid).one();
        if (client == null) {
            return null;
        }
        return client.asViewObject(ClientVO.class);
    }

    @Override
    public void updateClientInfo(int uid, ClientUpdateVO vo) {
        Client client = this.lambdaQuery().eq(Client::getUid, uid).one();
        if (client == null) {
            client = new Client();
            client.setUid(uid);
            client.setCreateTime(new Date());
        }
        client.setName(vo.getName());
        client.setGender(vo.getGender());
        client.setPhone(vo.getPhone());
        client.setWechat(vo.getWechat());
        client.setAddress(vo.getAddress());
        client.setUpdateTime(new Date());
        this.saveOrUpdate(client);
    }
}
