package com.zhanghongshen.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.user.dto.LoginDto;
import com.zhanghongshen.model.user.pojo.User;
import com.zhanghongshen.user.mapper.ApUserMapper;
import com.zhanghongshen.user.service.UserService;
import com.zhanghongshen.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<ApUserMapper, User>
        implements UserService {
    /**
     * app端登录功能
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto dto) {
        //1.正常登录 用户名和密码
        if(StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())){
            //1.1 根据手机号查询用户信息
            User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, dto.getPhone()));
            if(user == null){
                return ResponseResult.error(AppHttpCodeEnum.DATA_NOT_EXIST,"User doesn't exist");
            }

            String password = DigestUtils.md5DigestAsHex((dto.getPassword() + user.getSalt()).getBytes());
            if(!password.equals(user.getPassword())){
                return ResponseResult.error(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }

            String token = JwtUtils.createToken(user.getId());
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            user.setSalt("");
            user.setPassword("");
            map.put("user", user);

            return ResponseResult.success(map);
        }

        Map<String,Object> map = new HashMap<>();
        map.put("token", JwtUtils.createToken(0L));
        return ResponseResult.success(map);
    }
}
