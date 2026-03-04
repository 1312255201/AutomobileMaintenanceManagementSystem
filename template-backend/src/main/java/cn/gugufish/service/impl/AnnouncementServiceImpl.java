package cn.gugufish.service.impl;

import cn.gugufish.entity.dto.Announcement;
import cn.gugufish.entity.vo.request.AnnouncementCreateVO;
import cn.gugufish.entity.vo.request.AnnouncementUpdateVO;
import cn.gugufish.entity.vo.response.AnnouncementVO;
import cn.gugufish.mapper.AnnouncementMapper;
import cn.gugufish.service.AnnouncementService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public String createAnnouncement(AnnouncementCreateVO vo) {
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(vo, announcement);
        if (announcement.getStatus() == null) {
            announcement.setStatus(1); // Default published
        }
        announcement.setCreateTime(new Date());
        announcement.setUpdateTime(new Date());
        
        if (this.save(announcement)) {
            return null;
        } else {
            return "发布失败";
        }
    }

    @Override
    public String updateAnnouncement(AnnouncementUpdateVO vo) {
        Announcement announcement = this.getById(vo.getId());
        if (announcement == null) return "公告不存在";
        
        if (vo.getTitle() != null) announcement.setTitle(vo.getTitle());
        if (vo.getContent() != null) announcement.setContent(vo.getContent());
        if (vo.getCover() != null) announcement.setCover(vo.getCover());
        if (vo.getType() != null) announcement.setType(vo.getType());
        if (vo.getStatus() != null) announcement.setStatus(vo.getStatus());
        
        announcement.setUpdateTime(new Date());
        
        if (this.updateById(announcement)) {
            return null;
        } else {
            return "更新失败";
        }
    }

    @Override
    public String deleteAnnouncement(int id) {
        if (this.removeById(id)) {
            return null;
        } else {
            return "删除失败";
        }
    }

    @Override
    public List<AnnouncementVO> getAllAnnouncements() {
        return this.list(new QueryWrapper<Announcement>().orderByDesc("create_time"))
                .stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<AnnouncementVO> getPublishedAnnouncements() {
        return this.list(new QueryWrapper<Announcement>()
                .eq("status", 1)
                .orderByDesc("create_time"))
                .stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    private AnnouncementVO convertToVO(Announcement announcement) {
        AnnouncementVO vo = new AnnouncementVO();
        BeanUtils.copyProperties(announcement, vo);
        return vo;
    }
}
