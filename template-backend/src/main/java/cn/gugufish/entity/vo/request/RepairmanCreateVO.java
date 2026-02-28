package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RepairmanCreateVO {
    @NotBlank(message = "用户名不能为空")
    String username;
    @NotBlank(message = "密码不能为空")
    String password;
    @NotBlank(message = "姓名不能为空")
    String name;
    @NotBlank(message = "手机号不能为空")
    String phone;
    @Email(message = "邮箱格式不正确")
    String email;
    String introduction;
    String specialization;
}
