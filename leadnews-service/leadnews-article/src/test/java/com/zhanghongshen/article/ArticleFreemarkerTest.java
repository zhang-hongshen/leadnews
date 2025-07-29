package com.zhanghongshen.article;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhanghongshen.article.mapper.ArticleContentMapper;
import com.zhanghongshen.article.mapper.ArticleMapper;
import com.zhanghongshen.model.article.pojo.Article;
import com.zhanghongshen.model.article.pojo.ArticleContent;

import com.alibaba.fastjson2.JSONArray;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.minio.MinioTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {

    @Autowired
    private Configuration configuration;

    @Autowired
    private MinioTemplate minIOTemplate;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Test
    public void createStaticUrlTest() throws Exception {
        //1.获取文章内容
        ArticleContent articleContent = articleContentMapper.selectOne(Wrappers.<ArticleContent>lambdaQuery().eq(ArticleContent::getArticleId,
                1302862387124125698L));
        if(articleContent == null || articleContent.getContent().isBlank()){
            return;
        }
        //2.文章内容通过freemarker生成html文件
        Template template = configuration.getTemplate("article.ftl");
        Map<String, Object> params = new HashMap<>();
        params.put("content", JSONArray.parseArray(articleContent.getContent()));
        StringWriter out = new StringWriter();
        template.process(params, out);
        InputStream is = new ByteArrayInputStream(out.toString().getBytes());

        // Upload html file to minio
        String path = minIOTemplate.uploadHtmlFile("", articleContent.getArticleId() + ".html", is);

        //4.修改ap_article表，保存static_url字段
        Article article = new Article();
        article.setId(articleContent.getArticleId());
        article.setStaticUrl(path);
        articleMapper.updateById(article);
    }
}
