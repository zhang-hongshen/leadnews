package com.zhanghongshen.search.service;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.search.dto.HistorySearchDto;

public interface UserSearchHistoryService {

    void insert(String keyword, Long userId);

    ResponseResult listSearchHistory();

    ResponseResult deleteUserSearchHistory(HistorySearchDto dto);
}
