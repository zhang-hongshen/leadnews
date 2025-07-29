package com.zhanghongshen.search.service;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.search.dto.SearchDto;

import java.io.IOException;

public interface ArticleSearchService {
    ResponseResult search(SearchDto dto) throws IOException;
}
