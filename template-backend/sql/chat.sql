CREATE TABLE `db_chat_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL COMMENT '维修单ID',
  `sender_id` int NOT NULL COMMENT '发送者ID',
  `sender_role` varchar(20) NOT NULL COMMENT '发送者角色(user/repairman/admin)',
  `sender_name` varchar(50) DEFAULT NULL COMMENT '发送者名称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '发送者头像',
  `content` text COMMENT '消息内容',
  `type` varchar(10) NOT NULL DEFAULT 'text' COMMENT '消息类型(text/image)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='聊天记录表';
