package com.wsj.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SKMessageDTO implements Serializable {
    private long userId;
    private long goodsId;
}
