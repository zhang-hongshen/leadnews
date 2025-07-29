package com.zhanghongshen.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zhanghongshen.admin.mapper.UserMapper;
import com.zhanghongshen.admin.service.UserService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.admin.dto.UserDto;
import com.zhanghongshen.model.admin.pojo.User;
import com.zhanghongshen.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public ResponseResult login(UserDto dto) {

        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getName, dto.getName()));
        if(user == null){
            return ResponseResult.error(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        String salt = user.getSalt();
        String pswd = dto.getPassword();
        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
        if(!pswd.equals(user.getPassword())){
            return ResponseResult.error(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        Map<String,Object> map  = new HashMap<>();
        map.put("token", JwtUtils.createToken(user.getId()));
        user.setSalt("");
        user.setPassword("");
        map.put("user", user);
        return ResponseResult.success(map);
    }
}
