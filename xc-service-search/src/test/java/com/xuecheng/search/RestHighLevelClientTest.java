package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.mapping.TextScore;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RestHighLevelClientTest {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    RestClient restClient;

    /**
     * 创建索引库
     *
     * @throws IOException
     */
    @Test
    public void testCreateIndex() throws IOException {
        // 创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        // 设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", 1).put("number_of_replicas", 0));
        // 设置映射
        createIndexRequest.mapping("doc", "{\n" +
                "\t\"properties\": {\n" +
                "\t\t\"name\": {\n" +
                "\t\t\t\"type\": \"text\",\n" +
                "\t\t\t\"analyzer\": \"ik_max_word\",\n" +
                "\t\t\t\"search_analyzer\": \"ik_smart\"\n" +
                "\t\t},\n" +
                "\t\t\"description\": {\n" +
                "\t\t\t\"type\": \"text\",\n" +
                "\t\t\t\"analyzer\": \"ik_max_word\",\n" +
                "\t\t\t\"search_analyzer\": \"ik_smart\"\n" +
                "\t\t},\n" +
                "\t\t\"studymodel\": {\n" +
                "\t\t\t\"type\": \"keyword\"\n" +
                "\t\t},\n" +
                "\t\t\"price\": {\n" +
                "\t\t\t\"type\": \"float\"\n" +
                "\t\t},\n" +
                "\t\t\"timestamp\": {\n" +
                "\t\t\t\"type\": \"date\",\n" +
                "\t\t\t\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}", XContentType.JSON);
        // 创建索引操作客户端
        IndicesClient indices = restHighLevelClient.indices();
        // 创建响应对象
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        // 得到响应结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.print("acknowledged：" + acknowledged);
    }

    /**
     * 删除索引库
     *
     * @throws IOException
     */
    @Test
    public void testDeleteIndex() throws IOException {
        // 删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        // 删除索引
        DeleteIndexResponse deleteIndexResponse = restHighLevelClient.indices().delete(deleteIndexRequest);
        // 删除碎银响应结果
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.print("acknowledged:" + acknowledged);
    }

    /**
     * 添加文档
     *
     * @throws IOException
     */
    @Test
    public void testAddDoc() throws IOException {
        // 准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "Bootstrap开发");
        jsonMap.put("description", "Bootstrap是由Twitter推出的一个前台页面开发框架，是一个非常流行的开发框架，此框架集成了多种页面效果。此开发框架包含了大量的CSS、JS程序代码，可以帮助开发者（尤其是不擅长页面开发的程序人员）轻松的实现一个不受浏览器限制的精美界面效果。");
        jsonMap.put("studymodel", "201002");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 38.6f);
        // 索引请求对象
        IndexRequest indexRequest = new IndexRequest("xc_course", "doc");
        // 指定索引文档内容
        indexRequest.source(jsonMap);
        // 索引响应对象
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
        // 获取索引响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.print(result);
    }

    /**
     * 更新文档
     *
     * @throws IOException
     */
    @Test
    public void updateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("xc_course", "doc", "-dqGTmoBiEP3AMq6Tspd");
        Map<String, String> map = new HashMap<>();
        map.put("name", "spring cloud实战ok");
        updateRequest.doc(map);
        UpdateResponse update = restHighLevelClient.update(updateRequest);
        RestStatus status = update.status();
        System.out.print(status);
    }

    /**
     * 根据id删除文档
     *
     * @throws IOException
     */
    @Test
    public void testDelDoc() throws IOException {
        //删除文档id
        String id = "-dqGTmoBiEP3AMq6Tspd";
        // 删除索引请求对象
        DeleteRequest deleteRequest = new DeleteRequest("xc_course", "doc", id);
        // 响应对象
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
        // 获取响应结果
        DocWriteResponse.Result result = deleteResponse.getResult();
        System.out.print(result);

    }

    /**
     * 搜索type下的全部记录
     *
     * @throws IOException
     */
    @Test
    public void testSearchAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // dource字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    @Test
    public void testSearchByPage() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // 分页查询，设置起始下标，从0开始
        searchSourceBuilder.from(0);
        // 每页显示个数
        searchSourceBuilder.size(10);

        // source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        // 高匹配率
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    /**
     * TermQuery测试
     * 精确匹配，不会将词语分词
     *
     * @throws IOException
     */
    @Test
    public void testTermQuery() throws Exception {
        SearchRequest searchRequest = new SearchRequest("xc_course)");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name", "xxx"));
        // source过滤字段，第一个参数是显示的字段，第二个是不显示的字段
        searchSourceBuilder.fetchSource(new String[]{"name", "format"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        // 高匹配率
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit h : hitsHits) {
            Map<String, Object> map = h.getSourceAsMap();
            String name = (String) map.get("name");
            // 时间格式化
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = (String) map.get("format");
            Date format = dateFormat.parse(dateStr);
            System.out.print("name=" + name + ", format=" + format);
        }
    }

    /**
     * 根据多个id搜索
     *
     * @throws IOException
     */
    @Test
    public void testQueryById() throws IOException {
        // 搜索对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 定义多个id
        String[] ids = new String[]{"1", "2"};
        List<String> idList = Arrays.asList(ids);
        // 注意这里的是“termsQuery”，不是“termQuery”，多了个s！！！
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id", idList));
        searchRequest.source(searchSourceBuilder);
        // 查询
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hit : hitsHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.print(sourceAsMap.toString());
        }
    }

    /**
     * 全文检索，分词后将各词从索引中搜索(配合and或or组合)
     */
    @Test
    public void testMatchQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source过滤字段
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        // 匹配关键字(分词后可以选择多个词是and或者or组合查询)
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring开发").operator(Operator.OR));
        searchRequest.source(searchSourceBuilder);
        // 查询
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    /**
     * 全文检索，分词后将各词从索引中搜索(配合词的占比查询)
     * 设置"minimum_should_match": "80%"表示，假如有三个词，三个词在文档的匹配占比为80%，即3*0.8=2.4，向上取整得2，表示至少有两个词在文档中要匹配成功。
     *
     * @throws IOException
     */
    @Test
    public void testMinimum_should_match() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source过滤字段
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        // 匹配关键字(分词后可以选择多个词是and或者or组合查询)
        searchSourceBuilder.query(QueryBuilders
                .matchQuery("description", "前台页面开发框架 架构")
                // 设置占比为80%。
                .minimumShouldMatch("80%"));
        searchRequest.source(searchSourceBuilder);
        // 查询
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    /**
     * multiQuery匹配多个字段查询
     * termQuery和matchQuery一次只能匹配一个Field,multiQuery，一次可以匹配多个字段。
     */
    @Test
    public void testMultiQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source过滤字段
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        // 匹配关键字(分词后可以选择多个词是and或者or组合查询)
        searchSourceBuilder.query(QueryBuilders
                .multiMatchQuery("spring框架", "name", "description")
                // 设置占比为50%。
                .minimumShouldMatch("50%")
                // 提升boost：表示权重提升10倍，执行上边的查询，发现name中包括spring关键字的文档排在前边
                .field("name", 10));
        searchRequest.source(searchSourceBuilder);
        // 查询
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    /**
     * 布尔查询
     * <p>
     * 结合精确分词查询和不分词查询共同使用
     *
     * @throws IOException
     */
    @Test
    public void testBooleanQuery() throws IOException {
        // 索引请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        // 查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        // multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring框架", "name", "description")
                .minimumShouldMatch("50%");
        // 单独增大比重
        multiMatchQueryBuilder.field("name", 10);
        // TermQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("stidyModel", "201001");
        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        // 设置布尔查询对象
        searchSourceBuilder.query(boolQueryBuilder);
        // 设施搜索源配置
        searchRequest.source(searchSourceBuilder);
        // 搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hit : hitsHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.print(sourceAsMap.toString());
        }
    }

    /**
     * 布尔查询使用过虑器
     *
     * @throws IOException
     */
    @Test
    public void testFilter() throws IOException {
        // 索引请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        // 查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        // multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring框架", "name", "description")
                .minimumShouldMatch("50%");
        // 单独增大比重
        multiMatchQueryBuilder.field("name", 10);
//        searchSourceBuilder.query(multiMatchQueryBuilder);

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel", "201001"));
        // 范围过虑：保留大于等于60 并且小于等于100的记录
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        // ...
    }

    /**
     * 排序
     *
     * @throws IOException
     */
    @Test
    public void testSort() throws IOException {
        // 索引请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        // 查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        // multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring框架", "name", "description")
                .minimumShouldMatch("50%");
        // 单独增大比重
        multiMatchQueryBuilder.field("name", 10);
//        searchSourceBuilder.query(multiMatchQueryBuilder);

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        // 范围过虑：保留大于等于60 并且小于等于100的记录
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));

        // 排序
        searchSourceBuilder.sort(new FieldSortBuilder("studymodel").order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        // ...
    }

    @Test
    public void testHighlight() throws IOException {
        // 索引请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        // 查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        // multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring框架", "name", "description")
                .minimumShouldMatch("50%");
        // 单独增大比重
        multiMatchQueryBuilder.field("name", 10);
//        searchSourceBuilder.query(multiMatchQueryBuilder);

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        // 范围过虑：保留大于等于60 并且小于等于100的记录
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));

        // 排序
        searchSourceBuilder.sort(new FieldSortBuilder("studymodel").order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));

        // 高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");// 设置前缀
        highlightBuilder.postTags("</tag>");// 设置后缀
        // 设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //名称
            String name = (String) sourceAsMap.get("name");
            // 取出高亮字体
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null) {
                    Text[] fragments = nameField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text str : fragments) {
                        stringBuffer.append(str.toString());
                    }
                    name = stringBuffer.toString();
                }
            }
        }
    }


}
