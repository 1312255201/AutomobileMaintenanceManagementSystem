package cn.gugufish.entity.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PartsCategoryCreateVO {
    @NotBlank(message = "分类名称不能为空")
    String name;
    String description;
}
