package com.zhanghongshen.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.wemedia.dto.WmMaterialDto;
import com.zhanghongshen.model.wemedia.pojo.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WmMaterialService extends IService<WmMaterial> {

    ResponseResult<WmMaterial> uploadPicture(MultipartFile multipartFile);

    ResponseResult<List<WmMaterial>> listMaterial(WmMaterialDto dto);
}
