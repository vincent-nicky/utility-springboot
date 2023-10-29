package com.wsj.test;

import com.alibaba.fastjson2.JSON;
import com.wsj.entity.Person;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class TestDocument {
    @Autowired
    private RestHighLevelClient client;
    /*
    PUT person/_doc/1
    {
      "name":"张三",
      "age":18,
      "address":"高新区双水磨"
    }

     */
    @Test
    public void testAddWithMap() throws IOException {
        //数据对象，map
        Map data = new HashMap();
        data.put("address","深圳宝安");
        data.put("name","尚硅谷");
        data.put("age",20);
        //不需要创建用来操作Document的客户端，直接使用高级客户端即可
        //IndexRequest：CreateRequest、UpdateRequest
        //DeleteIndexRequest   GetIndexRequest   CreateIndexRequest
        //DeleteRequest  GetRequest             IndexRequest

        IndexRequest request = new IndexRequest("person").id("1").source(data);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
        System.out.println(response.status());
    }

    @Test
    public void testAddWithEntity() throws IOException {
        //数据对象，javaObject
        Person p = new Person();
        p.setId("2");
        p.setName("硅谷2222");
        p.setAge(30);
        p.setAddress("北京昌平区");
        //不需要创建用来操作Document的客户端，直接使用高级客户端即可
        //IndexRequest：CreateRequest、UpdateRequest
        //DeleteIndexRequest   GetIndexRequest   CreateIndexRequest
        //DeleteRequest  GetRequest             IndexRequest
        String data = JSON.toJSONString(p);
        IndexRequest request = new IndexRequest("person").id(p.getId()).source(data, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
        System.out.println(response.status());
    }

    @Test
    public void testUpdate() throws IOException {
        //数据对象，javaObject
        Person p = new Person();
        p.setId("2");
        p.setName("硅谷3333");
        //p.setAge(30);
        p.setAddress("北京昌平区!!!!");
        //不需要创建用来操作Document的客户端，直接使用高级客户端即可
        //IndexRequest：CreateRequest、UpdateRequest
        //DeleteIndexRequest   GetIndexRequest   CreateIndexRequest
        //DeleteRequest  GetRequest             IndexRequest
        String data = JSON.toJSONString(p);
        IndexRequest request = new IndexRequest("person").id(p.getId()).source(data, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
        System.out.println(response.status());
    }

    @Test
    public void testGetById() throws IOException {
        GetRequest request = new GetRequest("person").id("2");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
        System.out.println();
        Map<String, Object> source = response.getSource();
        System.out.println(source);
    }

    @Test
    public void testDeleteById() throws IOException {
        DeleteRequest request = new DeleteRequest("person").id("2");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
        System.out.println(response.status());

    }

    @Test
    public void testBulk() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        //# 1 删除1号记录
        DeleteRequest request1 = new DeleteRequest("person").id("1");
        bulkRequest.add(request1);
        //# 2 添加8号记录
        Map data = new HashMap();
        data.put("address","上海松江");
        data.put("name","尚硅谷");
        data.put("age",16);
        IndexRequest request2 = new IndexRequest("person").id("8").source(data);
        bulkRequest.add(request2);
        //# 3 修改2号记录 名称为二号
        //request.add
        Map map2 = new HashMap();
        map2.put("address","北京昌平！！！");
        map2.put("name","尚硅谷！！！");
        //data.put("age",16);//???? 使用UpdateRequest 局部覆盖，使用IndexRequest 全局覆盖
        UpdateRequest updateReqeust = new UpdateRequest("person","2").doc(map2);
        bulkRequest.add(updateReqeust);


        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }
}
