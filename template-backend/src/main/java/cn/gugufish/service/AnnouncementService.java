package cn.gugufish.service;

import cn.gugufish.entity.dto.Announcement;
import cn.gugufish.entity.vo.request.AnnouncementCreateVO;
import cn.gugufish.entity.vo.request.AnnouncementUpdateVO;
import cn.gugufish.entity.vo.response.AnnouncementVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    String createAnnouncement(AnnouncementCreateVO vo);
    String updateAnnouncement(AnnouncementUpdateVO vo);
    String deleteAnnouncement(int id);
    List<AnnouncementVO> getAllAnnouncements(); // Admin sees all
    List<AnnouncementVO> getPublishedAnnouncements(); // User sees published
}
