package com.zhanghongshen.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.zhanghongshen.es.pojo.SearchArticleVo;
import com.zhanghongshen.es.mapper.ArticleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = ElasticsearchDemoApplication.class)
@RunWith(SpringRunner.class)
public class ArticleTest {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ElasticsearchClient esClient;

    @Test
    public void init() throws Exception {

        //1.查询所有符合条件的文章数据
        List<SearchArticleVo> searchArticleVos = articleMapper.loadArticleList();

        //2.批量导入到es索引
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (SearchArticleVo searchArticleVo : searchArticleVos) {
            br.operations(op ->
                    op.index(idx ->
                            idx.index("app_info_article")
                                    .id(searchArticleVo.getId().toString())
                                    .document(searchArticleVo)
                    ));

        }
        BulkResponse bulkResponse = esClient.bulk(br.build());
        if (bulkResponse.errors()) {
            System.out.println("Failures occurred during the bulk operation:");
        } else {
            System.out.println("Bulk operation was successful!");
        }
    }
}