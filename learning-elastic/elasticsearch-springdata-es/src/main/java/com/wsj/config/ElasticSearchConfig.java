//package com.wsj.config;
//
//import lombok.Data;
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.boot.SpringBootConfiguration;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
//
//// TODO 取消注释使自定义配置生效
//@SpringBootConfiguration
//@ConfigurationProperties(prefix = "elasticsearch1")
//@Data
//public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {
//
//    private String host;
//
//    private Integer port;
//
//    //@Bean
//    @Override
//    public RestHighLevelClient elasticsearchClient() {
//        return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));
//    }
//}