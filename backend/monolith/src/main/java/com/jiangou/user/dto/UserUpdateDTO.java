package com.jiangou.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UserUpdateDTO {

    @Size(max = 64)
    private String displayName;

    @Email
    @Size(max = 255)
    private String email;

    @Pattern(regexp = "active|disabled", message = "状态仅允许 active 或 disabled")
    private String status;

    private List<@Pattern(regexp = "USER|ADMIN") String> roles;
}
