package com.wsj.entity.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName goods
 */
@TableName(value ="goods")
@Data
public class Goods implements Serializable {
    private Double id;

    private String title;

    private Long price;

    private Double stock;

    private Double saleNum;

    private Date createTime;

    private String categoryName;

    private String brandName;

    private String spec;

    private static final long serialVersionUID = 1L;
}