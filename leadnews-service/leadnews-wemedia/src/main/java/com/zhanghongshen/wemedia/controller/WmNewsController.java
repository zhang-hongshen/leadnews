package com.zhanghongshen.wemedia.controller;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.wemedia.dto.WmNewsDto;
import com.zhanghongshen.model.wemedia.dto.WmNewsPageReqDto;
import com.zhanghongshen.model.wemedia.pojo.WmNews;
import com.zhanghongshen.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {


    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult<List<WmNews>> findAll(@RequestBody WmNewsPageReqDto dto){
        if(dto == null){
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID);
        }
        return wmNewsService.listAll(dto);
    }


    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        if(dto == null || dto.getContent() == null){
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID);
        }
        return  wmNewsService.submitNews(dto);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto){
        if(dto.getId() == null){
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID);
        }
        return wmNewsService.downOrUp(dto);
    }

    @GetMapping("/one/{newsId}")
    public ResponseResult<WmNews> getNews(@PathVariable("newsId") Long newsId) {
        return ResponseResult.success(wmNewsService.getById(newsId));
    }

    @GetMapping("/del_news/{newsId}")
    public ResponseResult deleteNews(@PathVariable("newsId") Long newsId) {
        return wmNewsService.deleteNews(newsId);
    }
}
