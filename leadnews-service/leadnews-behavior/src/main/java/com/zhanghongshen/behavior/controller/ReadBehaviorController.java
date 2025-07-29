package com.zhanghongshen.behavior.controller;


import com.zhanghongshen.behavior.service.ReadBehaviorService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.behavior.dto.ReadBehaviorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/read_behavior")
@RequiredArgsConstructor
public class ReadBehaviorController {

    private final ReadBehaviorService readBehaviorService;

    @PostMapping
    public ResponseResult readBehavior(@RequestBody ReadBehaviorDto dto) {
        if (dto == null || dto.getArticleId() == null) {
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID);
        }
        return readBehaviorService.readBehavior(dto);
    }
}
