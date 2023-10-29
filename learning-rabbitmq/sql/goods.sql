CREATE DATABASE IF NOT EXISTS learning_flash_sale;

USE learning_flash_sale;

create table `goods`
(
    `id`       bigint NOT NULL AUTO_INCREMENT COMMENT '商品id',
    `name` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品名称',
    `count` bigint COMMENT '商品数量',
    PRIMARY KEY (`id`)
) CHARSET = utf8mb4;