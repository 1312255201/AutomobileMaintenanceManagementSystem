package cn.gugufish.controller;

import cn.gugufish.entity.RestBean;
import cn.gugufish.entity.vo.request.AnnouncementCreateVO;
import cn.gugufish.entity.vo.request.AnnouncementUpdateVO;
import cn.gugufish.entity.vo.response.AnnouncementVO;
import cn.gugufish.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcement")
@Tag(name = "公告管理", description = "公告与活动发布")
public class AnnouncementController {

    @Resource
    AnnouncementService announcementService;

    @GetMapping("/list/published")
    @Operation(summary = "获取已发布公告列表(用户端)")
    public RestBean<List<AnnouncementVO>> getPublishedList() {
        return RestBean.success(announcementService.getPublishedAnnouncements());
    }

    @GetMapping("/list/all")
    @Operation(summary = "获取所有公告列表(管理端)")
    public RestBean<List<AnnouncementVO>> getAllList() {
        return RestBean.success(announcementService.getAllAnnouncements());
    }

    @PostMapping("/create")
    @Operation(summary = "创建公告")
    public RestBean<Void> createAnnouncement(@RequestBody @Valid AnnouncementCreateVO vo) {
        String message = announcementService.createAnnouncement(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新公告")
    public RestBean<Void> updateAnnouncement(@RequestBody @Valid AnnouncementUpdateVO vo) {
        String message = announcementService.updateAnnouncement(vo);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }

    @GetMapping("/delete/{id}")
    @Operation(summary = "删除公告")
    public RestBean<Void> deleteAnnouncement(@PathVariable int id) {
        String message = announcementService.deleteAnnouncement(id);
        if (message == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, message);
        }
    }
}
