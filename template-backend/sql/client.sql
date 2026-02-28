CREATE TABLE `db_client` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` int NOT NULL COMMENT 'Account ID',
  `name` varchar(50) DEFAULT NULL COMMENT 'Real Name',
  `gender` tinyint(1) DEFAULT 0 COMMENT '0: Unknown, 1: Male, 2: Female',
  `phone` varchar(20) DEFAULT NULL,
  `wechat` varchar(50) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Client Information Table';
