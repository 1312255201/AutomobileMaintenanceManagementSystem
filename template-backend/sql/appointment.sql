CREATE TABLE `db_appointment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `car_model` varchar(255) NOT NULL COMMENT 'Car Model',
  `license_plate` varchar(50) NOT NULL COMMENT 'License Plate',
  `appointment_time` datetime NOT NULL COMMENT 'Appointment Time',
  `service_type` varchar(100) NOT NULL COMMENT 'Service Type',
  `description` text COMMENT 'Description',
  `status` int DEFAULT '0' COMMENT '0: Pending, 1: Confirmed, 2: Completed, 3: Cancelled',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Appointment Table';
