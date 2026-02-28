package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Vehicle;
import cn.gugufish.entity.vo.request.VehicleCreateVO;
import cn.gugufish.entity.vo.request.VehicleUpdateVO;
import cn.gugufish.entity.vo.response.VehicleVO;
import cn.gugufish.mapper.VehicleMapper;
import cn.gugufish.service.VehicleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    @Override
    public List<VehicleVO> getVehicleList(int uid) {
        return this.lambdaQuery()
                .eq(Vehicle::getUid, uid)
                .list()
                .stream()
                .map(v -> v.asViewObject(VehicleVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String createVehicle(int uid, VehicleCreateVO vo) {
        Vehicle vehicle = vo.asViewObject(Vehicle.class);
        vehicle.setUid(uid);
        vehicle.setCreateTime(new Date());
        if (this.save(vehicle)) {
            return null;
        } else {
            return "Failed to add vehicle";
        }
    }

    @Override
    public String updateVehicle(int uid, VehicleUpdateVO vo) {
        Vehicle vehicle = this.getById(vo.getId());
        if (vehicle == null) {
            return "Vehicle not found";
        }
        if (vehicle.getUid() != uid) {
            return "Unauthorized operation";
        }
        
        vehicle.setLicensePlate(vo.getLicensePlate());
        vehicle.setVin(vo.getVin());
        vehicle.setEngineNumber(vo.getEngineNumber());
        vehicle.setBrand(vo.getBrand());
        vehicle.setModel(vo.getModel());
        vehicle.setColor(vo.getColor());
        vehicle.setMileage(vo.getMileage());
        vehicle.setYear(vo.getYear());
        vehicle.setUpdateTime(new Date());
        
        if (this.updateById(vehicle)) {
            return null;
        } else {
            return "Failed to update vehicle";
        }
    }

    @Override
    public String deleteVehicle(int uid, int id) {
        Vehicle vehicle = this.getById(id);
        if (vehicle == null) {
            return "Vehicle not found";
        }
        if (vehicle.getUid() != uid) {
            return "Unauthorized operation";
        }
        if (this.removeById(id)) {
            return null;
        } else {
            return "Failed to delete vehicle";
        }
    }
}
