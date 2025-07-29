package com.zhanghongshen.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhanghongshen.model.article.dto.ArticleHomeDto;
import com.zhanghongshen.model.article.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     *
     * @param loadType  1: load more
     *                  2: load new
     */

    List<Article> listArticle(ArticleHomeDto dto, short loadType);

    List<Article> listArticleAfter(@Param("date") Date date);
}
