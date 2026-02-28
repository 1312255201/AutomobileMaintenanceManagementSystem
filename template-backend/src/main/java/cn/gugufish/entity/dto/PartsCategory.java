package cn.gugufish.entity.dto;

import cn.gugufish.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 配件类型实体类
 */
@Data
@TableName("db_parts_category")
@AllArgsConstructor
@NoArgsConstructor
public class PartsCategory implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String name;
    String description;
    Date createTime;
}
