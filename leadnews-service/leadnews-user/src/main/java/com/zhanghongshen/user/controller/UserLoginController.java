package com.zhanghongshen.user.controller;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.user.dto.LoginDto;
import com.zhanghongshen.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class UserLoginController {


    private final UserService apUserService;

    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto dto){
        return apUserService.login(dto);
    }
}
