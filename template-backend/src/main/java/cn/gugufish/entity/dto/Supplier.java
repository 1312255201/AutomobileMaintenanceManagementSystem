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
 * 供应商信息实体类
 */
@Data
@TableName("db_supplier")
@AllArgsConstructor
@NoArgsConstructor
public class Supplier implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String name;
    String address;
    String contactName;
    String contactPhone;
    String email;
    String businessLicense;
    String taxRegistration;
    Date cooperationDate;
    String bankAccount;
    String bankName;
    String accountHolder;
    Integer status;
    String remark;
    Date createTime;
}
