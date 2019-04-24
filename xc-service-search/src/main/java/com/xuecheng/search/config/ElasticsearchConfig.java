package com.xuecheng.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${xuecheng.elasticsearch.hostlist}")
    private String hostlist;

    /**
     * 创建RestHightLevelClient客户端
     *
     * @return
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 解析hostlist配置信息
        String[] split = hostlist.split(",");
        // 创建httphost数组，其中存放es主机和端口的配置信息
        HttpHost[] httpGostArray = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            httpGostArray[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
        }
        // 创建RestHightLevelClient客户端
        return new RestHighLevelClient(RestClient.builder(httpGostArray));
    }

    /**
     * 项目主要使用RestHighLevelClient，对于低级的客户端暂时不用
     *
     * @return
     */
    @Bean
    public RestClient restClient() {
        // 解析hostlist配置信息
        String[] split = hostlist.split(",");
        // 创建httpHost数组，其中存放es主机和端口的配置信息
        HttpHost[] httpHostArray = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            httpHostArray[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "htpp");
        }
        return RestClient.builder(httpHostArray).build();
    }


}
