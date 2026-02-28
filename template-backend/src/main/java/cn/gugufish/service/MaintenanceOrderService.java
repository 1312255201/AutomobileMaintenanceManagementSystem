package cn.gugufish.service;

import cn.gugufish.entity.dto.MaintenanceOrder;
import cn.gugufish.entity.vo.request.OrderCreateVO;
import cn.gugufish.entity.vo.response.MaintenanceOrderVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MaintenanceOrderService extends IService<MaintenanceOrder> {
    List<MaintenanceOrderVO> getOrderList();
    String createOrder(OrderCreateVO vo);
}
