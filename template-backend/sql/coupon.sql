CREATE TABLE `db_coupon` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '优惠券名称',
  `discount_amount` decimal(10,2) NOT NULL COMMENT '优惠金额',
  `condition_amount` decimal(10,2) DEFAULT '0.00' COMMENT '使用门槛(满多少可用)',
  `user_id` int NOT NULL COMMENT '所属用户ID',
  `valid_start` datetime NOT NULL COMMENT '有效期开始',
  `valid_end` datetime NOT NULL COMMENT '有效期结束',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态 0:未使用 1:已使用 2:已过期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='优惠券表';
