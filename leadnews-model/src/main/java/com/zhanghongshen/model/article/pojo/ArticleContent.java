package com.zhanghongshen.model.article.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhanghongshen.model.common.BaseEntity;
import lombok.Data;

@Data
@TableName("ap_article_content")
public class ArticleContent  extends BaseEntity {

    /**
     * 文章id
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 文章内容
     */
    private String content;
}
