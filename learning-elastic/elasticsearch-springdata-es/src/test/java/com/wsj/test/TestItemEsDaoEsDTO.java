package com.wsj.test;

import com.wsj.dao.es.ItemEsDao;
import com.wsj.entity.es.ItemEs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class TestItemEsDaoEsDTO {
    @Autowired
    private ItemEsDao itemEsDao;

    @Test
    public void testSave() {
        ItemEs itemEs = new ItemEs(2L, "小米手机2", " 手机2", "小米2", 2499.00, "http://image.leyou.com/13123.jpg");
        itemEsDao.save(itemEs);
    }

    @Test
    public void testUpdate() {
        ItemEs itemEs = new ItemEs(2L, "小米手机22", " 手机22", "小米2", 2499.00, "http://image.leyou.com/13123.jpg");
        itemEsDao.save(itemEs);
    }

    @Test
    public void testSaveAll() {
        List<ItemEs> list = new ArrayList<>();
        list.add(new ItemEs(3L, "坚果手机R1", " 手机", "锤子", 3699.00, "http://image.leyou.com/123.jpg"));
        list.add(new ItemEs(4L, "华为META10", " 手机", "华为", 4499.00, "http://image.leyou.com/3.jpg"));
        this.itemEsDao.saveAll(list);
    }

    @Test
    public void testGetById() {
        Optional<ItemEs> optional = this.itemEsDao.findById(1L);
        System.out.println(optional.get());
    }

    @Test
    public void testFindAll() {
        //Iterable 不是iterator
        Iterable<ItemEs> itemList = this.itemEsDao.findAll(Sort.by(Sort.Order.desc("price")));
        itemList.forEach(itemEs -> System.out.println(itemEs));
    }

    @Test
    public void testDeleteById() {
        this.itemEsDao.deleteById(4L);
    }

    @Test
    public void testFindByPriceBetween() {
        List<ItemEs> itemEsList = this.itemEsDao.findByPriceBetween(2500.0, 5000.0);
        itemEsList.forEach(System.out::println);
    }

    @Test
    public void testFindByTitleAndPrice() {
        List<ItemEs> itemEsList = this.itemEsDao.findByTitleAndPrice("小米手机", 3699.0);
        itemEsList.forEach(System.out::println);
    }

}
