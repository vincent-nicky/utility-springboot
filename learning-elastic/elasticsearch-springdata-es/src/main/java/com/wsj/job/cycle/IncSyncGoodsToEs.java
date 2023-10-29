package com.wsj.job.cycle;

import com.wsj.dao.es.GoodsEsDao;
import com.wsj.dao.mapper.GoodsMapper;
import com.wsj.entity.domain.Goods;
import com.wsj.entity.es.GoodsEs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步到 es
 */
// todo 取消 @Component 注释开启任务
@Component
@Slf4j
public class IncSyncGoodsToEs {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsEsDao goodsEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 5 * 60 * 1000L);
        List<Goods> goodsList = goodsMapper.listGoodsWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(goodsList)) {
            log.info("no inc post");
            return;
        }
        /*
          .map(PostEsDTO::objToDto) 是一个中间操作，它将流中的每个元素映射为 PostEsDTO 对象。
          这里使用了方法引用（PostEsDTO::objToDto），它指示使用 PostEsDTO 类中名为 objToDto 的方法来进行映射。
         */
        List<GoodsEs> goodsEsList = goodsList.stream()
                .map(GoodsEs::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = goodsEsList.size();
        log.info("IncSyncGoodsToEs start, total {}", total);
        /*
          分批次将数据保存到数据库，每次最多处理pageSize（500）条数据
         */
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            goodsEsDao.saveAll(goodsEsList.subList(i, end));
        }
        log.info("IncSyncGoodsToEs end, total {}", total);
    }
}
