package com.wsj.dao;

import com.wsj.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
// 整合的是MyBatis，不是SpringDataJPA,也不是tkMyBatis
@Mapper
public interface GoodsDao  {

    public List<Goods> findAll();
}
