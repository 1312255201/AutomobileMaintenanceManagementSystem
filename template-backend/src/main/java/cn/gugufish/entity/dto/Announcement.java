package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_announcement")
public class Announcement implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String title;
    String content;
    String cover;
    String type; // notice, activity
    Integer status; // 1: published, 0: draft
    Date createTime;
    Date updateTime;
}
