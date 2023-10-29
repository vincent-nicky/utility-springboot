package com.wsj.test;

import com.alibaba.fastjson2.JSON;
import com.wsj.dao.GoodsDao;
import com.wsj.entity.Goods;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
把MySQL的es数据库中的goods表的所有的记录
转换为（导入到）
ES中，变为指定索引goods的指定的文档

然后不再使用SQL语句对MySQL进行操作，而是使用DSL对ES进行操作
好处
1.实时
2.分词
3.相关性

 */
@SpringBootTest
public class TestImport {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private RestHighLevelClient client;

    @Test
    public void test01() throws IOException {
        List<Goods> goodsList = this.goodsDao.findAll();
        //System.out.println(goodsList.size());

        //创建BulkRequest并将多个IndexRequest打捆
        BulkRequest bulkRequest = new BulkRequest();
        for(Goods goods :goodsList){
            //将Goods中的specStr转换为spec map
            Map spec = JSON.parseObject(goods.getSpecStr(), Map.class);
            goods.setSpec(spec);
            //将goods对象转换为JSON字符串
            String data = JSON.toJSONString(goods);
            //创建一个IndexRequest用来添加文档
            IndexRequest request = new IndexRequest("goods")
                    .id(goods.getId()+"")
                    .source(data, XContentType.JSON);
            //打捆
            bulkRequest.add(request);
        }
        //发送BulkRequest并得到响应
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        //输出响应结果
        System.out.println(response.status());


    }
}
