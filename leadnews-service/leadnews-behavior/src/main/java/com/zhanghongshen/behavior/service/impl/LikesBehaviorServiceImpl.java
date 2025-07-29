package com.zhanghongshen.behavior.service.impl;

import com.alibaba.fastjson2.JSON;
import com.zhanghongshen.behavior.context.UserContextHolder;

import com.zhanghongshen.behavior.service.LikesBehaviorService;
import com.zhanghongshen.common.cache.CacheService;
import com.zhanghongshen.common.constants.BehaviorConstants;
import com.zhanghongshen.common.constants.TopicConstants;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.behavior.dto.LikesBehaviorDto;
import com.zhanghongshen.model.behavior.dto.UnLikesBehaviorDto;
import com.zhanghongshen.model.message.UpdateArticleMess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class LikesBehaviorServiceImpl implements LikesBehaviorService {

    private final CacheService cacheService;

    private final KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public ResponseResult like(LikesBehaviorDto dto) {

        Long userId = UserContextHolder.getUserId();

        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getArticleId());
        mess.setType(UpdateArticleMess.UpdateArticleType.LIKES);

        //3.点赞  保存数据
        if (dto.getOperation() == 0) {
            Object obj = cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), userId.toString());
            if (obj != null) {
                return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID, "已点赞");
            }
            // 保存当前key
            log.info("保存当前key:{} ,{}, {}", dto.getArticleId(), userId, dto);
            cacheService.hPut(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), userId.toString(), JSON.toJSONString(dto));
            mess.setDiff(1);
        } else {
            // 删除当前key
            log.info("删除当前key:{}, {}", dto.getArticleId(), userId);
            cacheService.hDelete(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), userId.toString());
            mess.setDiff(-1);
        }

        kafkaTemplate.send(TopicConstants.TOPIC_HOT_ARTICLE_SCORE, JSON.toJSONString(mess));

        return ResponseResult.success();

    }

    @Override
    public ResponseResult unLike(UnLikesBehaviorDto dto) {

        Long userId = UserContextHolder.getUserId();

        if (dto.getType() == 0) {
            cacheService.hPut(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId(), userId.toString(), com.alibaba.fastjson2.JSON.toJSONString(dto));
            log.info("hash put{} ,{}, {}", dto.getArticleId(), userId, dto);
        } else {
            cacheService.hDelete(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId(), userId.toString());
            log.info("删除当前key:{} ,{}, {}", dto.getArticleId(), userId, dto);
        }

        return ResponseResult.success();
    }

}
