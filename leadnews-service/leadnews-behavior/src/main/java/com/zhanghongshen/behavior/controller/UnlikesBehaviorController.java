package com.zhanghongshen.behavior.controller;

import com.zhanghongshen.behavior.service.LikesBehaviorService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.behavior.dto.UnLikesBehaviorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/un_likes_behavior")
@RequiredArgsConstructor
public class UnlikesBehaviorController {

    private final LikesBehaviorService likesBehaviorService;

    @PostMapping
    public ResponseResult unLike(@RequestBody UnLikesBehaviorDto dto) {
        if (dto.getArticleId() == null) {
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID);
        }
        return likesBehaviorService.unLike(dto);
    }
}