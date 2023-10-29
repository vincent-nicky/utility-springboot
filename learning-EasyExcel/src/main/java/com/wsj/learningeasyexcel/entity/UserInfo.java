package com.wsj.learningeasyexcel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 星球表格用户信息
 */
@Data
public class UserInfo {

    /**
     * id
     */
    @ExcelProperty("编号")
    private String id;

    /**
     * 用户昵称
     */
    @ExcelProperty("昵称")
    private String username;


}