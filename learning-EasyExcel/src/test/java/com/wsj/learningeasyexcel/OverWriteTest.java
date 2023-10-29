package com.wsj.learningeasyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.wsj.learningeasyexcel.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 写入到 Excel
 * <p>
 * 参考：https://easyexcel.opensource.alibaba.com/docs/current/quickstart/write
 *
 */
@Slf4j
@SpringBootTest
class OverWriteTest {

    public static final String fileName = "D:\\dev\\myProject\\utility-springboot\\learning-EasyExcel\\src\\main\\resources\\user.xlsx";

    private List<UserInfo> data() {
        List<UserInfo> list = ListUtils.newArrayList();
        for (int i = 10000; i < 10010; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(String.valueOf(i));
            userInfo.setUsername(String.valueOf(i));
            list.add(userInfo);
        }
        return list;
    }


    /**
     * 1、覆盖写入
     */
    @Test
    void testDemo1(){
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为sheet1 然后文件流会自动关闭
        EasyExcel.write(fileName, UserInfo.class).sheet("sheet1")
                .doWrite(data());
    }

    /**
     * 导出全部时，排除不想要的
     */
    @Test
    public void excludeOrIncludeWrite() {
        //String fileName = TestFileUtil.getPath() + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里需要注意 在使用ExcelProperty注解的使用，如果想不空列则需要加入order字段，而不是index,order会忽略空列，然后继续往后，而index，不会忽略空列，在第几列就是第几列。

        // 根据用户传入字段 假设我们要忽略 username
        Set<String> excludeColumnFiledNames = new HashSet<String>();
        excludeColumnFiledNames.add("username");
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, UserInfo.class).excludeColumnFiledNames(excludeColumnFiledNames).sheet("模板")
                .doWrite(data());
    }

    /**
     * 只导出自己想要的列
     */
    @Test
    void testDemo2(){
        //fileName = TestFileUtil.getPath() + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx";
        // 根据用户传入字段 假设我们只要导出 username
        Set<String> includeColumnFiledNames = new HashSet<String>();
        includeColumnFiledNames.add("username");
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, UserInfo.class).includeColumnFiledNames(includeColumnFiledNames).sheet("模板")
                .doWrite(data());
    }

}
