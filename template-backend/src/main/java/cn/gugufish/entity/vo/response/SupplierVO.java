package cn.gugufish.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class SupplierVO {
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
