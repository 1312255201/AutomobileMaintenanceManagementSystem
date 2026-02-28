USE carrepair;
ALTER TABLE db_maintenance_item ADD COLUMN part_id INT DEFAULT NULL COMMENT 'Associated Part ID for parts';
