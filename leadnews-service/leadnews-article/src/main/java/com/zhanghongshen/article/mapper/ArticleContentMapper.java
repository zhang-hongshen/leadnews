package com.zhanghongshen.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhanghongshen.model.article.pojo.ArticleContent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleContentMapper
        extends BaseMapper<ArticleContent> {
}
