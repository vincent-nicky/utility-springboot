package com.wsj.test;

import com.wsj.entity.es.ItemEs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@SpringBootTest
public class TestSpringDataES {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testTemplate() {
        System.out.println(elasticsearchRestTemplate);
    }

    @Test
    public void testCreateIndex() {
        // 项目启动后，springboot会自动创建好索引和其映射，无需手动创建

        //elasticsearchRestTemplate.indexOps(ItemEs.class).create();

        //elasticsearchRestTemplate.indexOps(ItemEs.class).delete();
    }

    @Test
    public void testAdd() {
        ItemEs itemEs = new ItemEs(1L, "小米手机7", " 手机",
                "小米", 3499.00, "http://image.leyou.com/13123.jpg");
        this.elasticsearchRestTemplate.save(itemEs); //save update saveAll
        //this.elasticsearchRestTemplate.delete()
        //this.elasticsearchRestTemplate.get();
    }
}
