/*
 Navicat Premium Dump SQL

 Source Server         : ming
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : yihu

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 04/06/2025 09:02:16
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`
(
    `id`                   int NOT NULL AUTO_INCREMENT,
    `project_id`           int NULL DEFAULT NULL,
    `title`                varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '活动标题',
    `notice_content`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '活动通知',
    `staff_count`          int NULL DEFAULT NULL COMMENT '工作人员人数',
    `volunteer_count`      int NULL DEFAULT NULL COMMENT '志愿者人数',
    `service_object_count` int NULL DEFAULT NULL COMMENT '服务对象人数',
    `questionnaire_id`     int NULL DEFAULT NULL,
    `status`               int NULL DEFAULT NULL COMMENT '0-草稿 1-已提交',
    `reject_reason`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `del_flag`             varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `create_time`          datetime NULL DEFAULT NULL,
    `create_by_id`         int NULL DEFAULT NULL,
    `update_time`          datetime NULL DEFAULT NULL,
    `update_by_id`         int NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for activity_files
-- ----------------------------
DROP TABLE IF EXISTS `activity_files`;
CREATE TABLE `activity_files`
(
    `id`           int NOT NULL AUTO_INCREMENT,
    `activity_id`  int NULL DEFAULT NULL COMMENT '活动id',
    `name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
    `storage_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名+时间戳，防止重名',
    `file_sort`    int NULL DEFAULT NULL COMMENT '文件分类 1-签到 2-活动档案 3-新闻稿',
    `file_url`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件保存路径',
    `del_flag`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for activity_news
-- ----------------------------
DROP TABLE IF EXISTS `activity_news`;
CREATE TABLE `activity_news`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `activity_id` int NULL DEFAULT NULL COMMENT '活动id',
    `platform`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新闻稿发布平台',
    `link`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '新闻稿链接',
    `del_flag`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '删除标识',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for answer
-- ----------------------------
DROP TABLE IF EXISTS `answer`;
CREATE TABLE `answer`
(
    `answerId`       int NOT NULL AUTO_INCREMENT,
    `fill_time`      datetime NULL DEFAULT NULL,
    `question_id`    int NOT NULL,
    `question_title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `question_type`  varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `write_value`    varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`answerId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for business
-- ----------------------------
DROP TABLE IF EXISTS `business`;
CREATE TABLE `business`
(
    `id`               int                                                           NOT NULL AUTO_INCREMENT,
    `order_no`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `apply_user_id`    int                                                           NOT NULL,
    `accept_expert_id` int NULL DEFAULT NULL,
    `status`           int                                                           NOT NULL,
    `create_at`        datetime                                                      NOT NULL,
    `end_at`           datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX              `FK_order_no`(`order_no` ASC) USING BTREE,
    CONSTRAINT `FK_order_no` FOREIGN KEY (`order_no`) REFERENCES `order` (`order_no`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for communication
-- ----------------------------
DROP TABLE IF EXISTS `communication`;
CREATE TABLE `communication`
(
    `id`              int NOT NULL AUTO_INCREMENT,
    `business_id`     int NULL DEFAULT NULL,
    `send_user_id`    int NULL DEFAULT NULL,
    `receive_user_id` int NULL DEFAULT NULL,
    `content`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
    `time`            datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX             `FK_receive_user_id`(`receive_user_id` ASC) USING BTREE,
    INDEX             `FK_send_user_id`(`send_user_id` ASC) USING BTREE,
    INDEX             `FK_server_id`(`business_id` ASC) USING BTREE,
    CONSTRAINT `FK_receive_user_id` FOREIGN KEY (`receive_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FK_send_user_id` FOREIGN KEY (`send_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FK_server_id` FOREIGN KEY (`business_id`) REFERENCES `business` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 985 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for communication_file
-- ----------------------------
DROP TABLE IF EXISTS `communication_file`;
CREATE TABLE `communication_file`
(
    `id`               int NOT NULL AUTO_INCREMENT,
    `communication_id` int NOT NULL,
    `file_id`          int NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX              `FK_communication_id`(`communication_id` ASC) USING BTREE,
    INDEX              `fk_communication_file_file_1`(`file_id` ASC) USING BTREE,
    CONSTRAINT `fk_communication_file_file_1` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FK_communication_id` FOREIGN KEY (`communication_id`) REFERENCES `communication` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`
(
    `id`            int                                                           NOT NULL AUTO_INCREMENT,
    `name`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `storage_path`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `file_type`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `file_size`     bigint                                                        NOT NULL,
    `create_time`   datetime                                                      NOT NULL,
    `md5`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `quote_count`   int                                                           NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 94 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for invite_expert_record
-- ----------------------------
DROP TABLE IF EXISTS `invite_expert_record`;
CREATE TABLE `invite_expert_record`
(
    `id`             int      NOT NULL AUTO_INCREMENT,
    `invite_user_id` int      NOT NULL,
    `create_at`      datetime NOT NULL,
    `deadline`       datetime NOT NULL,
    `isAgree`        int NULL DEFAULT NULL,
    `expert_user_id` int NULL DEFAULT NULL,
    `refuseReason`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `fk_invite_user_id`(`invite_user_id` ASC) USING BTREE,
    INDEX            `fk_specialis_user_id`(`expert_user_id` ASC) USING BTREE,
    CONSTRAINT `fk_invite_user_id` FOREIGN KEY (`invite_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_specialis_user_id` FOREIGN KEY (`expert_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for membership
-- ----------------------------
DROP TABLE IF EXISTS `membership`;
CREATE TABLE `membership`
(
    `membership_id` int                                                           NOT NULL AUTO_INCREMENT,
    `order_no`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `user_id`       int NULL DEFAULT NULL,
    `buy_date`      datetime                                                      NOT NULL,
    `deadline`      datetime                                                      NOT NULL,
    `grade`         int                                                           NOT NULL,
    PRIMARY KEY (`membership_id`) USING BTREE,
    INDEX           `FK_member_order`(`order_no` ASC) USING BTREE,
    INDEX           `FK_member_user`(`user_id` ASC) USING BTREE,
    CONSTRAINT `FK_member_order` FOREIGN KEY (`order_no`) REFERENCES `order` (`order_no`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FK_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`
(
    `order_no`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `buyer_id`        int                                                           NOT NULL,
    `type`            int                                                           NOT NULL,
    `payment_type`    int NULL DEFAULT NULL,
    `other_order_no`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `payment_amount`  float NULL DEFAULT NULL,
    `payee_id`        int NULL DEFAULT NULL,
    `draw_proportion` float NULL DEFAULT NULL,
    `received_amount` float NULL DEFAULT NULL,
    `status`          int                                                           NOT NULL,
    `create_at`       datetime                                                      NOT NULL,
    `pay_at`          datetime NULL DEFAULT NULL,
    `end_at`          datetime NULL DEFAULT NULL,
    `delete_flag`     int NULL DEFAULT NULL,
    `delete_at`       datetime NULL DEFAULT NULL,
    `delete_by_id`    int NULL DEFAULT NULL,
    PRIMARY KEY (`order_no`) USING BTREE,
    INDEX             `FK_buyer_id`(`buyer_id` ASC) USING BTREE,
    INDEX             `FK_delete_by_id`(`delete_by_id` ASC) USING BTREE,
    INDEX             `FK_payee_id`(`payee_id` ASC) USING BTREE,
    CONSTRAINT `FK_buyer_id` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FK_delete_by_id` FOREIGN KEY (`delete_by_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FK_payee_id` FOREIGN KEY (`payee_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`
(
    `id`          int                                                           NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `type`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `price`       float                                                         NOT NULL,
    `discount`    float                                                         NOT NULL,
    `proportion`  float                                                         NOT NULL,
    `vip_time`    int NULL DEFAULT NULL COMMENT '只有会员商品需要填，定制服务不需要',
    `create_user` int                                                           NOT NULL,
    `create_at`   datetime                                                      NOT NULL,
    `update_user` int NULL DEFAULT NULL,
    `update_at`   datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for proj_file
-- ----------------------------
DROP TABLE IF EXISTS `proj_file`;
CREATE TABLE `proj_file`
(
    `id`         int NOT NULL AUTO_INCREMENT,
    `project_id` int NOT NULL,
    `file_id`    int NULL DEFAULT NULL,
    `type`       int NOT NULL COMMENT 'type=0为目录节点，=2为文件节点',
    `parent_id`  int NULL DEFAULT NULL COMMENT '此项为空整体为根，此项非空整体为节点',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX        `fk_proj_file_project_1`(`project_id` ASC) USING BTREE,
    INDEX        `fk_proj_file_project_2`(`file_id` ASC) USING BTREE,
    CONSTRAINT `fk_proj_file_project_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_proj_file_project_2` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '有文件id的为文件节点\r\n无文件id的为文件夹节点\r\n整体表格组成树' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`
(
    `id`            int                                                           NOT NULL AUTO_INCREMENT,
    `name`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `status`        int NULL DEFAULT NULL,
    `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `del_flag`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `create_time`   datetime NULL DEFAULT NULL,
    `create_by_id`  int NULL DEFAULT NULL,
    `update_time`   datetime NULL DEFAULT NULL,
    `update_by_id`  int NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for qualification_info
-- ----------------------------
DROP TABLE IF EXISTS `qualification_info`;
CREATE TABLE `qualification_info`
(
    `id`        int                                                           NOT NULL AUTO_INCREMENT,
    `record_id` int                                                           NOT NULL,
    `name`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `file_id`   int                                                           NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX       `fk_record_id`(`record_id` ASC) USING BTREE,
    INDEX       `fk_qualification_info_file_1`(`file_id` ASC) USING BTREE,
    CONSTRAINT `fk_qualification_info_file_1` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_record_id` FOREIGN KEY (`record_id`) REFERENCES `invite_expert_record` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`
(
    `question_id`          int NOT NULL AUTO_INCREMENT,
    `details`              varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `question_description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
    `question_nullable`    bit(1) NULL DEFAULT NULL,
    `question_title`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `question_type`        varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `questionnaire_id`     int NOT NULL,
    PRIMARY KEY (`question_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for questionnaire
-- ----------------------------
DROP TABLE IF EXISTS `questionnaire`;
CREATE TABLE `questionnaire`
(
    `questionnaire_id` int NOT NULL AUTO_INCREMENT,
    `create_time`      datetime NULL DEFAULT NULL,
    `start_time`       datetime NULL DEFAULT NULL,
    `end_time`         datetime NULL DEFAULT NULL,
    `status`           int NULL DEFAULT NULL,
    `fill_count`       int NULL DEFAULT NULL,
    `delete_flag`      int NULL DEFAULT NULL,
    PRIMARY KEY (`questionnaire_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for questionnaire_ip
-- ----------------------------
DROP TABLE IF EXISTS `questionnaire_ip`;
CREATE TABLE `questionnaire_ip`
(
    `id`               int NOT NULL AUTO_INCREMENT,
    `questionnaire_id` int NULL DEFAULT NULL,
    `ip`               varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_setting`;
CREATE TABLE `sys_setting`
(
    `id`             int      NOT NULL AUTO_INCREMENT,
    `name`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `content`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `parent_id`      int NULL DEFAULT NULL,
    `last_chaget_at` datetime NOT NULL,
    `last_change_id` int      NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `FK_change_userid`(`last_change_id` ASC) USING BTREE,
    CONSTRAINT `FK_change_userid` FOREIGN KEY (`last_change_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for temp
-- ----------------------------
DROP TABLE IF EXISTS `temp`;
CREATE TABLE `temp`
(
    `temp_id`              int NOT NULL AUTO_INCREMENT,
    `question_title`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `question_description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `question_nullable`    bit(1) NULL DEFAULT NULL,
    `question_type`        varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `details`              varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`temp_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for upload_configuration
-- ----------------------------
DROP TABLE IF EXISTS `upload_configuration`;
CREATE TABLE `upload_configuration`
(
    `id`                 int                                                           NOT NULL AUTO_INCREMENT,
    `type`               int                                                           NOT NULL,
    `menu_levels`        int                                                           NOT NULL COMMENT '菜单等级,如:1,2,3',
    `menu_name`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名',
    `menu_path`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单路径，如1-1，1-2，1-2-3',
    `del_flag`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `create_time`        datetime NULL DEFAULT NULL,
    `create_by_id`       int NULL DEFAULT NULL,
    `last_updated_time`  datetime NULL DEFAULT NULL,
    `last_updated_by_id` int NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`              int                                                           NOT NULL AUTO_INCREMENT,
    `name`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `phone`           varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `description`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
    `password`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `role`            int                                                           NOT NULL,
    `location`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `status`          int                                                           NOT NULL,
    `balance`         float                                                         NOT NULL,
    `create_at`       datetime                                                      NOT NULL,
    `last_login_time` datetime                                                      NOT NULL,
    `updater_id`      int NULL DEFAULT NULL,
    `update_at`       datetime NULL DEFAULT NULL,
    `delete_flag`     int                                                           NOT NULL,
    `delete_at`       datetime NULL DEFAULT NULL,
    `deleter_id`      int NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_sitting
-- ----------------------------
DROP TABLE IF EXISTS `user_sitting`;
CREATE TABLE `user_sitting`
(
    `id`             int      NOT NULL AUTO_INCREMENT,
    `user_id`        int      NOT NULL,
    `name`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `content`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `parent_id`      int NULL DEFAULT NULL,
    `last_chaget_at` datetime NOT NULL,
    `last_change_id` int      NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `fk_user_sitting_user_1`(`user_id` ASC) USING BTREE,
    INDEX            `fk_user_sitting_user_2`(`last_change_id` ASC) USING BTREE,
    CONSTRAINT `fk_user_sitting_user_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_user_sitting_user_2` FOREIGN KEY (`last_change_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET
FOREIGN_KEY_CHECKS = 1;
