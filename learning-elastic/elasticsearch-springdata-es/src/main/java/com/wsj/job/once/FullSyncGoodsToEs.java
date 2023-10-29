package com.wsj.job.once;

import com.wsj.dao.es.GoodsEsDao;
import com.wsj.entity.domain.Goods;
import com.wsj.entity.es.GoodsEs;
import com.wsj.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步到 es
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncGoodsToEs implements CommandLineRunner {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsEsDao goodsEsDao;

    @Override
    public void run(String... args) {
        List<Goods> goodsList = goodsService.list();
        if (CollectionUtils.isEmpty(goodsList)) {
            return;
        }
        List<GoodsEs> goodsEsList = goodsList.stream().map(GoodsEs::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = goodsList.size();
        log.info("FullSyncGoodsToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            goodsEsDao.saveAll(goodsEsList.subList(i, end));
        }
        log.info("FullSyncGoodsToEs end, total {}", total);
    }
}
