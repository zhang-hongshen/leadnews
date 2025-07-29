package com.zhanghongshen.apis.wemedia;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.wemedia.pojo.WmChannel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("leadnews-wemedia")
public interface WemediaClient {

    @GetMapping("/api/v1/channel/list")
    ResponseResult<List<WmChannel>> listChannel();
}
