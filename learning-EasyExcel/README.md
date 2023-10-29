# Easy Excel

官网：[https://easyexcel.opensource.alibaba.com/](https://easyexcel.opensource.alibaba.com/)

Github：[https://github.com/alibaba/easyexcel](https://github.com/alibaba/easyexcel)

EasyExcel是一个基于Java的、快速、简洁、解决大文件内存溢出的Excel处理工具。他能让你在不用考虑性能、内存的等因素的情况下，快速完成Excel的读、写等功能。

## 1、从Excel导入

推荐写法二和写法四

文件路径

```java
public static final String fileName = "D:\\dev\\myProject\\utility-springboot\\learning-EasyExcel\\src\\main\\resources\\user.xlsx";
```

如果是 `File`：

```java
File file = new File(fileName);

...EasyExcel.read(file)...
```

如果是 `MultipartFile`

```java
...EasyExcel.read(multipartFile.getInputStream())...
```

### （1）

```java
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
```

### （2）读取完就返回一个集合

```java
@Test
void testDemo1_1() {
    // doRead()和doReadSync()区别在于：doReadSync()里面配了一个自定义的监听，并且返回读取到excel数据的List集合
    List<UserInfo> userInfoList = EasyExcel.read(fileName)
            .head(UserInfo.class)
            .sheet()
            .doReadSync();

    userInfoList.forEach(System.out::println);
}
```

### （3）

```java
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
```

### （4）自定义监听器 UserDataListener

```java
@Test
void testDemo3() {
    // 有个很重要的点 UserDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
    // 写法3：
    //fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";

    // 自定义监听器 UserDataListener
    // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
    EasyExcel.read(fileName, UserInfo.class, new UserDataListener()).sheet().doRead();
}
```

### （5）

```java
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
```

### （6）通用 ExcelUtils：将表格转csv字符串

```java
/**
 * 通用 ExcelUtils
 */
@Slf4j
public class ExcelUtils {
    public static String excelToCsv(MultipartFile multipartFile) {
        // 读取数据到list
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        // 转换为 csv
        StringBuilder stringBuilder = new StringBuilder();
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        // 将表头设置为list
        List<String> headerList = headerMap.values().stream()
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toList());
        // 将list表头以,分隔
        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            // 每个数据以,分隔
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }
}
```



## 2、写入到Excel

文件路径：

```java
public static final String fileName = "D:\\dev\\myProject\\utility-springboot\\learning-EasyExcel\\src\\main\\resources\\user.xlsx";
```

### （1）覆盖写入

```java
@Test
void testDemo1(){
    // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为sheet1 然后文件流会自动关闭
    EasyExcel.write(fileName, UserInfo.class).sheet("sheet1")
            .doWrite(data());
}
```

### （2）导出全部时，排除不想要的

```java
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
```

### （3）只导出自己想要的列

```java
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
```

