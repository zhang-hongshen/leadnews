package com.zhanghongshen.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhanghongshen.common.dto.PageResponseResult;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.wemedia.dto.WmMaterialDto;
import com.zhanghongshen.model.wemedia.pojo.WmMaterial;
import com.zhanghongshen.wemedia.context.UserContextHolder;
import com.zhanghongshen.wemedia.mapper.WmMaterialMapper;
import com.zhanghongshen.wemedia.service.WmMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.minio.MinioTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial>
        implements WmMaterialService {

    private final MinioTemplate minioTemplate;

    @Override
    public ResponseResult<WmMaterial> uploadPicture(MultipartFile multipartFile) {

        String fileName = UUID.randomUUID().toString().replace("-", "");
        String extensionName = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String url = null;
        try {
            url = minioTemplate.uploadImageFile(fileName + "." + extensionName, multipartFile.getInputStream());
            log.info("upload image file to minio succeed，url: {}",url);
        } catch (IOException e) {
            log.error("upload image file to minio failed", e);
        }

        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(UserContextHolder.getUserId());
        wmMaterial.setUrl(url);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setType((short)0);
        save(wmMaterial);
        return ResponseResult.success(wmMaterial);
    }

    @Override
    public ResponseResult<List<WmMaterial>> listMaterial(WmMaterialDto dto) {
        IPage<WmMaterial> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WmMaterial::getUserId, UserContextHolder.getUserId())
                .orderByDesc(WmMaterial::getCreateTime);

        if(dto.getIsCollection() != null && dto.getIsCollection() == 1){
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }

        page = page(page,lambdaQueryWrapper);

        ResponseResult<List<WmMaterial>> responseResult = new PageResponseResult<>(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }
}
