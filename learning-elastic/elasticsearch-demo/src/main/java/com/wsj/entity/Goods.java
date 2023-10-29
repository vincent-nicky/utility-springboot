package com.wsj.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Goods {
    //@JSONField(serialize = false)//在转换JSON时，忽略该字段
    private int id;
    private String title;
    private double price;
    private int stock;
    private int saleNum;
    private Date createTime;
    private String categoryName;
    private String brandName;
    private Map spec; //将数据库中的json串解析成map进行数据封装
    @JSONField(serialize = false)//在转换JSON时，忽略该字段
    private String specStr;//接收数据库的信息 "{\"机身内存\":\"16G\",\"网络\":\"联通3G\"}"

}