package com.zhanghongshen.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.wemedia.dto.WmNewsDto;
import com.zhanghongshen.model.wemedia.dto.WmNewsPageReqDto;
import com.zhanghongshen.model.wemedia.pojo.WmNews;

import java.util.List;

public interface WmNewsService extends IService<WmNews> {

    ResponseResult<List<WmNews>> listAll(WmNewsPageReqDto dto);

    ResponseResult submitNews(WmNewsDto dto);

    ResponseResult downOrUp(WmNewsDto dto);

    ResponseResult deleteNews(Long newsId);
}
