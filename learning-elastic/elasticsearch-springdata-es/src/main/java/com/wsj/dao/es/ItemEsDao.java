package com.wsj.dao.es;

import com.wsj.entity.es.ItemEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemEsDao extends ElasticsearchRepository<ItemEs, Long> {

    List<ItemEs> findByPriceBetween(Double price1, Double price2);

    //org.springframework.data.mapping.PropertyReferenceException: No property name found for type ItemEs!
    List<ItemEs> findByTitleAndPrice(String name, Double price);

}
