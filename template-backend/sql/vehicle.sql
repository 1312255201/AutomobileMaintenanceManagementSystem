CREATE TABLE `db_vehicle` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` int NOT NULL COMMENT 'Owner Account ID',
  `license_plate` varchar(20) NOT NULL COMMENT 'License Plate Number',
  `vin` varchar(50) DEFAULT NULL COMMENT 'Vehicle Identification Number',
  `engine_number` varchar(50) DEFAULT NULL COMMENT 'Engine Number',
  `brand` varchar(50) DEFAULT NULL COMMENT 'Car Brand',
  `model` varchar(50) DEFAULT NULL COMMENT 'Car Model',
  `color` varchar(20) DEFAULT NULL COMMENT 'Car Color',
  `mileage` int DEFAULT 0 COMMENT 'Current Mileage (km)',
  `year` varchar(10) DEFAULT NULL COMMENT 'Production Year',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User Vehicle Information';
