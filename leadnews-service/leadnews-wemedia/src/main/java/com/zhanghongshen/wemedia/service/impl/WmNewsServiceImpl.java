package com.zhanghongshen.wemedia.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhanghongshen.common.constants.TopicConstants;
import com.zhanghongshen.common.constants.WemediaConstants;
import com.zhanghongshen.common.exception.CustomException;
import com.zhanghongshen.common.dto.PageResponseResult;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.wemedia.dto.WmNewsDto;
import com.zhanghongshen.model.wemedia.dto.WmNewsPageReqDto;
import com.zhanghongshen.model.wemedia.pojo.WmMaterial;
import com.zhanghongshen.model.wemedia.pojo.WmNews;
import com.zhanghongshen.model.wemedia.pojo.WmNewsMaterial;
import com.zhanghongshen.wemedia.context.UserContextHolder;
import com.zhanghongshen.wemedia.mapper.WmMaterialMapper;
import com.zhanghongshen.wemedia.mapper.WmNewsMapper;
import com.zhanghongshen.wemedia.mapper.WmNewsMaterialMapper;
import com.zhanghongshen.wemedia.service.WmNewsService;
import com.zhanghongshen.wemedia.service.WmNewsTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews>
        implements WmNewsService {

    private final WmNewsMaterialMapper wmNewsMaterialMapper;

    private final WmMaterialMapper wmMaterialMapper;

    private final WmNewsTaskService wmNewsTaskService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final TransactionTemplate transactionTemplate;

    /**
     * 查询文章
     * @param dto
     * @return
     */
    @Override
    public ResponseResult<List<WmNews>> listAll(WmNewsPageReqDto dto) {

        //获取当前登录人的信息
        Long userId = UserContextHolder.getUserId();
        if(userId == null){
            return ResponseResult.error(AppHttpCodeEnum.NEED_LOGIN);
        }

        //2.分页条件查询
        IPage<WmNews> page = new Page<>(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //状态精确查询
        if(dto.getStatus() != null){
            lambdaQueryWrapper.eq(WmNews::getStatus,dto.getStatus());
        }

        //频道精确查询
        if(dto.getChannelId() != null){
            lambdaQueryWrapper.eq(WmNews::getChannelId,dto.getChannelId());
        }

        //时间范围查询
        if(dto.getBeginPubDate() != null && dto.getEndPubDate() != null){
            lambdaQueryWrapper.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());
        }

        //关键字模糊查询
        if(StringUtils.isNotBlank(dto.getKeyword())){
            lambdaQueryWrapper.like(WmNews::getTitle,dto.getKeyword());
        }

        lambdaQueryWrapper.eq(WmNews::getUserId, userId)
                .orderByDesc(WmNews::getCreateTime);


        page = page(page,lambdaQueryWrapper);

        ResponseResult<List<WmNews>> responseResult = new PageResponseResult<>(dto.getPage(),dto.getSize(), (int)page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult submitNews(WmNewsDto dto) {

        //1.保存或修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);
        //封面图片  list---> string
        if(dto.getImages() != null && !dto.getImages().isEmpty()){
            //[1.jpg,2.jpg]-->   1.jpg,2.jpg
            String imageStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imageStr);
        }
        //如果当前封面类型为自动 -1
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }

        saveOrUpdateWmNews(wmNews);

        //2.判断是否为草稿  如果为草稿结束当前方法
        if(dto.getStatus().equals(WmNews.Status.NORMAL.getCode())){
            return ResponseResult.success();
        }

        //3.不是草稿，保存文章内容图片与素材的关系
        //获取到文章内容中的图片信息
        List<String> materials = extractUrlInfo(dto.getContent());
        saveRelativeInfoForContent(materials, wmNews.getId());

        //4.不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片
        saveRelativeInfoForCover(dto, wmNews, materials);

        wmNewsTaskService.addNewsToTask(wmNews.getId(), wmNews.getPublishTime());

        return ResponseResult.success();

    }

    /**
     * save or update news
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        wmNews.setUserId(UserContextHolder.getUserId());
        wmNews.setSubmitTime(new Date());
        wmNews.setEnable((short)1); //默认上架

        if(wmNews.getId() == null){
            save(wmNews);
        } else {
            updateById(wmNews);

            // TODO can be deleted asynchronously
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery()
                    .eq(WmNewsMaterial::getNewsId, wmNews.getId()));
        }
    }

    private List<String> extractUrlInfo(String content) {
        List<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if(map.get("type").equals("image")){
                String imgUrl = (String) map.get("value");
                materials.add(imgUrl);
            }
        }

        return materials;
    }

    private void saveRelativeInfoForContent(List<String> materials, Long newsId) {
        saveRelativeInfo(materials, newsId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {

        List<String> images = dto.getImages();

        //如果当前封面类型为自动，则设置封面类型的数据
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            int size = materials.size();
            if(size >= 3){
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            } else if(size >= 1){
                //单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            } else {
                //无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if(images != null && !images.isEmpty()){
                wmNews.setImages(StringUtils.join(images,","));
            }
            updateById(wmNews);
        }

        if(images != null && !images.isEmpty()){
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }
    }

    private void saveRelativeInfo(List<String> materialUrls, Long newsId, Short type) {
        if(materialUrls == null || materialUrls.isEmpty()){
            return;
        }
        List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery()
                .in(WmMaterial::getUrl, materialUrls));

        //判断素材是否有效
        if(dbMaterials == null || dbMaterials.isEmpty()){
            // 第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }

        if(materialUrls.size() != dbMaterials.size()){
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }

        List<Long> idList = dbMaterials.stream().map(WmMaterial::getId)
                .collect(Collectors.toList());

        //批量保存
        wmNewsMaterialMapper.saveRelations(idList, newsId, type);
    }



    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        WmNews wmNews = getById(dto.getId());
        if(wmNews == null){
            return ResponseResult.error(AppHttpCodeEnum.DATA_NOT_EXIST,"News doesn't exist.");
        }

        if(!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())){
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID,"News status is not published.");
        }

        if(dto.getEnable() != null && dto.getEnable() > -1 && dto.getEnable() < 2){
            update(Wrappers.<WmNews>lambdaUpdate().set(WmNews::getEnable,dto.getEnable())
                    .eq(WmNews::getId,wmNews.getId()));
            if(wmNews.getArticleId() != null){
                Map<String,Object> map = new HashMap<>();
                map.put("articleId",wmNews.getArticleId());
                map.put("enable",dto.getEnable());
                kafkaTemplate.send(TopicConstants.TOPIC_WM_NEWS_UP_OR_DOWN,JSON.toJSONString(map));
            }
        }
        return ResponseResult.success();
    }

    @Override
    public ResponseResult deleteNews(Long newsId) {

        WmNews news = getById(newsId);

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            removeById(newsId);
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery()
                    .eq(WmNewsMaterial::getNewsId,newsId));
        });

        if(news.getArticleId() != null){
            kafkaTemplate.send(TopicConstants.TOPIC_ARTICLE_DELETE, newsId.toString());
        }
        return ResponseResult.success();
    }
}
