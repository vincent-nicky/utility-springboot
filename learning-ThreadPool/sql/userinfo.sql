CREATE DATABASE IF NOT EXISTS learning_thread_pool;

USE learning_thread_pool;

create table `userInfo`
(
    `id`       bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
    PRIMARY KEY (`id`)
) CHARSET = utf8mb4;