package com.wsj.test;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
public class TestIndex {
    @Autowired
    private RestHighLevelClient client;

    @Test
    public void testAddWithoutMapping() throws IOException {
        //使用高级客户端创建一个操作索引的client
        IndicesClient indicesClient = client.indices();

        //创建一个（创建索引）请求
        CreateIndexRequest request = new CreateIndexRequest("person"); //PUT person

        //把请求发送给ES并得到响应
        CreateIndexResponse response = indicesClient.create(request, RequestOptions.DEFAULT);

        //输出响应信息
        /*
        {
          "acknowledged" : true,
          "shards_acknowledged" : true,
          "index" : "person"
        }
         */

        System.out.println(response.index());//person
        System.out.println(response.isAcknowledged());//true
        System.out.println(response.isShardsAcknowledged());//true
    }

    @Test
    public void testAddWithMapping() throws IOException {
        //使用高级客户端创建一个操作索引的client
        IndicesClient indicesClient = client.indices();
        //创建一个（创建索引）请求
        CreateIndexRequest request = new CreateIndexRequest("person"); //PUT person
        String source = "{\n" +
                "     \"properties\":{\n" +
                "        \"address\":{\n" +
                "          \"type\":\"text\",\n" +
                "          \"analyzer\":\"ik_max_word\"\n" +
                "        },\n" +
                "        \"age\":{\n" +
                "          \"type\":\"long\"\n" +
                "        },\n" +
                "        \"name\":{\n" +
                "          \"type\":\"keyword\"\n" +
                "        }\n" +
                "      }\n" +
                "  }";
        request.mapping(source, XContentType.JSON);
        //把请求发送给ES并得到响应
        CreateIndexResponse response = indicesClient.create(request, RequestOptions.DEFAULT);
        //输出响应信息
        /*
        {
          "acknowledged" : true,
          "shards_acknowledged" : true,
          "index" : "person"
        }
         */
        System.out.println(response.index());//person
        System.out.println(response.isAcknowledged());//true
        System.out.println(response.isShardsAcknowledged());//true
    }

    @Test
    public void testExists() throws IOException {
        //使用高级客户端创建一个操作索引的client
        IndicesClient indicesClient = client.indices();
        //创建一个（判断索引是否存在的）请求
        //CreateIndexRequest request = new CreateIndexRequest("person"); //PUT person
        GetIndexRequest request = new GetIndexRequest("person");
        //把请求发送给ES并得到响应
        boolean exists = indicesClient.exists(request, RequestOptions.DEFAULT);
        //输出响应信息
        System.out.println(exists);
    }

    @Test
    public void testGet() throws IOException {
        //使用高级客户端创建一个操作索引的client
        IndicesClient indicesClient = client.indices();
        //创建一个（判断索引是否存在的）请求
        GetIndexRequest request = new GetIndexRequest("person");
        //把请求发送给ES并得到响应
        GetIndexResponse response = indicesClient.get(request, RequestOptions.DEFAULT);
        //输出响应信息
        System.out.println(response.getAliases());//"aliases" : { },
        System.out.println(response.getSettings());
        //System.out.println(response.getMappings());
        Map<String, MappingMetadata> mappings = response.getMappings();
        MappingMetadata mappingMetadata = mappings.get("person");
        Map<String, Object> sourceAsMap = mappingMetadata.getSourceAsMap();
        System.out.println(sourceAsMap);
    }

    @Test
    public void testDelete() throws IOException {
        //使用高级客户端创建一个操作索引的client
        IndicesClient indicesClient = client.indices();
        //创建一个（判断索引是否存在的）请求
        DeleteIndexRequest request = new DeleteIndexRequest("person");
        //把请求发送给ES并得到响应
        AcknowledgedResponse response = indicesClient.delete(request, RequestOptions.DEFAULT);
        //输出响应信息
        System.out.println(response.isAcknowledged());
    }
}
