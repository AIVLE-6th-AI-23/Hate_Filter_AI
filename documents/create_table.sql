-- MySQL script to create tables for the database 'hate_filter_ai'

-- Table structure for `department`
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `dept_id` VARCHAR(255) NOT NULL,
  `dept_name` VARCHAR(255),
  PRIMARY KEY (`dept_id`)
);

-- Table structure for `user`
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT,
  `is_active` BIT(1),
  `created_at` DATETIME(6),
  `last_login` DATETIME(6),
  `dept_id` VARCHAR(255),
  `email` VARCHAR(255),
  `employee_id` VARCHAR(255),
  `pwd` VARCHAR(255),
  `user_name` VARCHAR(255),
  PRIMARY KEY (`user_id`),
  FOREIGN KEY (`dept_id`) REFERENCES `department` (`dept_id`)
);

-- Table structure for `board`
DROP TABLE IF EXISTS `board`;
CREATE TABLE `board` (
  `board_id` BIGINT NOT NULL AUTO_INCREMENT,
  `is_public` BIT(1),
  `created_at` DATETIME(6),
  `end_date` DATETIME(6),
  `board_title` VARCHAR(255),
  `description` VARCHAR(255),
  PRIMARY KEY (`board_id`)
);

-- Table structure for `post`
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `post_id` BIGINT NOT NULL AUTO_INCREMENT,
  `board_id` BIGINT,
  `created_at` DATETIME(6),
  `modified_at` DATETIME(6),
  `user_id` BIGINT,
  `view_count` BIGINT,
  `description` VARCHAR(255),
  `post_title` VARCHAR(255),
  PRIMARY KEY (`post_id`),
  FOREIGN KEY (`board_id`) REFERENCES `board` (`board_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

-- Table structure for `content_analysis`
DROP TABLE IF EXISTS `content_analysis`;
CREATE TABLE `content_analysis` (
  `analysis_id` BIGINT NOT NULL AUTO_INCREMENT,
  `analyzed_at` DATETIME(6),
  `post_id` BIGINT,
  `analysis_detail` VARCHAR(255),
  `content_type` VARCHAR(255),
  `status` VARCHAR(255),
  PRIMARY KEY (`analysis_id`),
  UNIQUE KEY (`post_id`),
  FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`)
);

-- Table structure for `hate_category`
DROP TABLE IF EXISTS `hate_category`;
CREATE TABLE `hate_category` (
  `category_id` BIGINT NOT NULL AUTO_INCREMENT,
  `severity_level` BIGINT,
  `category_name` VARCHAR(255),
  `description` VARCHAR(255),
  PRIMARY KEY (`category_id`)
);

-- Table structure for `analysis_category_result`
DROP TABLE IF EXISTS `analysis_category_result`;
CREATE TABLE `analysis_category_result` (
  `result_id` BIGINT NOT NULL AUTO_INCREMENT,
  `category_score` FLOAT,
  `analysis_id` BIGINT,
  `category_id` BIGINT,
  `detection_metadata` VARCHAR(255),
  PRIMARY KEY (`result_id`),
  FOREIGN KEY (`analysis_id`) REFERENCES `content_analysis` (`analysis_id`),
  FOREIGN KEY (`category_id`) REFERENCES `hate_category` (`category_id`)
);

-- Table structure for `audit_log`
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
  `log_id` BIGINT NOT NULL AUTO_INCREMENT,
  `action_time` DATETIME(6),
  `target_id` BIGINT,
  `user_id` BIGINT,
  `action_metadata` VARCHAR(255),
  `action_type` VARCHAR(255),
  `dept_id` VARCHAR(255),
  PRIMARY KEY (`log_id`),
  FOREIGN KEY (`dept_id`) REFERENCES `department` (`dept_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

-- Table structure for `role`
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `role_id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(255),
  PRIMARY KEY (`role_id`)
);

-- Table structure for `user_role`
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `role_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`role_id`, `user_id`),
  FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);
