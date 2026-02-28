CREATE TABLE `db_maintenance_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL COMMENT 'Associated Appointment ID',
  `repairman_id` int NOT NULL COMMENT 'Assigned Repairman ID',
  `status` int DEFAULT 0 COMMENT '0: Pending, 1: In Progress, 2: Completed, 3: Paid',
  `total_cost` decimal(10,2) DEFAULT 0.00 COMMENT 'Total Cost',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_appointment_id` (`appointment_id`),
  KEY `idx_repairman_id` (`repairman_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Maintenance Order Table';

CREATE TABLE `db_maintenance_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL COMMENT 'Maintenance Order ID',
  `item_name` varchar(100) NOT NULL COMMENT 'Item Name (Labor or Part)',
  `item_type` tinyint(1) NOT NULL COMMENT '1: Labor, 2: Part',
  `cost` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Cost',
  `quantity` int DEFAULT 1 COMMENT 'Quantity (for parts)',
  `remark` varchar(255) DEFAULT NULL COMMENT 'Remark',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Maintenance Order Items';
