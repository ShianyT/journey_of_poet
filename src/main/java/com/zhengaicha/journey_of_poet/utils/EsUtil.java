package com.zhengaicha.journey_of_poet.utils;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.entity.Poem;
import com.zhengaicha.journey_of_poet.entity.Post;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EsUtil {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    ObjectMapper objectMapper = new ObjectMapper();

    public void savePost(Post post) {
        try {
            IndexRequest request = new IndexRequest("post");
            String json = objectMapper.writeValueAsString(post);
            request.id(String.valueOf(post.getId()));
            request.source(json, XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePost(int id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest("post");
            deleteRequest.id(String.valueOf(id));
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Post> searchPost(String keywords, Integer currentPage) {
        ArrayList<Post> posts = new ArrayList<>();
        try {
            SearchRequest request = new SearchRequest("post");
            SearchSourceBuilder builder = new SearchSourceBuilder();

            // 查询条件
            MatchQueryBuilder title = QueryBuilders.matchQuery("title", keywords);
            MatchQueryBuilder content = QueryBuilders.matchQuery("content", keywords);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.should(title).should(content);
            builder.query(boolQueryBuilder).from(currentPage * 15 - 15).size(15);

            // 查询高亮
            HighlightBuilder highlightBuilder =
                    new HighlightBuilder().field("*")
                            .requireFieldMatch(false)
                            .preTags("<strong>").postTags("</strong>")
                            // 下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等;
                            .fragmentSize(800000)// 最大高亮分片数
                            .numOfFragments(0);// 从第一个分片获取高亮片段;

            builder.highlighter(highlightBuilder);

            // 查询结果
            request.source(builder);
            SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHits hits = search.getHits();

            if (hits == null) {
                return null;
            }
            // 遍历结果
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Post post = BeanUtil.copyProperties(sourceAsMap, Post.class);
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields != null) {
                    HighlightField highlightField = highlightFields.get("content");
                    if (highlightField != null) {
                        Text[] fragments = highlightField.getFragments();
                        if (fragments != null) {
                            post.setContent(String.valueOf(fragments[0]));
                        }
                    }

                    highlightField = highlightFields.get("title");
                    if (highlightField != null) {
                        Text[] fragments = highlightField.getFragments();
                        if (fragments != null) {
                            post.setTitle(String.valueOf(fragments[0]));
                        }
                    }
                }
                posts.add(post);
            }
            return posts;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Poem> searchPoem(String keywords, int currentPage) {
        ArrayList<Poem> poems = new ArrayList<>();
        SearchRequest request = new SearchRequest("poem");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = null;
        String type = "name";
        matchQueryBuilder = QueryBuilders.matchQuery(type, keywords);
        builder.query(matchQueryBuilder).from(currentPage * 15 - 15).size(15);
        // 查询高亮
        HighlightBuilder highlightBuilder =
                new HighlightBuilder().field(type)
                        .requireFieldMatch(false)
                        .preTags("<strong>").postTags("</strong>")
                        // 下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等;
                        .fragmentSize(800000)// 最大高亮分片数
                        .numOfFragments(0);// 从第一个分片获取高亮片段;
        builder.highlighter(highlightBuilder);


        request.source(builder);
        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SearchHits hits = search.getHits();

        if (hits == null) {
            return null;
        }
        // 遍历结果
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Poem poem = BeanUtil.copyProperties(sourceAsMap, Poem.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField highlightField = highlightFields.get(type);
                if (highlightField != null) {
                    Text[] fragments = highlightField.getFragments();
                    if (fragments != null) {
                        poem.setName(String.valueOf(fragments[0]));
                    }
                }
            }
            poems.add(poem);
        }
        return poems;
    }
}
