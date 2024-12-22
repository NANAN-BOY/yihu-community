/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : form

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 22/12/2024 18:00:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for field_values
-- ----------------------------
DROP TABLE IF EXISTS `field_values`;
CREATE TABLE `field_values`  (
  `field_value_id` int NOT NULL AUTO_INCREMENT,
  `entry_id` int NOT NULL,
  `template_fields_id` int NOT NULL,
  `field_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`field_value_id`) USING BTREE,
  INDEX `FK_Reference_5`(`entry_id` ASC) USING BTREE,
  INDEX `FK_Reference_6`(`template_fields_id` ASC) USING BTREE,
  CONSTRAINT `FK_Reference_5` FOREIGN KEY (`entry_id`) REFERENCES `form_entries` (`entry_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_Reference_6` FOREIGN KEY (`template_fields_id`) REFERENCES `template_fields` (`template_fields_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of field_values
-- ----------------------------
INSERT INTO `field_values` VALUES (1, 1, 1, '1');
INSERT INTO `field_values` VALUES (2, 1, 2, '18-25');
INSERT INTO `field_values` VALUES (3, 1, 3, '1');
INSERT INTO `field_values` VALUES (4, 1, 4, 'html.txt');
INSERT INTO `field_values` VALUES (5, 2, 1, '1');
INSERT INTO `field_values` VALUES (6, 2, 2, '18-25');
INSERT INTO `field_values` VALUES (7, 2, 3, '1');
INSERT INTO `field_values` VALUES (8, 2, 4, 'html.txt');
INSERT INTO `field_values` VALUES (9, 3, 5, '田会雷');
INSERT INTO `field_values` VALUES (10, 3, 6, '男');
INSERT INTO `field_values` VALUES (11, 3, 7, '汉族');
INSERT INTO `field_values` VALUES (12, 3, 8, '软件2202');
INSERT INTO `field_values` VALUES (13, 3, 9, '预备党员');

-- ----------------------------
-- Table structure for form_entries
-- ----------------------------
DROP TABLE IF EXISTS `form_entries`;
CREATE TABLE `form_entries`  (
  `entry_id` int NOT NULL AUTO_INCREMENT,
  `template_id` int NOT NULL,
  `user_id` int NOT NULL,
  `submitted_at` datetime NOT NULL,
  PRIMARY KEY (`entry_id`) USING BTREE,
  INDEX `FK_Reference_3`(`template_id` ASC) USING BTREE,
  INDEX `FK_Reference_4`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK_Reference_3` FOREIGN KEY (`template_id`) REFERENCES `projecttemplate` (`template_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_Reference_4` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of form_entries
-- ----------------------------
INSERT INTO `form_entries` VALUES (1, 1, 1, '2024-12-21 15:56:49');
INSERT INTO `form_entries` VALUES (2, 1, 1, '2024-12-21 16:10:18');
INSERT INTO `form_entries` VALUES (3, 2, 1, '2024-12-22 16:22:07');

-- ----------------------------
-- Table structure for form_fields
-- ----------------------------
DROP TABLE IF EXISTS `form_fields`;
CREATE TABLE `form_fields`  (
  `form_fields_id` int NOT NULL AUTO_INCREMENT,
  `forms_id` int NULL DEFAULT NULL,
  `form_id` int NOT NULL,
  `form_fields_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `form_fields_type` int NOT NULL,
  `isRequired` tinyint(1) NOT NULL,
  `form_fields_options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`form_fields_id`) USING BTREE,
  INDEX `FK_Reference_1`(`forms_id` ASC) USING BTREE,
  CONSTRAINT `FK_Reference_1` FOREIGN KEY (`forms_id`) REFERENCES `forms` (`forms_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of form_fields
-- ----------------------------

-- ----------------------------
-- Table structure for forms
-- ----------------------------
DROP TABLE IF EXISTS `forms`;
CREATE TABLE `forms`  (
  `forms_id` int NOT NULL AUTO_INCREMENT,
  `form_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `form_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`forms_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forms
-- ----------------------------

-- ----------------------------
-- Table structure for projecttemplate
-- ----------------------------
DROP TABLE IF EXISTS `projecttemplate`;
CREATE TABLE `projecttemplate`  (
  `template_id` int NOT NULL AUTO_INCREMENT,
  `template_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `template_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `template_enable` int NOT NULL,
  PRIMARY KEY (`template_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of projecttemplate
-- ----------------------------
INSERT INTO `projecttemplate` VALUES (1, '信息收集', '收集信息', 0);
INSERT INTO `projecttemplate` VALUES (2, '学生会报名表', '学生会报名信息收集', 1);

-- ----------------------------
-- Table structure for template_fields
-- ----------------------------
DROP TABLE IF EXISTS `template_fields`;
CREATE TABLE `template_fields`  (
  `template_fields_id` int NOT NULL AUTO_INCREMENT,
  `template_id` int NULL DEFAULT NULL,
  `template_fields_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `template_fields_type` int NOT NULL,
  `fields_isRequired` tinyint(1) NOT NULL,
  `template_fields_options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`template_fields_id`) USING BTREE,
  INDEX `FK_Reference_2`(`template_id` ASC) USING BTREE,
  CONSTRAINT `FK_Reference_2` FOREIGN KEY (`template_id`) REFERENCES `projecttemplate` (`template_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of template_fields
-- ----------------------------
INSERT INTO `template_fields` VALUES (1, 1, 'name', 1, 1, NULL);
INSERT INTO `template_fields` VALUES (2, 1, '年龄', 3, 1, '[\"18-25\",\"25-35\",\"35-45\"]');
INSERT INTO `template_fields` VALUES (3, 1, '详细信息', 2, 1, NULL);
INSERT INTO `template_fields` VALUES (4, 1, '上传附件', 4, 1, NULL);
INSERT INTO `template_fields` VALUES (5, 2, '姓名', 1, 1, NULL);
INSERT INTO `template_fields` VALUES (6, 2, '性别', 1, 1, NULL);
INSERT INTO `template_fields` VALUES (7, 2, '民族', 1, 1, NULL);
INSERT INTO `template_fields` VALUES (8, 2, '班级', 1, 1, NULL);
INSERT INTO `template_fields` VALUES (9, 2, '政治面貌', 1, 1, NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `hash_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_role` int NOT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '123', '$2a$10$USP4qXO2glEsoKZPblEBXOtc1/0bon1RLMzWhocEA/Elz2.N2CCc2', 3);
INSERT INTO `user` VALUES (2, '1234', '$2a$10$0l00ddtZZM/TIOpangDaTeKaLknzyzo295cGtcIWcvvFkp89DSU6e', 2);

SET FOREIGN_KEY_CHECKS = 1;
