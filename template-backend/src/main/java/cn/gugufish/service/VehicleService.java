package cn.gugufish.service;

import cn.gugufish.entity.dto.Vehicle;
import cn.gugufish.entity.vo.request.VehicleCreateVO;
import cn.gugufish.entity.vo.request.VehicleUpdateVO;
import cn.gugufish.entity.vo.response.VehicleVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface VehicleService extends IService<Vehicle> {
    List<VehicleVO> getVehicleList(int uid);
    String createVehicle(int uid, VehicleCreateVO vo);
    String updateVehicle(int uid, VehicleUpdateVO vo);
    String deleteVehicle(int uid, int id);
}
