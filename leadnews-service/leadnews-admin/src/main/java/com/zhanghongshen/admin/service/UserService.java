package com.zhanghongshen.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.admin.dto.UserDto;
import com.zhanghongshen.model.admin.pojo.User;

public interface UserService extends IService<User> {

    /**
     * 登录
     * @param dto
     * @return
     */
    ResponseResult login(UserDto dto);
}
