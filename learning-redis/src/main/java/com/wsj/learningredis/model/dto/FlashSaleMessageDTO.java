package com.wsj.learningredis.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FlashSaleMessageDTO implements Serializable {
    private long userId;
    private long goodsId;
}
