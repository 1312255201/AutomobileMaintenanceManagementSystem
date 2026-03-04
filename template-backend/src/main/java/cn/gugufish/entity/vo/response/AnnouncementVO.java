package cn.gugufish.entity.vo.response;

import lombok.Data;
import java.util.Date;

@Data
public class AnnouncementVO {
    private Integer id;
    private String title;
    private String content;
    private String cover;
    private String type;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
