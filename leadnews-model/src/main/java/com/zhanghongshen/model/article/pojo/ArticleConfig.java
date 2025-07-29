package com.zhanghongshen.model.article.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhanghongshen.model.common.BaseEntity;
import lombok.Data;

@Data
@TableName("ap_article_config")
public class ArticleConfig extends BaseEntity {

    public ArticleConfig(Long articleId){
        this.articleId = articleId;
        this.commentable = true;
        this.forwardable = true;
        this.down = false;
        this.deleted = false;
    }

    /**
     * 文章id
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 是否可评论
     * true: 可以评论   1
     * false: 不可评论  0
     */
    @TableField("is_comment")
    private Boolean commentable;

    /**
     * 是否转发
     * true: 可以转发   1
     * false: 不可转发  0
     */
    @TableField("is_forward")
    private Boolean forwardable;


    @TableField("is_down")
    private Boolean down;


    @TableField("is_delete")
    private Boolean deleted;

}
