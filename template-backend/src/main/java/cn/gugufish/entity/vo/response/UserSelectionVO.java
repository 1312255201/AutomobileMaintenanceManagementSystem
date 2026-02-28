package cn.gugufish.entity.vo.response;

import lombok.Data;

@Data
public class UserSelectionVO {
    private Integer id;
    private String username;
    private String email;
    private String role;
}
