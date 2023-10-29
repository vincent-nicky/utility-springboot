package com.wsj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.dao.mapper.GoodsMapper;
import com.wsj.entity.domain.Goods;
import com.wsj.service.GoodsService;
import org.springframework.stereotype.Service;

/**
* @author 86178
* @description 针对表【goods】的数据库操作Service实现
* @createDate 2023-10-26 00:21:28
*/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
    implements GoodsService{

}




