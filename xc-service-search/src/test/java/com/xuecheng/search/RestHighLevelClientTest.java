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
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
}
