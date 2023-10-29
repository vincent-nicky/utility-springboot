package com.wsj.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wsj.entity.domain.Goods;

import java.util.Date;
import java.util.List;

/**
* @author 86178
* @description 针对表【goods】的数据库操作Mapper
* @createDate 2023-10-26 00:21:28
* @Entity generator.domain.Goods
*/
public interface GoodsMapper extends BaseMapper<Goods> {
    List<Goods> listGoodsWithDelete(Date minCreateTime);
}




