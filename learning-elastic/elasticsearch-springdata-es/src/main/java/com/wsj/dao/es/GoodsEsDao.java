package com.wsj.dao.es;

import com.wsj.entity.es.GoodsEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsEsDao extends ElasticsearchRepository<GoodsEs, Double> {

}
