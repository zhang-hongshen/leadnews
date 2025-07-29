package com.zhanghongshen.wemedia.controller;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import com.zhanghongshen.model.wemedia.dto.WmMaterialDto;
import com.zhanghongshen.model.wemedia.pojo.WmMaterial;
import com.zhanghongshen.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {


    @Autowired
    private WmMaterialService wmMaterialService;


    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile){
        if(multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID);
        }

        return wmMaterialService.uploadPicture(multipartFile);
    }

    @PostMapping("/list")
    public ResponseResult<List<WmMaterial>> listMaterial(@RequestBody WmMaterialDto dto){
        return wmMaterialService.listMaterial(dto);
    }
}
