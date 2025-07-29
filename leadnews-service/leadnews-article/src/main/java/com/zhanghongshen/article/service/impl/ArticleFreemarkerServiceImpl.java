package com.zhanghongshen.article.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.zhanghongshen.article.service.ArticleFreemarkerService;
import com.zhanghongshen.model.article.pojo.Article;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.minio.MinioTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    private final Configuration configuration;

    private final MinioTemplate minioTemplate;

    /**
     * 生成静态文件上传到minIO中
     * @param article
     * @param content
     */

    @Override
    public String uploadArticle(Article article, String content) {
        if(StringUtils.isBlank(content)) {
            return null;
        }
        var out = new StringWriter();
        try {
            // generate html file according to existed template
            Template template = configuration.getTemplate("article.ftl");
            Map<String,Object> dataModel = new HashMap<>();
            dataModel.put("content", JSONArray.parseArray(content));

            template.process(dataModel, out);
        } catch (Exception e) {
            log.error("uploadArticleToMinIO error", e);
        }

        // Upload Html File to Minio
        InputStream in = new ByteArrayInputStream(out.toString().getBytes());
        return minioTemplate.uploadHtmlFile(article.getId() + ".html", in);
    }

}
