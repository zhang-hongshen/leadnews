package com.zhanghongshen.wemedia.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhanghongshen.apis.article.ArticleClient;
import com.zhanghongshen.model.article.dto.ArticleDto;
import com.zhanghongshen.model.wemedia.pojo.WmChannel;
import com.zhanghongshen.model.wemedia.pojo.WmSensitive;
import com.zhanghongshen.model.wemedia.pojo.WmUser;
import com.zhanghongshen.utils.SensitiveWordUtil;
import com.zhanghongshen.wemedia.mapper.WmChannelMapper;
import com.zhanghongshen.wemedia.mapper.WmNewsMapper;
import com.zhanghongshen.wemedia.mapper.WmSensitiveMapper;
import com.zhanghongshen.wemedia.mapper.WmUserMapper;
import com.zhanghongshen.wemedia.service.WmNewsAutoScanService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.wemedia.pojo.WmNews;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    private final WmNewsMapper wmNewsMapper;

    private final WmChannelMapper wmChannelMapper;

    private final WmUserMapper wmUserMapper;

    private final WmSensitiveMapper wmSensitiveMapper;

    private final ArticleClient articleClient;

    @Override
    @Async
    public void autoScanWmNews(Long id) {
        //1.查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if(wmNews == null){
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }
        if (!wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            return;
        }
        //从内容中提取纯文本内容和图片
        Map<String, Object> textAndImages = extractTextAndImages(wmNews);
        if(!scanSensitiveContent((String) textAndImages.get("content"), wmNews)) {
            return;
        }
        //4.审核成功，保存app端的相关的文章数据
        ResponseResult responseResult = saveAppArticle(wmNews);
        if(responseResult == null || !responseResult.isSuccess()){
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
        }

        wmNews.setArticleId((Long) responseResult.getData());
        updateWmNews(wmNews, WmNews.Status.PUBLISHED.getCode(),"审核成功");
    }

    private Map<String, Object> extractTextAndImages(WmNews wmNews) {

        //存储纯文本内容
        StringBuilder text = new StringBuilder();

        List<String> images = new ArrayList<>();

        //1。从自媒体文章的内容中提取文本和图片
        if (StringUtils.isNotBlank(wmNews.getContent())) {
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    text.append(map.get("value"));
                }

                if (map.get("type").equals("image")) {
                    images.add((String) map.get("value"));
                }
            }
        }
        //2.提取文章的封面图片
        if (StringUtils.isNotBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", text.toString());
        resultMap.put("images", images);
        return resultMap;

    }
    /**
     * 保存app端相关的文章数据
     * @param wmNews
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {

        ArticleDto dto = new ArticleDto();
        //属性的拷贝
        BeanUtils.copyProperties(wmNews, dto);
        //文章的布局
        dto.setLayout(wmNews.getType());
        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if(wmChannel != null){
            dto.setChannelName(wmChannel.getName());
        }

        //作者
        dto.setAuthorId(wmNews.getUserId());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if(wmUser != null){
            dto.setAuthorName(wmUser.getName());
        }

        //设置文章id
        if(wmNews.getArticleId() != null){
            dto.setArticleId(wmNews.getArticleId());
        }

        return articleClient.saveArticle(dto);
    }

    /**
     * 修改文章内容
     * @param wmNews
     * @param status
     * @param reason
     */
    private void updateWmNews(WmNews wmNews, short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 自管理的敏感词审核
     * @param content
     * @param wmNews
     * @return
     */
    private boolean scanSensitiveContent(String content, WmNews wmNews) {

        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery()
                .select(WmSensitive::getSensitives));
        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());

        //初始化敏感词库
        SensitiveWordUtil.initMap(sensitiveList);

        //查看文章中是否包含敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);
        if(!map.isEmpty()){
            updateWmNews(wmNews,(short) 2,"当前文章中存在违规内容" + map);
            return false;
        }
        return true;
    }
}
