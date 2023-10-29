package com.wsj.learningeasyexcel.importuser;

import com.alibaba.excel.EasyExcel;
import com.wsj.learningeasyexcel.entity.UserInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入星球用户到数据库
 */
public class ImportUserExcel2 {

    public static void main(String[] args) {
        // todo 记得改为自己的测试文件
        String fileName = "D:\\dev\\myProject\\utility-springboot\\learning-EasyExcel\\src\\main\\resources\\user.xlsx";

        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserInfo> userInfoList =
                EasyExcel.read(fileName)
                        .head(UserInfo.class)
                        .sheet()
                        .doReadSync();

        System.out.println("总数 = " + userInfoList.size());

        // 去除 userInfoList 中 username 为空的
        Map<String, List<UserInfo>> listMap =
                userInfoList.stream()
                        .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUsername()))
                        .collect(Collectors.groupingBy(UserInfo::getUsername));

        for (Map.Entry<String, List<UserInfo>> stringListEntry : listMap.entrySet()) {
            if (stringListEntry.getValue().size() > 1) {
                System.out.println("username = " + stringListEntry.getKey());
            }
        }

        System.out.println("不重复昵称数 = " + listMap.keySet().size());
    }
}
