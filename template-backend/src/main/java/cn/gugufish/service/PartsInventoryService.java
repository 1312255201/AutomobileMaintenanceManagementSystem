package cn.gugufish.service;

import cn.gugufish.entity.dto.PartsInventory;
import cn.gugufish.entity.vo.request.PartsInboundVO;
import cn.gugufish.entity.vo.request.PartsInventoryCreateVO;
import cn.gugufish.entity.vo.request.PartsInventoryUpdateVO;
import cn.gugufish.entity.vo.request.PartsOutboundVO;
import cn.gugufish.entity.vo.response.PartsInventoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PartsInventoryService extends IService<PartsInventory> {
    String createPart(PartsInventoryCreateVO vo);
    IPage<PartsInventoryVO> getPartList(int pageNum, int pageSize, String name, Integer categoryId, String brand);
    String updatePart(PartsInventoryUpdateVO vo);
    String deletePart(int id);
    String inbound(int operatorId, PartsInboundVO vo);
    String outbound(int operatorId, PartsOutboundVO vo);
}
