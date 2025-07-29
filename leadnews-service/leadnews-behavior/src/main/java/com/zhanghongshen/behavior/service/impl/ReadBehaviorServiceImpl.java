package com.zhanghongshen.behavior.service.impl;

import com.alibaba.fastjson2.JSON;
import com.zhanghongshen.behavior.context.UserContextHolder;
import com.zhanghongshen.behavior.service.ReadBehaviorService;
import com.zhanghongshen.common.cache.CacheService;
import com.zhanghongshen.common.constants.BehaviorConstants;
import com.zhanghongshen.common.constants.TopicConstants;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.behavior.dto.ReadBehaviorDto;
import com.zhanghongshen.model.message.UpdateArticleMess;
import com.zhanghongshen.model.user.pojo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReadBehaviorServiceImpl implements ReadBehaviorService {

    private CacheService cacheService;

    private KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public ResponseResult readBehavior(ReadBehaviorDto dto) {

        Long userId = UserContextHolder.getUserId();

        //更新阅读次数
        String readBehaviorJson = (String) cacheService.hGet(BehaviorConstants.READ_BEHAVIOR + dto.getArticleId().toString(), userId.toString());
        if (StringUtils.isNotBlank(readBehaviorJson)) {
            ReadBehaviorDto readBehaviorDto = JSON.parseObject(readBehaviorJson, ReadBehaviorDto.class);
            dto.setCount((short) (readBehaviorDto.getCount() + dto.getCount()));
        }
        // 保存当前key
        log.info("保存当前key:{} {} {}", dto.getArticleId(), userId, dto);
        cacheService.hPut(BehaviorConstants.READ_BEHAVIOR + dto.getArticleId().toString(), userId.toString(), JSON.toJSONString(dto));

        //发送消息，数据聚合

        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getArticleId());
        mess.setType(UpdateArticleMess.UpdateArticleType.VIEWS);
        mess.setDiff(1);
        kafkaTemplate.send(TopicConstants.TOPIC_HOT_ARTICLE_SCORE,JSON.toJSONString(mess));


        return ResponseResult.success();

    }
}
