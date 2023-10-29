package com.wsj.entity.es;

import com.wsj.entity.domain.Goods;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName goods
 */
@Data
@Document(indexName = "goods")
public class GoodsEs implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @Id
    private Double id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Keyword)
    private Long price;

    @Field(type = FieldType.Keyword)
    private Double stock;

    @Field(type = FieldType.Keyword)
    private Double saleNum;

    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Keyword)
    private String brandName;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String spec;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param goods
     * @return
     */
    public static GoodsEs objToDto(Goods goods) {
        if (goods == null) {
            return null;
        }
        GoodsEs goodsEs = new GoodsEs();
        BeanUtils.copyProperties(goods, goodsEs);
        return goodsEs;
    }

    /**
     * 包装类转对象
     *
     * @param goodsEs
     * @return
     */
    public static Goods dtoToObj(GoodsEs goodsEs) {
        if (goodsEs == null) {
            return null;
        }
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsEs, goods);
        return goods;
    }
}