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
 * 维修工信息实体类
 */
@Data
@TableName("db_repairman")
@AllArgsConstructor
@NoArgsConstructor
public class Repairman implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer accountId;
    String name;
    String phone;
    String email;
    String introduction;
    String specialization;
    Date createTime;
}
