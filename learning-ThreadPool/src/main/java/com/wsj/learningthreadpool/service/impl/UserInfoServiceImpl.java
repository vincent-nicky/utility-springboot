package com.wsj.learningthreadpool.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.learningthreadpool.dao.mapper.UserInfoMapper;
import com.wsj.learningthreadpool.entity.UserInfo;
import com.wsj.learningthreadpool.service.UserInfoService;
import org.springframework.stereotype.Service;

/**
* @author 86178
* @description 针对表【userInfo】的数据库操作Service实现
* @createDate 2023-10-28 18:54:36
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService {

}




