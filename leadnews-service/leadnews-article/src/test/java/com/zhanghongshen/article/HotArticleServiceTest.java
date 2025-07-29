package com.zhanghongshen.article;

import com.zhanghongshen.article.service.HotArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class HotArticleServiceTest {

    @Autowired
    private HotArticleService service;

    @Test
    public void computeHotArticleTest() {
        service.computeHotArticle();
    }
}
