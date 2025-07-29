package com.zhanghongshen.model.wemedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WmNewsDto {

    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 频道id
     */
    private Long channelId;
    /**
     * 标签
     */
    private String labels;
    /**
     * 发布时间
     */
    private Date publishTime;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 文章封面类型  0 无图 1 单图 3 多图 -1 自动
     */
    private Short type;

    /**
     * 状态 提交为1  草稿为0
     */
    private Short status;

    /**
     * 封面图片列表 多张图以逗号隔开
     */
    private List<String> images;

    /**
     * 是否上架  0 下架  1 上架
     */
    private Short enable;

    @JsonProperty("submitedTime")
    private Date submitTime;

}
