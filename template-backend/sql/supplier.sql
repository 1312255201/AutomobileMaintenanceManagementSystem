CREATE TABLE `db_supplier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT 'Supplier Name',
  `address` varchar(255) DEFAULT NULL COMMENT 'Company Address',
  `contact_name` varchar(100) DEFAULT NULL COMMENT 'Contact Person Name',
  `contact_phone` varchar(50) DEFAULT NULL COMMENT 'Contact Phone Number',
  `email` varchar(100) DEFAULT NULL COMMENT 'Contact Email',
  `business_license` varchar(100) DEFAULT NULL COMMENT 'Business License Number',
  `tax_registration` varchar(100) DEFAULT NULL COMMENT 'Tax Registration Number',
  `cooperation_date` date DEFAULT NULL COMMENT 'Cooperation Start Date',
  `bank_account` varchar(100) DEFAULT NULL COMMENT 'Bank Account Number',
  `bank_name` varchar(255) DEFAULT NULL COMMENT 'Bank Name',
  `account_holder` varchar(255) DEFAULT NULL COMMENT 'Account Holder Name',
  `status` int DEFAULT '0' COMMENT '0: Active, 1: Inactive',
  `remark` text COMMENT 'Remarks',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Supplier Information Table';
