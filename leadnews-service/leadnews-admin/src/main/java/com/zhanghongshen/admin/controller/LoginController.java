package com.zhanghongshen.admin.controller;


import com.zhanghongshen.admin.service.UserService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.admin.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/in")
    public ResponseResult login(@RequestBody UserDto dto){
        if(StringUtils.isBlank(dto.getName()) || StringUtils.isBlank(dto.getPassword())){
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID,
                    "Username or password is empty.");
        }
        return userService.login(dto);
    }
}
