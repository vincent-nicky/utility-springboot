package com.wsj.learningeasyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;
import com.wsj.learningeasyexcel.entity.UserInfo;
import com.wsj.learningeasyexcel.listener.UserDataListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 导入 Excel
 * <p>
 * 推荐使用  testDemo1_1、testDemo3()
 * <p>
 * 参考：https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
 */
@Slf4j
@SpringBootTest
class ReadTest {

    public static final String fileName = "D:\\dev\\myProject\\utility-springboot\\learning-EasyExcel\\src\\main\\resources\\user.xlsx";

    @Test
    void testDemo1() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        //fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";

        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, UserInfo.class, new PageReadListener<UserInfo>(dataList -> {
            for (UserInfo userInfo : dataList) {
                log.info("读取到一条数据{}", JSON.toJSONString(userInfo));
            }
        })).sheet().doRead();
    }


    @Test
    void testDemo1_1() {
        // doRead()和doReadSync()区别在于：doReadSync()里面配了一个自定义的监听，并且返回读取到excel数据的List集合
        List<UserInfo> userInfoList = EasyExcel.read(fileName)
                .head(UserInfo.class)
                .sheet()
                .doReadSync();

        userInfoList.forEach(System.out::println);
    }

    @Test
    void testDemo2() {
        // 写法2：
        // 匿名内部类 不用额外写一个DemoDataListener
        //fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";

        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, UserInfo.class, new ReadListener<UserInfo>() {
            // 单次缓存的数据量
            public static final int BATCH_COUNT = 100;
            // 临时存储
            private List<UserInfo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(UserInfo data, AnalysisContext context) {
                cachedDataList.add(data);
                if (cachedDataList.size() >= BATCH_COUNT) {
                    saveData();
                    // 存储完成清理 list
                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }
            }
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                saveData();
            }
            // 加上存储数据库
            private void saveData() {
                log.info("{}条数据，开始存储数据库！", cachedDataList.size());
                log.info("存储数据库成功！");
            }
        }).sheet().doRead();
    }
    @Test
    void testDemo3() {
        // 有个很重要的点 UserDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法3：
        //fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";

        // 自定义监听器 UserDataListener
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, UserInfo.class, new UserDataListener()).sheet().doRead();
    }

    @Test
    void testDemo4() {
        // 写法4
        //fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";

        // 一个文件一个reader
        try (ExcelReader excelReader = EasyExcel.read(fileName, UserInfo.class, new UserDataListener()).build()){
            // 构建一个sheet 这里可以指定名字或者no
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            // 读取一个sheet
            excelReader.read(readSheet);
        }
    }
}
