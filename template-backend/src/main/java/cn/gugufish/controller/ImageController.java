package cn.gugufish.controller;


import cn.gugufish.entity.RestBean;
import cn.gugufish.service.ImageService;
import cn.gugufish.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传管理控制器
 * 提供图片文件的上传和管理功能，支持多种图片使用场景
 *
 * 主要功能：
 * - 临时图片上传：用于论坛帖子、评论中的图片上传
 * - 头像上传：用户个人头像的上传和更新
 *
 * 安全特性：
 * - 文件大小限制：防止超大文件上传
 * - 文件类型验证：仅允许图片格式
 * - 用户身份验证：需要登录后才能上传
 *
 * 存储方式：使用MinIO对象存储服务保存图片文件
 *
 * @author GuguFish
 */
@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageController {

    /**
     * 图片服务类
     * 提供图片上传、处理、存储等核心业务逻辑
     */
    @Resource
    ImageService imageService;

    /**
     * 上传临时图片文件
     * 主要用于论坛帖子、评论等内容中的图片上传
     * 上传的图片会存储到MinIO并返回访问URL
     *
     * 限制条件：
     * - 文件大小不超过5MB
     * - 必须是有效的图片格式
     *
     * @param id 当前登录用户的ID（从JWT令牌中获取）
     * @param file 要上传的图片文件
     * @param response HTTP响应对象，用于设置错误状态码
     * @return 上传结果，成功返回图片访问URL，失败返回错误信息
     * @throws Exception 文件处理过程中可能出现的异常
     */
    @PostMapping("cache")
    public RestBean<String> uploadImage(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                        @RequestParam("file") MultipartFile file,
                                        HttpServletResponse response) throws Exception{
        if(file.getSize() > 1024 * 1024 * 5)
            return RestBean.failure(400, "图像大小最大不能大于5MB");

        log.info("用户{}正在进行图片上传操作", id);
        String url = imageService.uploadImage(id, file);

        if(url != null){
            log.info("用户{}图片上传成功，大小：{}", id, file.getSize());
            // Since ObjectController handles /images/**, we should return a URL that maps to it.
            // ImageServiceImpl returns path like "/cache/..."
            // We should prepend "/images" to it.
            return RestBean.success("/images" + url);
        } else {
            response.setStatus(400);
            return RestBean.failure(400, "图片上传失败，请联系管理员！");
        }
    }
}
