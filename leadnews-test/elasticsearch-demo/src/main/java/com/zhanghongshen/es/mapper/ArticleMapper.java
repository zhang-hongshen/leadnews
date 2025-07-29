package com.zhanghongshen.es.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhanghongshen.es.pojo.SearchArticleVo;
import com.zhanghongshen.model.article.pojo.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    List<SearchArticleVo> loadArticleList();

}
