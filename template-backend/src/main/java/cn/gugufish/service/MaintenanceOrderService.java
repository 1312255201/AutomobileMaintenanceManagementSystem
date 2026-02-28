package cn.gugufish.service;

import cn.gugufish.entity.dto.MaintenanceOrder;
import cn.gugufish.entity.vo.request.MaintenanceItemCreateVO;
import cn.gugufish.entity.vo.request.OrderCreateVO;
import cn.gugufish.entity.vo.response.MaintenanceOrderVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MaintenanceOrderService extends IService<MaintenanceOrder> {
    List<MaintenanceOrderVO> getOrderList();
    MaintenanceOrderVO getOrderDetail(int id);
    String createOrder(OrderCreateVO vo);
    String addItem(MaintenanceItemCreateVO vo);
    String deleteItem(int itemId);
    String completeOrder(int orderId);
    String payOrder(int orderId);
    List<MaintenanceOrderVO> getActiveOrders();
}
