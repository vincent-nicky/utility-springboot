package com.wsj.test;

import com.alibaba.fastjson2.JSON;
import com.wsj.entity.Goods;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TestQuery {
    @Autowired
    private RestHighLevelClient client;
    @Test
    public void testMatchAll() throws IOException {
        //1.创建一个查询请求
        SearchRequest request = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();//!!!
        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);

        //2.执行查询请求并得到响应
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //3.将响应结果封装到List<Goods>中
        List<Goods> goodsList = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println("total="+total); //ES中goods索引的文档的总数量total，并不是当前页的数量
        SearchHit[] hitArr = hits.getHits();
        for(SearchHit hit :hitArr){
            //获取文档字符串
            String sourceAsString = hit.getSourceAsString();
            //将文档字符串转换给Goods对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }
        //4.遍历List<Goods>
        System.out.println("goodsList.size="+goodsList.size());//Java中goodsList的元素个数
        goodsList.forEach(goods -> System.out.println(goods));

    }

    @Test
    public void testMatch() throws IOException {
        //1.创建一个查询请求
        SearchRequest request = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders
                .matchQuery("title","华为手机")
                .operator(Operator.AND);
        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);

        //2.执行查询请求并得到响应
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //3.将响应结果封装到List<Goods>中
        List<Goods> goodsList = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println("total="+total); //ES中goods索引的文档的总数量total，并不是当前页的数量
        SearchHit[] hitArr = hits.getHits();
        for(SearchHit hit :hitArr){
            //获取文档字符串
            String sourceAsString = hit.getSourceAsString();
            //将文档字符串转换给Goods对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }
        //4.遍历List<Goods>
        System.out.println("goodsList.size="+goodsList.size());//Java中goodsList的元素个数
        goodsList.forEach(goods -> System.out.println(goods));
    }

    @Test
    public void testTerm() throws IOException {
        //1.创建一个查询请求
        SearchRequest request = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.termQuery("categoryName","手机");
        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);

        //2.执行查询请求并得到响应
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //3.将响应结果封装到List<Goods>中
        List<Goods> goodsList = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println("total="+total); //ES中goods索引的文档的总数量total，并不是当前页的数量
        SearchHit[] hitArr = hits.getHits();
        for(SearchHit hit :hitArr){
            //获取文档字符串
            String sourceAsString = hit.getSourceAsString();
            //将文档字符串转换给Goods对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }
        //4.遍历List<Goods>
        System.out.println("goodsList.size="+goodsList.size());//Java中goodsList的元素个数
        goodsList.forEach(goods -> System.out.println(goods));
    }

    @Test
    public void testWildcard() throws IOException { //通配符
        //1.创建一个查询请求
        SearchRequest request = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("title","华*");
        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);

        //2.执行查询请求并得到响应
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //3.将响应结果封装到List<Goods>中
        List<Goods> goodsList = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println("total="+total); //ES中goods索引的文档的总数量total，并不是当前页的数量
        SearchHit[] hitArr = hits.getHits();
        for(SearchHit hit :hitArr){
            //获取文档字符串
            String sourceAsString = hit.getSourceAsString();
            //将文档字符串转换给Goods对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }
        //4.遍历List<Goods>
        System.out.println("goodsList.size="+goodsList.size());//Java中goodsList的元素个数
        goodsList.forEach(goods -> System.out.println(goods));
    }

    /**
     QueryBuilder: 指定查询条件 matchQuery termQuery  rangeQuery  wildCardQuery .boolQuery()
     SearchSourceBuilder:不仅可以包含查询条件，还可以指定排序、分页、高亮、聚合
     */
    @Test
    public void testRange() throws IOException {
        //1.创建一个查询请求
        SearchRequest request = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders
               .rangeQuery("price").gte(2000).lte(3000);

        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);
        searchSourceBuilder.sort("price", SortOrder.DESC);

        //2.执行查询请求并得到响应
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //3.将响应结果封装到List<Goods>中
        List<Goods> goodsList = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println("total="+total); //ES中goods索引的文档的总数量total，并不是当前页的数量
        SearchHit[] hitArr = hits.getHits();
        for(SearchHit hit :hitArr){
            //获取文档字符串
            String sourceAsString = hit.getSourceAsString();
            //将文档字符串转换给Goods对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }
        //4.遍历List<Goods>
        System.out.println("goodsList.size="+goodsList.size());//Java中goodsList的元素个数
        goodsList.forEach(goods -> System.out.println(goods));
    }

    @Test
    public void testQueryString() throws IOException {
        //1.创建一个查询请求
        SearchRequest request = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("华为手机")
                .field("title")
                .field("categoryName")//key
                .field("brandName");// key 在所有字段中查询华为手机 分词

        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);
//
//        searchSourceBuilder.from(0);
//        searchSourceBuilder.size(200);
//        searchSourceBuilder.sort("price", SortOrder.DESC);

        //2.执行查询请求并得到响应
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //3.将响应结果封装到List<Goods>中
        List<Goods> goodsList = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println("total="+total); //ES中goods索引的文档的总数量total，并不是当前页的数量
        SearchHit[] hitArr = hits.getHits();
        for(SearchHit hit :hitArr){
            //获取文档字符串
            String sourceAsString = hit.getSourceAsString();
            //将文档字符串转换给Goods对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }
        //4.遍历List<Goods>
        System.out.println("goodsList.size="+goodsList.size());//Java中goodsList的元素个数
        goodsList.forEach(goods -> System.out.println(goods));
    }

    @Test
    public void testBool() throws IOException {
        //1.创建一个查询请求
        SearchRequest request = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder1 = QueryBuilders.termQuery("brandName","华为");
        boolQueryBuilder.must(queryBuilder1);
        QueryBuilder queryBuilder2 = QueryBuilders.termQuery("title","手机");
        boolQueryBuilder.filter(queryBuilder2);
        QueryBuilder queryBuilder3 = QueryBuilders.rangeQuery("price").gte(2000).lte(3000);
        boolQueryBuilder.filter(queryBuilder3);

        //boolQueryBuilder.mustNot();
        //boolQueryBuilder.should();

        searchSourceBuilder.query(boolQueryBuilder);
        request.source(searchSourceBuilder);

        //2.执行查询请求并得到响应
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //3.将响应结果封装到List<Goods>中
        List<Goods> goodsList = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println("total="+total); //ES中goods索引的文档的总数量total，并不是当前页的数量
        SearchHit[] hitArr = hits.getHits();
        for(SearchHit hit :hitArr){
            //获取文档字符串
            String sourceAsString = hit.getSourceAsString();
            //将文档字符串转换给Goods对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }
        //4.遍历List<Goods>
        System.out.println("goodsList.size="+goodsList.size());//Java中goodsList的元素个数
        goodsList.forEach(goods -> System.out.println(goods));
    }


    @Test
    public void testHighlight() throws IOException {
        //1.创建一个查询请求
        SearchRequest request = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders
                .matchQuery("title","电视");
        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);



        //2.执行查询请求并得到响应
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //3.将响应结果封装到List<Goods>中
        List<Goods> goodsList = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println("total="+total); //ES中goods索引的文档的总数量total，并不是当前页的数量
        SearchHit[] hitArr = hits.getHits();
        for(SearchHit hit :hitArr){
            //获取文档字符串
            String sourceAsString = hit.getSourceAsString();
            //将文档字符串转换给Goods对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("title");
            Text[] fragments = highlightField.fragments();
            String title = fragments[0].toString();
            goods.setTitle(title);
            goodsList.add(goods);
        }
        //4.遍历List<Goods>
        System.out.println("goodsList.size="+goodsList.size());//Java中goodsList的元素个数
        goodsList.forEach(goods -> System.out.println(goods));
    }

    @Test
    public void testAggr() throws IOException {
        //1.创建一个查询请求
        SearchRequest request = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders
                .matchQuery("title","电视");
        searchSourceBuilder.query(queryBuilder);
        request.source(searchSourceBuilder);

        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("goods_brands").field("brandName").size(100);
        searchSourceBuilder.aggregation(aggregationBuilder);

        //2.执行查询请求并得到响应
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //3.将响应结果封装到List<Goods>中
        List<Goods> goodsList = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        System.out.println("total="+total); //ES中goods索引的文档的总数量total，并不是当前页的数量
        SearchHit[] hitArr = hits.getHits();
        for(SearchHit hit :hitArr){
            //获取文档字符串
            String sourceAsString = hit.getSourceAsString();
            //将文档字符串转换给Goods对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
           goodsList.add(goods);
        }
        //4.遍历List<Goods>
        System.out.println("goodsList.size="+goodsList.size());//Java中goodsList的元素个数
        goodsList.forEach(goods -> System.out.println(goods));

        //获取聚合信息
        Aggregations aggregations = response.getAggregations();
        Map<String, Aggregation> map = aggregations.asMap();
        //System.out.println(map);
        Terms goods_brands =(Terms) map.get("goods_brands");
        //System.out.println(goods_brands);
        List<? extends Terms.Bucket> buckets = goods_brands.getBuckets();
       for(Terms.Bucket bucket : buckets){
           System.out.println(bucket.getKey()+"  "+bucket.getDocCount());
       }

    }

}
