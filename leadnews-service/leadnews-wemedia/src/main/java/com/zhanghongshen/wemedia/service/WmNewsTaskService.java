package com.zhanghongshen.wemedia.service;

import java.util.Date;

public interface WmNewsTaskService {


    /**
     * 添加任务到延迟队列中
     * @param newsId  文章的id
     * @param publishTime  发布的时间  可以做为任务的执行时间
     */
    void addNewsToTask(Long newsId, Date publishTime);

    void scanNewsByTask();
}
