package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnouncementUpdateVO {
    @NotNull
    private Integer id;
    private String title;
    private String content;
    private String cover;
    private String type;
    private Integer status;
}
