package com.wsj.learningeasyexcel.dao;

import com.wsj.learningeasyexcel.entity.UserInfo;

import java.util.List;

public interface UserDao {

    void save(List<UserInfo> cachedDataList);
}
