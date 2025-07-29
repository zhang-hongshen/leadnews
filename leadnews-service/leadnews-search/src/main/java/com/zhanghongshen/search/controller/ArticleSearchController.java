package com.zhanghongshen.search.controller;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.search.dto.SearchDto;
import com.zhanghongshen.search.service.ArticleSearchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/article/search")
@RequiredArgsConstructor
public class ArticleSearchController {


    private final ArticleSearchService articleSearchService;

    @PostMapping("/search")
    public ResponseResult search(@RequestBody SearchDto dto) throws IOException {
        if(dto == null || StringUtils.isBlank(dto.getKeyword())){
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID);
        }
        return articleSearchService.search(dto);
    }
}
