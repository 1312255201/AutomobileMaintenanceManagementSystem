package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class SupplierCreateVO {
    @NotBlank(message = "供应商名称不能为空")
    String name;
    String address;
    String contactName;
    @NotBlank(message = "联系电话不能为空")
    String contactPhone;
    @Email(message = "邮箱格式不正确")
    String email;
    String businessLicense;
    String taxRegistration;
    Date cooperationDate;
    String bankAccount;
    String bankName;
    String accountHolder;
    Integer status;
    String remark;
}
