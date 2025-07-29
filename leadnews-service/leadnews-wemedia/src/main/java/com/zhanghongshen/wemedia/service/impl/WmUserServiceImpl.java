package com.zhanghongshen.wemedia.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.wemedia.dto.WmLoginDto;
import com.zhanghongshen.model.wemedia.pojo.WmUser;
import com.zhanghongshen.utils.JwtUtils;
import com.zhanghongshen.wemedia.mapper.WmUserMapper;
import com.zhanghongshen.wemedia.service.WmUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser>
        implements WmUserService {

    @Override
    public ResponseResult login(WmLoginDto dto) {

        //2.查询用户
        WmUser wmUser = getOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, dto.getName()));
        if(wmUser == null){
            return ResponseResult.error(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //3.比对密码
        String salt = wmUser.getSalt();
        String password = dto.getPassword();
        password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        if(password.equals(wmUser.getPassword())){
            //4.返回数据  jwt
            Map<String,Object> map  = new HashMap<>();
            map.put("token", JwtUtils.createToken(wmUser.getId()));
            wmUser.setSalt("");
            wmUser.setPassword("");
            map.put("user",wmUser);
            return ResponseResult.success(map);
        }
        return ResponseResult.error(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
    }
}