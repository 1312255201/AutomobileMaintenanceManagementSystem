CREATE TABLE `db_parts_inventory` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT 'Part Name',
  `category_id` int NOT NULL COMMENT 'Category ID',
  `brand` varchar(100) DEFAULT NULL COMMENT 'Brand',
  `price` decimal(10,2) NOT NULL COMMENT 'Unit Price',
  `quantity` int NOT NULL DEFAULT '0' COMMENT 'Stock Quantity',
  `description` text COMMENT 'Description',
  `precautions` text COMMENT 'Precautions',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Parts Inventory Table';

CREATE TABLE `db_parts_inbound` (
  `id` int NOT NULL AUTO_INCREMENT,
  `part_id` int NOT NULL COMMENT 'Part ID',
  `quantity` int NOT NULL COMMENT 'Inbound Quantity',
  `price` decimal(10,2) NOT NULL COMMENT 'Unit Price',
  `supplier_id` int DEFAULT NULL COMMENT 'Supplier ID',
  `operator_id` int NOT NULL COMMENT 'Operator User ID',
  `remark` text COMMENT 'Remark',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_part_id` (`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Parts Inbound Record Table';

CREATE TABLE `db_parts_outbound` (
  `id` int NOT NULL AUTO_INCREMENT,
  `part_id` int NOT NULL COMMENT 'Part ID',
  `quantity` int NOT NULL COMMENT 'Outbound Quantity',
  `price` decimal(10,2) NOT NULL COMMENT 'Selling Price',
  `customer_name` varchar(100) DEFAULT NULL COMMENT 'Customer Name (Redundant for display)',
  `appointment_id` int DEFAULT NULL COMMENT 'Associated Appointment ID',
  `operator_id` int NOT NULL COMMENT 'Operator User ID',
  `remark` text COMMENT 'Remark',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_part_id` (`part_id`),
  KEY `idx_appointment_id` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Parts Outbound Record Table';
