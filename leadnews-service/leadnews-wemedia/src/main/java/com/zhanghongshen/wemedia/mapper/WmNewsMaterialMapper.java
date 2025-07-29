package com.zhanghongshen.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhanghongshen.model.wemedia.pojo.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

    void saveRelations(@Param("materialIds") List<Long> materialIds,
                       @Param("newsId") Long newsId,
                       @Param("type") Short type);
}
