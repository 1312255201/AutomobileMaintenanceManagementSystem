package cn.gugufish.service;

import cn.gugufish.entity.dto.Repairman;
import cn.gugufish.entity.vo.request.RepairmanCreateVO;
import cn.gugufish.entity.vo.response.RepairmanVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RepairmanService extends IService<Repairman> {
    String createRepairman(RepairmanCreateVO vo);
    IPage<RepairmanVO> getRepairmanList(int pageNum, int pageSize);
    String updateRepairman(RepairmanVO vo);
    String deleteRepairman(int id);
}
