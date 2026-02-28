CREATE TABLE `db_repairman` (
  `id` int NOT NULL AUTO_INCREMENT,
  `account_id` int NOT NULL COMMENT 'Associated Account ID',
  `name` varchar(100) NOT NULL COMMENT 'Repairman Name',
  `phone` varchar(20) DEFAULT NULL COMMENT 'Phone Number',
  `email` varchar(255) DEFAULT NULL COMMENT 'Email',
  `introduction` text COMMENT 'Self Introduction',
  `specialization` varchar(255) DEFAULT NULL COMMENT 'Specialization Areas',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Repairman Information Table';
