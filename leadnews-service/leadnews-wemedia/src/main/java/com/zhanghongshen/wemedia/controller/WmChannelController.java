package com.zhanghongshen.wemedia.controller;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.wemedia.pojo.WmChannel;
import com.zhanghongshen.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {


    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult<List<WmChannel>> findAll(){
        return wmChannelService.listAll();
    }
}
