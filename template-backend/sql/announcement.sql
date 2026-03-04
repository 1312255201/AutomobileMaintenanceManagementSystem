CREATE TABLE `db_announcement` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容(HTML)',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面图',
  `type` varchar(20) NOT NULL DEFAULT 'notice' COMMENT '类型: notice/activity',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态: 1发布 0草稿',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告活动表';
