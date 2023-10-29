package com.wsj.learningredis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.learningredis.mapper.GoodsMapper;
import com.wsj.learningredis.model.Goods;
import com.wsj.learningredis.service.GoodsService;
import org.springframework.stereotype.Service;

/**
* @author 86178
* @description 针对表【goods】的数据库操作Service实现
* @createDate 2023-10-29 00:41:26
*/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
    implements GoodsService {

}




