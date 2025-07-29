package com.zhanghongshen.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.search.dto.SearchDto;
import com.zhanghongshen.search.context.UserContextHolder;
import com.zhanghongshen.search.service.ArticleSearchService;
import com.zhanghongshen.search.service.UserSearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleSearchServiceImpl implements ArticleSearchService {

    private final ElasticsearchClient esClient;

    private final UserSearchHistoryService searchHistoryService;

    @Override
    public ResponseResult search(SearchDto dto) throws IOException {

        Long userId = UserContextHolder.getUserId();

        // asynchronously save search history
        if(userId != null && dto.getFromIndex() == 0){
            searchHistoryService.insert(dto.getKeyword(), userId);
        }

        Query queryStringQuery = Query.of(builder ->
                builder.queryString(qs ->
                        qs.query(dto.getKeyword())
                                .fields("title", "content")
                                .defaultOperator(Operator.Or)
                ));
        Query rangeQuery = Query.of(r ->
                r.range(ra ->
                        ra.field("publishTime")
                        .lt(JsonData.of(dto.getMinBehotTime().getTime()))
        ));

        BoolQuery boolQuery = BoolQuery.of(b ->
                b.must(queryStringQuery)
                        .filter(rangeQuery)
        );

        Highlight highlight = Highlight.of(h ->
                h.fields("title", f ->
                        f.preTags("<font style='color: red; font-size: inherit;'>")
                        .postTags("</font>"))
        );

        SearchRequest searchRequest = SearchRequest.of(s ->
                s.index("app_info_article")
                        .query(boolQuery._toQuery())
                        .highlight(highlight)
                        .from(0)
                        .size(dto.getPageSize())
                        .sort(SortOptions.of(so ->
                                so.field(f ->
                                        f.field("publishTime")
                                                .order(SortOrder.Desc))))
        );

        return ResponseResult.success(extractHighlight(esClient.search(searchRequest, Map.class)));
    }

    private List<Map<String, Object>> extractHighlight(SearchResponse<Map> response) {
        List<Map<String, Object>> res = new ArrayList<>();
        for (Hit<Map> hit : response.hits().hits()) {
            Map<String, Object> source = hit.source();
            String title = String.valueOf(source.get("title"));
            if(hit.highlight() != null && !hit.highlight().isEmpty()){
                List<String> titles = hit.highlight().get("title");
                title = StringUtils.join(titles);
            }
            source.put("h_title", title);
            res.add(source);
        }
        return res;
    }
}
