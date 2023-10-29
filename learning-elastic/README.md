## 一、前置知识

![image-20231025192302958](https://cdn.jsdelivr.net/gh/vincent-nicky/image_store/blog/image-20231025192302958.png)

### Elasticsearch核心概念

#### 1、索引 index

文档存储的地方，类似于MySQL数据库中的数据库概念

#### 2、类型 type

如果按照关系型数据库中的对应关系，还应该有`表`的概念。ES中没有`表`的概念，这是ES和数据库的一个区别，在我们建立*索引*之后，可以直接往 *索引* 中写入*文档*。

在6.0版本之前，ES中有`Type`的概念，可以理解成关系型数据库中的`表`，但是官方说这是一个设计上的失误，所以在6.0版本之后`Type`就被废弃了。

#### 3、字段Field

相当于是数据表的字段，字段在ES中可以理解为JSON数据的键，下面的JSON数据中，*name* 就是一个字段。

#### 4、映射 mapping

*映射* 是对*文档*中每个*字段*的类型进行定义，每一种数据类型都有对应的使用场景。

每个*文档*都有映射，但是在大多数使用场景中，我们并不需要显示的创建映射，因为ES中实现了动态映射。

我们在索引中写入一个下面的JSON文档，在动态映射的作用下，`name`会映射成`text`类型，`age`会映射成`long`类型。

```
{
    "name":"jack",
    "age":18,
}
```

自动判断的规则如下：

![image-20231025193919942](https://cdn.jsdelivr.net/gh/vincent-nicky/image_store/blog/image-20231025193919942.png)

Elasticsearch中支持的类型如下：

![image-20231025193935677](https://cdn.jsdelivr.net/gh/vincent-nicky/image_store/blog/image-20231025193935677.png)

- string类型在ElasticSearch 旧版本中使用较多，从ElasticSearch 5.x开始不再支持string，由text和keyword类型替代。（已经废弃）
- text 类型，需要分词设置text类型，比如Email内容、产品描述，应该使用text类型。
- keyword 类型 ，不需要分词设置keyword类型，比如email地址、主机名、状态码和标签。

![image-20231025194006904](https://cdn.jsdelivr.net/gh/vincent-nicky/image_store/blog/image-20231025194006904.png)

![image-20231025194013112](https://cdn.jsdelivr.net/gh/vincent-nicky/image_store/blog/image-20231025194013112.png)

#### 5、文档 document

*文档* 在ES中相当于传统数据库中的*行*的概念，ES中的数据都以JSON的形式来表示，在MySQL中插入一行数据和ES中插入一个JSON文档是一个意思。下面的JSON数据表示，一个包含3个字段的*文档*。

```
{
    "name":"jack",
    "age":18,
    "gender":1
}
```

一个文档不只有数据。它还包含了元数据(metadata)——关于文档的信息。三个必须的元数据节点是：

| **节点** | **说明**           |
| -------- | ------------------ |
| _index   | 文档存储的地方     |
| _type    | 文档代表的对象的类 |
| _id      | 文档的唯一标识     |
| _score   | 文档的评分         |



## 二、常用 DSL 命令

数据以JSON的方式提供，通过请求体传输

### 1、测试连接

```json
GET /
```

### 2、分析

```json
# 命令后面的大括号必须换行
# _analyze 分词 
# 英文分词直接按照单词进行分词
GET /_analyze 
{ 
  "text":"we should study hard"
}

# 默认中文是按照字进行分词，无法满足要求
GET /_analyze
{
  "text":"我是中国人"
}
```

### 3、结合ik分词器

```json
# 细粒度
GET /_analyze
{
  "text":"乒乓球明年总冠军",
  "analyzer": "ik_max_word"
}

# 粗粒度
GET /_analyze
{
  "text":"乒乓球明年总冠军",
  "analyzer": "ik_smart"
}
```

### 4、自定义ik分词器

痛点

```JSON
# 遇到最新的网络语，分词无法满足需要
GET /_analyze
{
  "text":"蓝瘦香菇",
  "analyzer": "ik_max_word"
}

GET /_analyze
{
  "text":"耗子尾汁",
  "analyzer": "ik_max_word"
}

GET /_analyze
{
  "text":"喜大普奔",
  "analyzer": "ik_smart"
}
```

解决：

```sh
cd /opt/es/elasticsearch-7.17.14/plugins/ik/config/
```

编写文字（注意文件的格式是UTF-8）

```sh
vim mydic.dic
```

```
蓝瘦香菇
耗子尾汁
喜大普奔
```

修改配置

```sh
vim IKAnalyzer.cfg.xml
```

```properties
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
	<comment>IK Analyzer 扩展配置</comment>
	
	<!--用户可以在这里配置自己的扩展字典 -->
	<entry key="ext_dict">mydic.dic</entry>
	
	 <!--用户可以在这里配置自己的扩展停止词字典-->
	<entry key="ext_stopwords"></entry>
	<!--用户可以在这里配置远程扩展字典 -->
	<!-- <entry key="remote_ext_dict">words_location</entry> -->
	<!--用户可以在这里配置远程扩展停止词字典-->
	<!-- <entry key="remote_ext_stopwords">words_location</entry> -->
</properties>
```

重启es

### 5、索引 & 映射

```json
# 创建索引
PUT person

# 创建索引不使用post！
# POST person

# 判断索引是否存在
HEAD person

# 查询索引(包含aliases,mappings,settings)
GET person

# 查询映射
GET person/_mapping

# 添加映射
# 创建索引并添加映射
PUT person
{
  "mappings": {
    "properties": {
      "name":{
        "type":"keyword"
      },
     "age":{
       "type":"integer"
     }
    }
  }
}

# 索引库中添加字段
PUT person/_mapping
{
  "properties":{
    "address":{
      "type":"text",
      "analyzer":"ik_max_word"
    }
  }
}

# 关闭索引
POST person/_close

# 打开索引
POST person/_open

# 删除索引
DELETE person
```

### 6、文档

```json
# =====================添加======================

# 添加文档，指定id
# 文档的id和是否有id field是两回事
PUT person/_doc/1
{
  "name":"张三",
  "age":23,
  "address":"雁塔区南窑国际"
}

# 添加文档,不指定id,自动生成id
# PUT必须指定id，id不存在就添加，存在就修改
PUT person/_doc
{
  "name":"李四",
  "age":23,
  "address":"高新区双水磨小区"
}

# POST可以不指定id，不指定就是添加，自动生成id
POST person/_doc
{
  "name":"李四",
  "age":23,
  "address":"高新区双水磨小区"
}

# 添加文档,指定id,不存在，就添加
POST person/_doc/2
{
  "name":"王五",
  "age":24,
  "address":"高新区双水磨小区"
}

# POST指定id，id存在，就替换，全量替换
# PUT 必须指定id
POST person/_doc/2
{
  "name":"王五1",
  "age":25,
  "address":"高新区双水磨小区1"
}

# =====================修改======================

# 使用put必须使用id
# put的修改时全量覆盖，如果id存在，就进行修改，但是以现在提供的字段为全部字段
PUT person/_doc/1
{
  "age":24
}

# 如果不使用全量替换，请使用POST，并且需要使用_update,doc
POST person/_update/1
{
  "doc":{
    "age":24
  }
}

# 如果存在就替换，也是全量替换
POST person/_doc/1
{
  "age":24
}

# 修改文档 根据id，id存在就是修改，id不指定报405：请求方式不支持，使用POST方式
PUT person/_doc
{
  "name":"王五",
  "age":24,
  "address":"高新区双水磨南区"
}

POST person/_doc/2
{
  "name":"王五",
  "age":23,
  "address":"高新区双水磨南区"
}

# =====================查询======================

# 查询文档
GET person/_doc/1

GET person/_doc/2

# 查询文档
GET person/_doc/Ynq6ZosB6ff69mh4M35

# 查询文档
GET person/_doc/2

# 查询所有文档
GET person/_search

GET person/_search
{
  "query": {
    "match_all": {
    }
  }
}

# =====================删除======================

# 删除文档
DELETE person/_doc/2

DELETE person



#如果我们只需要判断文档是否存在，而不是查询文档内容，那么可以这样：
HEAD person/_doc/2
#存在返回：200 - OK
#不存在返回：404 – Not Found
```

### 7、exists 查询

```json
#查询exists
#不指定mappring，直接添加字段，能够自动识别类型，但是中文分词器是standard
POST person/_doc/3
{
  "name":"赵六",
  "age":26,
  "address":"雁塔区玫瑰公馆",
  "address2":"和发智能大厦B座"
}

GET person/_search
{
  "query": {
    "exists": {
      "field": "address2"
    }
  }
}
```

### 8、全文查询-match查询

```json
# keyword类型不分词，不建议使用match
# 查询条件中的match可以分词，数据是keyword不分词，必须完全匹配
GET person/_search
{
  "query": {
    "match": {
      "name": "张三"
    }
  }
}

GET person/_search
{
  "query": {
    "match": {
      "address": "雁塔区"
    }
  }
}

GET person/_search
{
  "query": {
    "match": {
      "address": "雁塔"
    }
  }
}

#match的条件会分词，文档的字段的内容安装指定的分词器来分词，要求分词中要有匹配的才行
GET person/_search
{
  "query": {
    "match": {
      "address": "南窑"
    }
  }
}

# 查询映射
GET person/_mapping

GET _analyze
{
  "text":"高新区双水磨南区",
  "analyzer": "ik_max_word"
}

GET _analyze
{
  "text":"雁塔",
  "analyzer": "ik_max_word"
}

```

### 9、查询文档-term查询

```json
#match 对查询条件分词，term对查询条件不分词
#term主要用于精确匹配哪些值，比如数字，日期，布尔值或 not_analyzed keyword类型 的字符串
GET  person/_search
{
  "query": {
    "term": {
      "name": "张三"
    }
  }
}

GET  person/_search
{
  "query": {
    "term": {
      "age": "23"
    }
  }
}

# term查询：查询条件不分词
# document的字段值要分词，要求分词中和查询条件完全匹配
GET  person/_search
{
  "query": {
    "term": {
      "address": "高新区"
    }
  }
}

GET person/_search

GET person/_search
{
  "query": {
    "terms": {
      "age": [23,24 ]
    }
  }
}

#terms 
GET person/_search
{
  "query": {
    "terms": {
      "address": ["国际","高新" ]
    }
  }
}
```

### 10、range查询

```
gt :: 大于
gte :: 大于等于
lt :: 小于
lte :: 小于等于
```

```json
# range
GET person/_search
{
  "query": {
    "range": {
      "age": {
        "gt": 23,
        "lte": 25
      }
    }
  }
}
```

### 11、复合查询（bool）

bool 可以用来合并多个过滤条件查询结果的布尔逻辑，它包含这如下几个操作符:

- **must** : 多个查询条件的完全匹配，相当于 and，**有评分**。

- **filter**: 多个查询条件的完全匹配，相当于 and，**无评分**。

- **must_not** ：多个查询条件的相反匹配，相当于 not。

- **should** : 至少有一个查询条件匹配，相当于 or。

```json
# bool查询 复合查询 多个查询条件
GET person/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "range": {
            "age": {
              "gte": 23,
              "lte": 25
            }
          }
        },
        {
          "match": {
            "address": "高新区小区"
          }
        }
      ],
      "must_not": [
        {
          "term": {
            "name": {
              "value": "李四"
            }
          }
        }
      ],
      "should": [
        {
          "exists": {
            "field": "address3"
          }
        }
      ]
    }
  }
}

```

### 12、高亮显示

```json
# 字符串默认类型text，如果是中文，默认分词器standard

GET student/_mapping

PUT  student/_doc/1001
{
	"id":"1001",
	"name":"张三",
	"age":20,
	"sex":"男"
}
PUT  student/_doc/1002
{
	"id":"1002",
	"name":"李四",
	"age":25,
	"sex":"女"
}

PUT  student/_doc/1003
{
	"id":"1003",
	"name":"王五",
	"age":30,
	"sex":"女"
}

PUT  student/_doc/1004
{
	"id":"1004",
	"name":"赵六",
	"age":30,
	"sex":"男"
}

GET student/_search

# 不指定"pre_tags"和"post_tags"，默认是<em></em>
GET student/_search
{
  "query": {
    "match": {
      "name": "张五李四"
    }
  },
  "highlight": {
    "fields": {
      "name":{
        "pre_tags": "<font color='red'>",
        "post_tags": "</font>"
      }
    }
  }
}
```

### 13、指定响应字段

```json
# "_source" 表示只显示指定的数据
GET student/_search
{
  "query": {
    "match": {
      "name": "张五李四"
    }
  },
  "highlight": {
    "fields": {
      "name":{
        "pre_tags": "<font color='red'>",
        "post_tags": "</font>"
      }
    }
  },
  "_source": ["id","name","sex"]
}
```

### 13、聚合 / 统计

```json
#聚合查询
GET student/_search

# aggs 聚合aggregations
# aggr_count 名称任意，见名知义
# terms!!!!!：底层就是先分组，在count计数
GET student/_search
{
  "aggs": {
    "aggr_count": {
      "terms": {
        "field": "age"
      }
    }
  }
}

# value_count 统计个数
GET student/_search
{
  "aggs": {
    "aggr_count": {
      "value_count": {
        "field": "age"
      }
    }
  }
}

# max：统计最大值
GET student/_search
{
  "aggs": {
    "aggr_maxAge": {
      "max": {
        "field": "age"
      }
    }
  }
}

# avg：统计平均
GET student/_search
{
  "aggs": {
    "aggr_avgAge": {
      "avg": {
        "field": "age"
      }
    }
  }
}
```

### 14、批量操作

#### 批量查询

```json
# 批量操作1
POST  student/_doc/_mget
{
  "ids" : [ "1001", "1003" ]
}

GET student/_search
{
  "query": {
    "terms": {
      "id": ["1001","1003"]
    }
  }
}
```

#### bulk操作（打捆）

```json
# 批量创建（不要有空行）
POST _bulk
{"create":{"_index":"person2","_id":2001}}
{"id":2001,"name":"name1","age": 20,"sex": "男"}
{"create":{"_index":"person2","_id":2002}}
{"id":2002,"name":"name2","age": 20,"sex": "男"}
{"create":{"_index":"person2","_id":2003}}
{"id":2003,"name":"name3","age": 20,"sex": "男"}

GET person2/_search
DELETE person2/_doc/2001
DELETE person2

# 批量删除（原方法）
GET person2/_search
delete person2/_doc/2001
delete person2/_doc/2002
delete person2/_doc/2003
DELETE person2

# 批量删除（使用_bulk）
POST _bulk
{"delete":{"_index":"person2","_id":2001}}
{"delete":{"_index":"person2","_id":2002}}
{"delete":{"_index":"person2","_id":2003}}


```

### 15、分页

```json
# 批量创建
POST _bulk
{"index":{"_index":"person2"}}
{"name":"张三","age": 20,"mail": "111@qq.com","hobby":"羽毛球、乒乓球、足球"}
{"index":{"_index":"person2"}}
{"name":"李四","age": 21,"mail": "222@qq.com","hobby":"羽毛球、乒乓球、足球、篮球"}
{"index":{"_index":"person2"}}
{"name":"王五","age": 22,"mail": "333@qq.com","hobby":"羽毛球、篮球、游泳、听音乐"}
{"index":{"_index":"person2"}}
{"name":"赵六","age": 23,"mail": "444@qq.com","hobby":"跑步、游泳"}
{"index":{"_index":"person2"}}
{"name":"孙七","age": 24,"mail": "555@qq.com","hobby":"听音乐、看电影"}

GET person2/_search
{
  "from": 2,
  "size": 3
}

# 添加查询条件
GET person2/_search
{
  "query": {
    "match": {
      "hobby": "听音乐乒乓球"
    }
  }, 
  "from": 0,
  "size": 2
}
```

## 三、QueryString查询

数据在url地址后面的？后面传递，查询字符串，不经过请求体

```json
# 查询分类
# DSL查询 数据以JSON的方式提供，通过请求体传输
# queryString查询  数据在url地址后面的？后面传递，查询字符串，不经过请求体（只允许post和get）

GET person2/_search?from=2&size=2

# 不区分字段
GET person2/_search?q=跑步

GET person2/_search?q=name:跑步

GET person2/_search?q=hobby:跑步

POST person2/_search?q=hobby:跑步
# PUT person2/_search?q=hobby:跑步

GET person2/_search?sort=age:desc
```

## 四、重建索引

随着业务需求的变更，索引的结构可能发生改变。ElasticSearch的索引一旦创建，只允许添加字段，不允许改变字段。因为改变字段，需要重建倒排索引，影响内部缓存结构，性能太低。那么此时，就需要重建一个新的索引，并将原有索引的数据导入到新索引中。

- 原索引库 ：student_index_v1

- 新索引库 ：student_index_v2

```json
# 新建student_index_v1索引，索引名称必须全部小写
PUT student_index_v1 
{
  "mappings": {
    "properties": {
      "birthday":{
        "type": "date"
      }
    }
  }
}
# 查询索引
GET student_index_v1

# 添加数据
PUT student_index_v1/_doc/1
{
  "birthday":"2020-11-11"
}

# 查询数据
GET student_index_v1/_search

# 随着业务的变更，换种数据类型进行添加数据，程序会直接报错
PUT student_index_v1/_doc/2
{
  "birthday":"2020年11月11号"
}

# 业务变更，需要改变birthday数据类型为text
# 1：创建新的索引 student_index_v2
# 2：将student_index_v1 数据拷贝到 student_index_v2

# 创建新的索引
PUT student_index_v2 
{
  "mappings": {
    "properties": {
      "birthday":{
        "type": "text"
      }
    }
  }
}

# 2：将student_index_v1 数据拷贝到 student_index_v2
POST _reindex
{
  "source": {
    "index": "student_index_v1"
  },
  "dest": {
    "index": "student_index_v2"
  }
}

# 查询新索引库数据
GET student_index_v2/_search

# 在新的索引库里面添加数据
PUT student_index_v2/_doc/2
{
  "birthday":"2020年11月13号"
}
```

## 五、搭建集群（尚未测试）

```json
#测试集群
# 请求方法：PUT
PUT /shopping
{
  "settings": {},
  "mappings": {
      "properties": {
        "title":{
          "type": "text"
        },
        "subtitle":{
          "type": "text",
          "analyzer": "standard"
        },
        "images":{
          "type": "keyword",
          "index": false
        },
        "price":{
          "type": "float",
          "index": true
        }
      }
  }
}

POST /shopping/_doc/1
{
    "title":"小米手机",
    "images":"http://www.gulixueyuan.com/xm.jpg",
    "price":3999.00
}

GET _cluster/health
```

## 六、springboot项目中使用ES

### 1、elasticsearch-rest-high-level-client（更新快、淘汰快，不推荐）

> 项目地址：

Java REST Client 有两种风格：

- Java Low Level REST Client ：用于Elasticsearch的官方低级客户端。它允许通过HTTP与Elasticsearch集群通信。将请求编排和响应反编排留给用户自己处理。它兼容所有的Elasticsearch版本。（PS：学过WebService的话，对编排与反编排这个概念应该不陌生。可以理解为对请求参数的封装，以及对响应结果的解析）

- Java High Level REST Client ：用于Elasticsearch的官方高级客户端。它是基于低级客户端的，它提供很多API，并负责请求的编排与响应的反编排。（PS：就好比是，一个是传自己拼接好的字符串，并且自己解析返回的结果；而另一个是传对象，返回的结果也已经封装好了，直接是对象，更加规范了参数的名称以及格式，更加面对对象一点）

- 在 Elasticsearch 7.0 中不建议使用TransportClient，并且在8.0中会完全删除TransportClient。因此，官方更建议我们用Java High Level REST Client。

官网：https://www.elastic.co/guide/index.html

![image-20231025220227569](https://cdn.jsdelivr.net/gh/vincent-nicky/image_store/blog/image-20231025220227569.png)

![image-20231025220532904](https://cdn.jsdelivr.net/gh/vincent-nicky/image_store/blog/image-20231025220532904.png)

java-rest-high-client 7.17：https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html

### 2、spring-boot-starter-data-elasticsearch

> 项目地址：

导入依赖：

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

配置文件

```
spring:
  elasticsearch:
    uris: http://192.168.50.129:9200
```

**项目启动后，springboot会自动创建好索引和其映射，无需手动创建**

使用 ElasticsearchRestTemplate

```java
@Autowired
private ElasticsearchRestTemplate elasticsearchRestTemplate;

@Test
public void testAdd() {
    Item itemEs = new Item(1L, "小米手机7", " 手机",
            "小米", 3499.00, "http://image.leyou.com/13123.jpg");
    this.elasticsearchRestTemplate.save(itemEs); //save update saveAll
}
```

使用 ElasticsearchRepository

```java
package com.wsj.dao;
import com.wsj.entity.es.ItemEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;
public interface ItemDao extends ElasticsearchRepository<Item, Long> {
    List<Item> findByPriceBetween(Double price1, Double price2);
    List<Item> findByTitleAndPrice(String name, Double price);
}
```

```java
@Autowired
private ItemDao itemDao;

@Test
public void testSave() {
    Item itemEs = new Item(2L, "小米手机2", " 手机2", "小米2", 2499.00, "http://image.leyou.com/13123.jpg");
    itemDao.save(itemEs);
}
```

