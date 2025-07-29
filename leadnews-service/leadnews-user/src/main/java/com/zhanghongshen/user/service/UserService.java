package com.zhanghongshen.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.user.dto.LoginDto;
import com.zhanghongshen.model.user.pojo.User;

import java.util.HashMap;

public interface UserService extends IService<User> {
    /**
     * app端登录功能
     * @param dto
     * @return
     */
    ResponseResult<HashMap<String, String>> login(LoginDto dto);
}
