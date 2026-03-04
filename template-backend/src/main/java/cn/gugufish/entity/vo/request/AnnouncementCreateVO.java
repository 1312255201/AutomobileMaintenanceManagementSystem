package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnouncementCreateVO {
    @NotNull
    private String title;
    @NotNull
    private String content;
    private String cover;
    @NotNull
    private String type; // notice, activity
    private Integer status;
}
