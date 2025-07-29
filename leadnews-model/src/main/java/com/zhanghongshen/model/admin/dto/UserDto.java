package com.zhanghongshen.model.admin.dto;

import lombok.Data;

@Data
public class UserDto {

    /**
     * 用户名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
}
