-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: localhost    Database: haiyun_meal
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `haiyun_meal`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `haiyun_meal` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `haiyun_meal`;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stall_id` bigint DEFAULT NULL COMMENT '所属档口',
  `type` int DEFAULT NULL COMMENT '类型   1 菜品分类 2 套餐分类',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '顺序',
  `status` int DEFAULT NULL COMMENT '分类状态 0:禁用，1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_category_name` (`name`),
  KEY `idx_category_stall` (`stall_id`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='菜品及套餐分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dish`
--

DROP TABLE IF EXISTS `dish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '菜品名称',
  `category_id` bigint NOT NULL COMMENT '菜品分类id',
  `price` decimal(10,2) DEFAULT NULL COMMENT '菜品价格',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '图片',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '描述信息',
  `status` int DEFAULT '1' COMMENT '0 停售 1 起售',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_dish_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='菜品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dish_flavor`
--

DROP TABLE IF EXISTS `dish_flavor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish_flavor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dish_id` bigint NOT NULL COMMENT '菜品',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '口味名称',
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '口味数据list',
  PRIMARY KEY (`id`),
  KEY `idx_dish_id` (`dish_id`)
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='菜品口味关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '姓名',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '性别',
  `id_number` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '身份证号',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态 0:禁用，1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='员工信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `employee_role`
--

DROP TABLE IF EXISTS `employee_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL COMMENT '员工id',
  `role_id` bigint NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_emp_role` (`employee_id`,`role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工-角色关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_detail`
--

DROP TABLE IF EXISTS `order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '名字',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '图片',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '口味',
  `number` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
-- ----------------------------
-- 校园食堂与取餐时段
-- ----------------------------
DROP TABLE IF EXISTS `pickup_slot`;
DROP TABLE IF EXISTS `stall`;
DROP TABLE IF EXISTS `canteen`;

CREATE TABLE `canteen` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `open_time` time DEFAULT NULL,
  `close_time` time DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `sort` int NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_canteen_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园食堂';

CREATE TABLE `stall` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `canteen_id` bigint NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `location` varchar(128) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `sort` int NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stall` (`canteen_id`,`name`),
  KEY `idx_stall_canteen` (`canteen_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食堂档口';

CREATE TABLE `pickup_slot` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stall_id` bigint NOT NULL,
  `slot_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `capacity` int NOT NULL DEFAULT '50',
  `booked` int NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '1',
  `version` int NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slot` (`stall_id`,`slot_date`,`start_time`),
  KEY `idx_slot_query` (`stall_id`,`slot_date`,`status`),
  CONSTRAINT `chk_slot_capacity` CHECK ((`booked` >= 0) and (`booked` <= `capacity`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='取餐时段库存';

CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `number` varchar(50) DEFAULT NULL COMMENT '订单号',
  `status` int NOT NULL DEFAULT '10' COMMENT '10待支付 20待接单 30制作中 40待取餐 50已取餐 60已取消 70已退款',
  `user_id` bigint NOT NULL COMMENT '下单用户',
  `canteen_id` bigint DEFAULT NULL COMMENT '食堂id',
  `stall_id` bigint DEFAULT NULL COMMENT '档口id',
  `slot_id` bigint DEFAULT NULL COMMENT '取餐时段id',
  `pickup_code` varchar(8) DEFAULT NULL COMMENT '取餐码',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `paid_at` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_method` int NOT NULL DEFAULT '1' COMMENT '支付方式 1微信 2支付宝 3模拟',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '0未支付 1已支付 2已退款',
  `amount` decimal(10,2) NOT NULL COMMENT '实收金额',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名称',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '取消原因',
  `rejection_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `ready_at` datetime DEFAULT NULL COMMENT '出餐时间',
  `picked_at` datetime DEFAULT NULL COMMENT '取餐时间',
  `slot_released` tinyint NOT NULL DEFAULT 0 COMMENT '时段库存是否已释放',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_pickup_code` (`pickup_code`),
  KEY `idx_status_order_time` (`status`,`order_time`),
  KEY `idx_user_id_order_time` (`user_id`,`order_time`),
  KEY `idx_order_stall_status` (`stall_id`,`status`,`order_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约取餐订单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payment_callback_log`
--

DROP TABLE IF EXISTS `payment_callback_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_callback_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `out_trade_no` varchar(64) NOT NULL COMMENT '商户订单号',
  `transaction_id` varchar(64) NOT NULL COMMENT '微信支付交易号',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_out_trade_transaction` (`out_trade_no`,`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付回调流水';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(128) NOT NULL COMMENT '权限点，如 dish:add',
  `name` varchar(64) NOT NULL COMMENT '权限名称',
  `type` tinyint NOT NULL DEFAULT '2' COMMENT '1菜单 2按钮/接口',
  `parent_id` bigint DEFAULT NULL COMMENT '父级，前端做树用',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_user` bigint DEFAULT NULL,
  `update_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL COMMENT '角色编码，如 ADMIN',
  `name` varchar(64) NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '说明',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_user` bigint DEFAULT NULL,
  `update_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色id',
  `permission_id` bigint NOT NULL COMMENT '权限id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-权限关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `setmeal`
--

DROP TABLE IF EXISTS `setmeal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setmeal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint NOT NULL COMMENT '菜品分类id',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '套餐名称',
  `price` decimal(10,2) NOT NULL COMMENT '套餐价格',
  `status` int DEFAULT '1' COMMENT '售卖状态 0:停售 1:起售',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '描述信息',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '图片',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_setmeal_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='套餐';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `setmeal_dish`
--

DROP TABLE IF EXISTS `setmeal_dish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setmeal_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `setmeal_id` bigint DEFAULT NULL COMMENT '套餐id',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '菜品名称 （冗余字段）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '菜品单价（冗余字段）',
  `copies` int DEFAULT NULL COMMENT '菜品份数',
  PRIMARY KEY (`id`),
  KEY `idx_setmeal_id` (`setmeal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='套餐菜品关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shopping_cart`
--

DROP TABLE IF EXISTS `shopping_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopping_cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '商品名称',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '图片',
  `user_id` bigint NOT NULL COMMENT '主键',
  `stall_id` bigint DEFAULT NULL COMMENT '所属档口',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '口味',
  `number` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_cart_stall` (`stall_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='购物车';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_no` varchar(32) DEFAULT NULL COMMENT '学号',
  `password` varchar(100) DEFAULT NULL COMMENT 'BCrypt密码',
  `openid` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '微信用户唯一标识',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '姓名',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '性别',
  `id_number` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '身份证号',
  `avatar` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='用户信息';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-23 19:47:48


-- ============================================================
-- 海韵食刻完整演示数据
-- 适用：本地演示、简历截图、接口联调
-- 说明：本脚本会清空业务演示表并重新写入，适合开发环境重复执行。
-- ============================================================
USE haiyun_meal;
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM payment_callback_log;
DELETE FROM order_detail;
DELETE FROM orders;
DELETE FROM shopping_cart;
DELETE FROM pickup_slot;
DELETE FROM setmeal_dish;
DELETE FROM setmeal;
DELETE FROM dish_flavor;
DELETE FROM dish;
DELETE FROM category;
DELETE FROM stall;
DELETE FROM canteen;
DELETE FROM employee_role;
DELETE FROM role_permission;
DELETE FROM permission;
DELETE FROM role;
DELETE FROM employee;
DELETE FROM user;
ALTER TABLE canteen AUTO_INCREMENT = 1;
ALTER TABLE stall AUTO_INCREMENT = 1;
ALTER TABLE category AUTO_INCREMENT = 1;
ALTER TABLE dish AUTO_INCREMENT = 1;
ALTER TABLE setmeal AUTO_INCREMENT = 1;
ALTER TABLE pickup_slot AUTO_INCREMENT = 1;
ALTER TABLE orders AUTO_INCREMENT = 1;
ALTER TABLE order_detail AUTO_INCREMENT = 1;
ALTER TABLE user AUTO_INCREMENT = 1;
ALTER TABLE employee AUTO_INCREMENT = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- 食堂
INSERT INTO canteen (id,name,address,open_time,close_time,status,sort) VALUES
 (1, '海韵第一食堂', '校园东区生活广场', '06:30', '20:30', 1, 1),
 (2, '南海苑风味餐厅', '校园南区宿舍旁', '07:00', '21:00', 1, 2),
 (3, '椰风苑教工餐厅', '校园中心区行政楼西侧', '07:30', '19:30', 1, 3),
 (4, '西区民族餐厅', '校园西区学生公寓旁', '07:00', '20:00', 1, 4);

-- 档口
INSERT INTO stall (id,canteen_id,name,description,location,status,sort) VALUES
 (1, 1, '米饭套餐', '两荤一素，适合正餐', '一层东侧', 1, 1),
 (2, 1, '面食档口', '现煮面食、汤粉与云吞', '一层中庭', 1, 2),
 (3, 2, '轻食饮品', '沙拉、三明治与现制饮品', '南区一层', 1, 1),
 (4, 2, '地方风味', '海南特色与家常菜', '南区二层', 1, 2),
 (5, 1, '早餐档口', '海南粉、包子和粥品', '一层北侧', 1, 3),
 (6, 4, '民族风味', '清真餐与西北风味', '西区一层', 1, 1),
 (7, 3, '教工简餐', '少油简餐与汤饮', '中心区一层', 1, 1),
 (8, 2, '甜品水果', '甜品、水果和椰饮', '南区一层', 0, 3);

-- 分类
INSERT INTO category (id,stall_id,type,name,sort,status,create_time,update_time) VALUES
 (101,1,1,'米饭套餐',1,1,NOW(),NOW()),
 (102,1,1,'例汤小食',2,1,NOW(),NOW()),
 (103,2,1,'汤面',1,1,NOW(),NOW()),
 (104,2,1,'粉类',2,1,NOW(),NOW()),
 (105,3,1,'轻食',1,1,NOW(),NOW()),
 (106,3,1,'饮品',2,1,NOW(),NOW()),
 (107,4,1,'海南风味',1,1,NOW(),NOW()),
 (108,4,1,'家常菜',2,1,NOW(),NOW()),
 (109,5,1,'早餐主食',1,1,NOW(),NOW()),
 (110,5,1,'粥品',2,1,NOW(),NOW()),
 (111,6,1,'民族风味',1,1,NOW(),NOW()),
 (112,6,1,'小炒',2,1,NOW(),NOW()),
 (113,7,1,'教工简餐',1,1,NOW(),NOW()),
 (114,7,1,'汤饮',2,1,NOW(),NOW()),
 (115,8,1,'甜品',1,1,NOW(),NOW()),
 (116,8,1,'水果',2,1,NOW(),NOW()),
 (117,1,2,'米饭套餐组合',3,1,NOW(),NOW()),
 (118,4,2,'海南风味组合',3,1,NOW(),NOW());

-- 菜品
INSERT INTO dish (id,name,category_id,price,image,description,status,create_time,update_time) VALUES
 (101,'海南鸡饭',101,16.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/3e8d0097-4a80-49ad-bab3-b04bb8400a5b.png','文昌鸡、香米饭与时蔬',1,NOW(),NOW()),
 (102,'番茄牛腩饭',101,18.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/9fa01441-3114-4102-a8e5-a56bed71a2b3.png','番茄牛腩配时蔬',1,NOW(),NOW()),
 (103,'香菇滑鸡饭',101,15.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/aa889a15-d880-4cb2-a18f-666e1ad28f3e.png','香菇滑鸡配青菜',1,NOW(),NOW()),
 (104,'咖喱鸡饭',101,17.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a4a1274f-7e93-464c-b944-cf246363beca.png','咖喱鸡块配米饭',1,NOW(),NOW()),
 (105,'紫菜蛋花汤',102,3.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/9338c8e6-b980-4dde-9b66-1884944e90f0.png','现煮例汤',1,NOW(),NOW()),
 (106,'清补凉',102,8.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a316d877-9321-4978-ba79-999fd2457ea6.png','椰奶清补凉',1,NOW(),NOW()),
 (107,'牛肉汤面',103,14.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/30e8e879-56e0-48de-abe5-536e0edc767b.png','现煮牛肉汤面',1,NOW(),NOW()),
 (108,'云吞面',103,13.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/30e8e879-56e0-48de-abe5-536e0edc767b.png','鲜肉云吞配细面',1,NOW(),NOW()),
 (109,'酸辣粉',104,11.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/1f94d0d8-2b0a-44e9-bd92-2c94cbbe45dd.png','酸辣开胃',1,NOW(),NOW()),
 (110,'牛腩粉',104,15.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/30e8e879-56e0-48de-abe5-536e0edc767b.png','牛腩与米粉',1,NOW(),NOW()),
 (111,'鸡胸肉沙拉',105,18.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/5d6e766f-f571-4bd1-a21f-0e24c7321779.png','高蛋白轻食',1,NOW(),NOW()),
 (112,'金枪鱼三明治',105,13.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/8ecb6183-71d4-4cd1-9e04-454698b2ca37.png','现做三明治',1,NOW(),NOW()),
 (113,'田园蔬菜沙拉',105,14.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/5d6e766f-f571-4bd1-a21f-0e24c7321779.png','当日蔬菜与油醋汁',1,NOW(),NOW()),
 (114,'椰子水',106,7.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/311de583-11db-4b39-943d-a719450d9b7e.png','海南椰子水',1,NOW(),NOW()),
 (115,'柠檬茶',106,9.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/311de583-11db-4b39-943d-a719450d9b7e.png','现制柠檬茶',1,NOW(),NOW()),
 (116,'豆浆',106,3.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/311de583-11db-4b39-943d-a719450d9b7e.png','无糖豆浆',1,NOW(),NOW()),
 (117,'文昌鸡',107,22.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/0afec55a-bec8-474e-8c4c-42302699795d.png','海南地方风味',1,NOW(),NOW()),
 (118,'清炒时蔬',108,12.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/0afec55a-bec8-474e-8c4c-42302699795d.png','当日时蔬',1,NOW(),NOW()),
 (119,'椰子饭',107,12.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/0afec55a-bec8-474e-8c4c-42302699795d.png','椰香糯米',1,NOW(),NOW()),
 (120,'海南粉',109,10.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/68924dd5-7a4e-4828-995d-ef0bc0350137.png','牛肉干、花生与酸菜',1,NOW(),NOW()),
 (121,'抱罗粉',109,11.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/68924dd5-7a4e-4828-995d-ef0bc0350137.png','琼北风味汤粉',1,NOW(),NOW()),
 (122,'鲜肉包',109,3.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/68924dd5-7a4e-4828-995d-ef0bc0350137.png','现蒸鲜肉包',1,NOW(),NOW()),
 (123,'煎蛋',109,2.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/68924dd5-7a4e-4828-995d-ef0bc0350137.png','现煎鸡蛋',1,NOW(),NOW()),
 (124,'南瓜粥',110,4.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/68924dd5-7a4e-4828-995d-ef0bc0350137.png','南瓜与小米',1,NOW(),NOW()),
 (125,'皮蛋瘦肉粥',110,7.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/68924dd5-7a4e-4828-995d-ef0bc0350137.png','广式粥品',1,NOW(),NOW()),
 (126,'兰州牛肉面',111,16.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/30e8e879-56e0-48de-abe5-536e0edc767b.png','清汤牛肉面',1,NOW(),NOW()),
 (127,'手抓饭',111,18.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/0afec55a-bec8-474e-8c4c-42302699795d.png','羊肉手抓饭',1,NOW(),NOW()),
 (128,'烤馕',111,5.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/68924dd5-7a4e-4828-995d-ef0bc0350137.png','现烤馕饼',1,NOW(),NOW()),
 (129,'孜然鸡丁',112,20.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a4a1274f-7e93-464c-b944-cf246363beca.png','孜然鸡丁配时蔬',1,NOW(),NOW()),
 (130,'番茄炒蛋',112,12.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a4a1274f-7e93-464c-b944-cf246363beca.png','家常番茄炒蛋',1,NOW(),NOW()),
 (131,'清蒸鱼套餐',113,24.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/0afec55a-bec8-474e-8c4c-42302699795d.png','清蒸鱼与时蔬',1,NOW(),NOW()),
 (132,'香菇鸡肉饭',113,18.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/0afec55a-bec8-474e-8c4c-42302699795d.png','少油简餐',1,NOW(),NOW()),
 (133,'菌菇汤',114,6.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/9338c8e6-b980-4dde-9b66-1884944e90f0.png','菌菇清汤',1,NOW(),NOW()),
 (134,'绿豆汤',114,4.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a316d877-9321-4978-ba79-999fd2457ea6.png','清热绿豆汤',1,NOW(),NOW()),
 (135,'椰奶冻',115,9.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a316d877-9321-4978-ba79-999fd2457ea6.png','椰奶与椰果',1,NOW(),NOW()),
 (136,'芒果班戟',115,12.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a316d877-9321-4978-ba79-999fd2457ea6.png','芒果奶油班戟',1,NOW(),NOW()),
 (137,'杨枝甘露',115,13.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a316d877-9321-4978-ba79-999fd2457ea6.png','芒果西柚甜品',1,NOW(),NOW()),
 (138,'西瓜切盒',116,8.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a316d877-9321-4978-ba79-999fd2457ea6.png','当日西瓜',1,NOW(),NOW()),
 (139,'水果拼盘',116,12.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a316d877-9321-4978-ba79-999fd2457ea6.png','三种时令水果',1,NOW(),NOW()),
 (140,'椰青',116,10.00,'https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a316d877-9321-4978-ba79-999fd2457ea6.png','整只椰青',1,NOW(),NOW());

-- 套餐
INSERT INTO setmeal (id,category_id,name,price,status,description,image,create_time,update_time) VALUES
 (301,117,'海南鸡饭套餐',24.00,1,'海南鸡饭、例汤与椰子水','https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/0afec55a-bec8-474e-8c4c-42302699795d.png',NOW(),NOW()),
 (302,117,'番茄牛腩套餐',25.00,1,'番茄牛腩饭与紫菜蛋花汤','https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/9fa01441-3114-4102-a8e5-a56bed71a2b3.png',NOW(),NOW()),
 (303,118,'文昌鸡风味套餐',32.00,1,'文昌鸡、椰子饭与时蔬','https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/0afec55a-bec8-474e-8c4c-42302699795d.png',NOW(),NOW()),
 (304,118,'清补凉下午茶',15.00,1,'清补凉与椰奶冻','https://sky-take-out-ith.oss-cn-beijing.aliyuncs.com/a316d877-9321-4978-ba79-999fd2457ea6.png',NOW(),NOW());

-- 套餐包含菜品
INSERT INTO setmeal_dish (id,setmeal_id,dish_id,name,price,copies) VALUES
 (3001,301,101,'海南鸡饭',16.00,1),
 (3002,301,105,'紫菜蛋花汤',3.00,1),
 (3003,301,114,'椰子水',7.00,1),
 (3004,302,102,'番茄牛腩饭',18.00,1),
 (3005,302,105,'紫菜蛋花汤',3.00,1),
 (3006,303,117,'文昌鸡',22.00,1),
 (3007,303,119,'椰子饭',12.00,1),
 (3008,303,118,'清炒时蔬',12.00,1),
 (3009,304,106,'清补凉',8.00,1),
 (3010,304,135,'椰奶冻',9.00,1);

-- 菜品口味
INSERT INTO dish_flavor (id,dish_id,name,value) VALUES
 (401,101,'辣度','不辣,微辣,中辣'),
 (402,101,'加料','加鸡腿,加青菜'),
 (403,107,'辣度','清汤,微辣,加辣'),
 (404,108,'份量','标准,加面'),
 (405,111,'酱汁','油醋汁,芝麻酱'),
 (406,115,'冰量','少冰,正常冰,去冰'),
 (407,126,'辣度','不辣,微辣,加辣'),
 (408,127,'份量','标准,加量');

-- 取餐时段：今天至未来两天，共 72 个时段
INSERT INTO pickup_slot (id,stall_id,slot_date,start_time,end_time,capacity,booked,status,version) VALUES
 (1001,1,CURDATE(),'11:00','11:30',50,3,1,0),
 (1002,1,CURDATE(),'11:30','12:00',50,8,1,0),
 (1003,1,CURDATE(),'17:30','18:00',50,13,1,0),
 (1004,2,CURDATE(),'11:00','11:30',50,6,1,0),
 (1005,2,CURDATE(),'11:30','12:00',50,11,1,0),
 (1006,2,CURDATE(),'17:30','18:00',50,16,1,0),
 (1007,3,CURDATE(),'11:00','11:30',40,9,1,0),
 (1008,3,CURDATE(),'11:30','12:00',40,14,1,0),
 (1009,3,CURDATE(),'17:30','18:00',40,0,1,0),
 (1010,4,CURDATE(),'11:00','11:30',30,12,1,0),
 (1011,4,CURDATE(),'11:30','12:00',30,17,1,0),
 (1012,4,CURDATE(),'17:30','18:00',30,3,1,0),
 (1013,5,CURDATE(),'11:00','11:30',40,15,1,0),
 (1014,5,CURDATE(),'11:30','12:00',40,1,1,0),
 (1015,5,CURDATE(),'17:30','18:00',40,6,1,0),
 (1016,6,CURDATE(),'11:00','11:30',40,18,1,0),
 (1017,6,CURDATE(),'11:30','12:00',40,4,1,0),
 (1018,6,CURDATE(),'17:30','18:00',40,9,1,0),
 (1019,7,CURDATE(),'11:00','11:30',40,2,1,0),
 (1020,7,CURDATE(),'11:30','12:00',40,7,1,0),
 (1021,7,CURDATE(),'17:30','18:00',40,12,1,0),
 (1022,8,CURDATE(),'11:00','11:30',30,5,0,0),
 (1023,8,CURDATE(),'11:30','12:00',30,10,0,0),
 (1024,8,CURDATE(),'17:30','18:00',30,15,0,0),
 (1025,1,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00','11:30',50,10,1,0),
 (1026,1,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:30','12:00',50,15,1,0),
 (1027,1,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'17:30','18:00',50,1,1,0),
 (1028,2,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00','11:30',50,13,1,0),
 (1029,2,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:30','12:00',50,18,1,0),
 (1030,2,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'17:30','18:00',50,4,1,0),
 (1031,3,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00','11:30',40,16,1,0),
 (1032,3,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:30','12:00',40,2,1,0),
 (1033,3,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'17:30','18:00',40,7,1,0),
 (1034,4,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00','11:30',30,0,1,0),
 (1035,4,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:30','12:00',30,5,1,0),
 (1036,4,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'17:30','18:00',30,10,1,0),
 (1037,5,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00','11:30',40,3,1,0),
 (1038,5,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:30','12:00',40,8,1,0),
 (1039,5,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'17:30','18:00',40,13,1,0),
 (1040,6,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00','11:30',40,6,1,0),
 (1041,6,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:30','12:00',40,11,1,0),
 (1042,6,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'17:30','18:00',40,16,1,0),
 (1043,7,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00','11:30',40,9,1,0),
 (1044,7,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:30','12:00',40,14,1,0),
 (1045,7,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'17:30','18:00',40,0,1,0),
 (1046,8,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00','11:30',30,12,0,0),
 (1047,8,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:30','12:00',30,17,0,0),
 (1048,8,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'17:30','18:00',30,3,0,0),
 (1049,1,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:00','11:30',50,17,1,0),
 (1050,1,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:30','12:00',50,3,1,0),
 (1051,1,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'17:30','18:00',50,8,1,0),
 (1052,2,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:00','11:30',50,1,1,0),
 (1053,2,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:30','12:00',50,6,1,0),
 (1054,2,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'17:30','18:00',50,11,1,0),
 (1055,3,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:00','11:30',40,4,1,0),
 (1056,3,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:30','12:00',40,9,1,0),
 (1057,3,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'17:30','18:00',40,14,1,0),
 (1058,4,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:00','11:30',30,7,1,0),
 (1059,4,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:30','12:00',30,12,1,0),
 (1060,4,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'17:30','18:00',30,17,1,0),
 (1061,5,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:00','11:30',40,10,1,0),
 (1062,5,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:30','12:00',40,15,1,0),
 (1063,5,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'17:30','18:00',40,1,1,0),
 (1064,6,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:00','11:30',40,13,1,0),
 (1065,6,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:30','12:00',40,18,1,0),
 (1066,6,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'17:30','18:00',40,4,1,0),
 (1067,7,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:00','11:30',40,16,1,0),
 (1068,7,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:30','12:00',40,2,1,0),
 (1069,7,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'17:30','18:00',40,7,1,0),
 (1070,8,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:00','11:30',30,0,0,0),
 (1071,8,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'11:30','12:00',30,5,0,0),
 (1072,8,DATE_ADD(CURDATE(), INTERVAL 2 DAY),'17:30','18:00',30,10,0,0);

-- 学生
INSERT INTO user (id,student_no,password,openid,name,phone,sex,id_number,avatar,create_time) VALUES
 (1,'20230001','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO',NULL,'林海',NULL,NULL,NULL,NULL,DATE_SUB(NOW(), INTERVAL 1 DAY)),
 (2,'20230002','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO',NULL,'陈雨',NULL,NULL,NULL,NULL,DATE_SUB(NOW(), INTERVAL 2 DAY)),
 (3,'20230003','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO',NULL,'王宁',NULL,NULL,NULL,NULL,DATE_SUB(NOW(), INTERVAL 3 DAY)),
 (4,'20230004','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO',NULL,'李悦',NULL,NULL,NULL,NULL,DATE_SUB(NOW(), INTERVAL 5 DAY)),
 (5,'20230005','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO',NULL,'许晨',NULL,NULL,NULL,NULL,DATE_SUB(NOW(), INTERVAL 7 DAY));

-- 员工
INSERT INTO employee (id,name,username,password,phone,sex,id_number,status,create_time,update_time,create_user,update_user) VALUES
 (1,'管理员','admin','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO','13812312312','1','110101199001010047',1,DATE_SUB(NOW(), INTERVAL 1 DAY),NOW(),1,1),
 (3,'李四','lisi','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO','15012378952','0','440102566253154879',1,DATE_SUB(NOW(), INTERVAL 3 DAY),NOW(),1,1),
 (4,'陈伟','chenwei','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO','13800000021','1','110101199203121234',1,DATE_SUB(NOW(), INTERVAL 4 DAY),NOW(),1,1),
 (5,'赵敏','zhaomin','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO','13800000022','0','110101199405181236',1,DATE_SUB(NOW(), INTERVAL 5 DAY),NOW(),1,1),
 (6,'王芳','wangfang','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO','13800000023','0','110101199106231248',1,DATE_SUB(NOW(), INTERVAL 6 DAY),NOW(),1,1),
 (7,'周宁','zhouning','$2a$10$oO2gxAUXIgu35MQMZbmpme8RooPKchfaSDiwjDTnOy57MjW7pVyJO','13800000024','1','110101199307091235',1,DATE_SUB(NOW(), INTERVAL 7 DAY),NOW(),1,1);

-- 角色
INSERT INTO role (id,code,name,description) VALUES
 (1, 'ADMIN', '超级管理员', '拥有全部权限'),
 (2, 'OPERATOR', '运营', '食堂、档口、菜品、时段与订单维护'),
 (3, 'FINANCE', '财务', '订单统计、报表与导出'),
 (4, 'KITCHEN', '档口人员', '接单、出餐与取餐核销'),
 (5, 'AUDITOR', '审计', '只读查看订单、员工与角色');

-- 权限点
INSERT INTO permission (code,name) VALUES
 ('canteen:query','食堂查询'),
 ('canteen:add','食堂新增'),
 ('canteen:update','食堂修改'),
 ('stall:query','档口查询'),
 ('stall:add','档口新增'),
 ('stall:update','档口修改'),
 ('slot:query','取餐时段查询'),
 ('slot:add','取餐时段生成'),
 ('slot:update','取餐时段修改'),
 ('dish:query','菜品查询'),
 ('dish:add','菜品新增'),
 ('dish:update','菜品修改'),
 ('dish:delete','菜品删除'),
 ('dish:status','菜品上下架'),
 ('setmeal:query','套餐查询'),
 ('setmeal:add','套餐新增'),
 ('setmeal:update','套餐修改'),
 ('setmeal:delete','套餐删除'),
 ('setmeal:status','套餐上下架'),
 ('category:query','分类查询'),
 ('category:add','分类新增'),
 ('category:update','分类修改'),
 ('category:delete','分类删除'),
 ('category:status','分类启禁用'),
 ('order:query','订单查询'),
 ('order:handle','订单处理'),
 ('order:cancel','订单取消'),
 ('order:statistics','订单统计'),
 ('employee:query','员工查询'),
 ('employee:add','员工新增'),
 ('employee:update','员工修改'),
 ('employee:status','员工启用禁用'),
 ('role:query','角色查询'),
 ('role:assign','角色权限分配'),
 ('report:query','报表查询'),
 ('report:export','报表导出'),
 ('shop:status','营业状态调整');

-- 员工角色
INSERT INTO employee_role (employee_id,role_id) VALUES (1,1),(3,2),(4,2),(5,4),(6,3),(7,5);

-- 超级管理员拥有全部权限
INSERT INTO role_permission (role_id,permission_id)
SELECT 1,id FROM permission;

-- 运营
INSERT INTO role_permission (role_id,permission_id)
SELECT 2,id FROM permission WHERE code LIKE 'canteen:%' OR code LIKE 'stall:%' OR code LIKE 'slot:%'
 OR code LIKE 'dish:%' OR code LIKE 'category:%' OR code LIKE 'setmeal:%'
 OR code IN ('order:query','order:handle','order:cancel','shop:status');

-- 财务
INSERT INTO role_permission (role_id,permission_id)
SELECT 3,id FROM permission WHERE code IN ('order:query','order:statistics','report:query','report:export');

-- 档口人员
INSERT INTO role_permission (role_id,permission_id)
SELECT 4,id FROM permission WHERE code IN ('order:query','order:handle','dish:query','slot:query');

-- 审计
INSERT INTO role_permission (role_id,permission_id)
SELECT 5,id FROM permission WHERE code IN ('order:query','report:query','employee:query','role:query');
-- 订单：覆盖待支付、待接单、制作中、待取餐、已取餐、已取消、已退款，以及近 7 天报表数据
INSERT INTO orders (id,number,status,user_id,canteen_id,stall_id,slot_id,pickup_code,order_time,paid_at,pay_method,pay_status,amount,remark,user_name,cancel_reason,rejection_reason,cancel_time,ready_at,picked_at,slot_released) VALUES
 (2001, 'HYDEMO0001', 20, 2, 1, 1, 1001, '410001', DATE_SUB(NOW(), INTERVAL 28 MINUTE), DATE_ADD(NOW(), INTERVAL -30 MINUTE), 3, 1, 19.00, '少辣', '陈雨', NULL, NULL, NULL, NULL, NULL, 0),
 (2002, 'HYDEMO0002', 20, 3, 1, 2, 1004, '410002', DATE_SUB(NOW(), INTERVAL 34 MINUTE), DATE_ADD(NOW(), INTERVAL -36 MINUTE), 3, 1, 14.00, '', '王宁', NULL, NULL, NULL, NULL, NULL, 0),
 (2003, 'HYDEMO0003', 30, 1, 1, 1, 1002, '410003', DATE_SUB(NOW(), INTERVAL 52 MINUTE), DATE_ADD(NOW(), INTERVAL -54 MINUTE), 3, 1, 26.00, '不要香菜', '林海', NULL, NULL, NULL, NULL, NULL, 0),
 (2004, 'HYDEMO0004', 30, 4, 2, 3, 1007, '410004', DATE_SUB(NOW(), INTERVAL 46 MINUTE), DATE_ADD(NOW(), INTERVAL -48 MINUTE), 3, 1, 25.00, '', '李悦', NULL, NULL, NULL, NULL, NULL, 0),
 (2005, 'HYDEMO0005', 40, 2, 1, 2, 1005, '410005', DATE_SUB(NOW(), INTERVAL 74 MINUTE), DATE_ADD(NOW(), INTERVAL -76 MINUTE), 3, 1, 15.00, '打包', '陈雨', NULL, NULL, NULL, DATE_ADD(NOW(), INTERVAL -69 MINUTE), NULL, 0),
 (2006, 'HYDEMO0006', 40, 3, 2, 3, 1008, '410006', DATE_SUB(NOW(), INTERVAL 68 MINUTE), DATE_ADD(NOW(), INTERVAL -70 MINUTE), 3, 1, 13.00, '', '王宁', NULL, NULL, NULL, DATE_ADD(NOW(), INTERVAL -63 MINUTE), NULL, 0),
 (2007, 'HYDEMO0007', 10, 4, 2, 4, 1010, '410007', DATE_SUB(NOW(), INTERVAL 4 MINUTE), NULL, 3, 0, 22.00, '待支付测试', '李悦', NULL, NULL, NULL, NULL, NULL, 0),
 (2008, 'HYDEMO0008', 60, 1, 1, 1, 1003, '410008', DATE_SUB(NOW(), INTERVAL 95 MINUTE), NULL, 3, 0, 17.00, '', '林海', '用户取消', NULL, NOW(), NULL, NULL, 1),
 (2009, 'HYDEMO0009', 70, 2, 1, 2, 1006, '410009', DATE_SUB(NOW(), INTERVAL 110 MINUTE), DATE_ADD(NOW(), INTERVAL -112 MINUTE), 3, 2, 13.00, '', '陈雨', '档口临时缺货', NULL, NOW(), DATE_ADD(NOW(), INTERVAL -105 MINUTE), NULL, 1),
 (2010, 'HYDEMO0010', 50, 1, 1, 1, 1001, '410010', DATE_SUB(NOW(), INTERVAL 160 MINUTE), DATE_ADD(NOW(), INTERVAL -162 MINUTE), 3, 1, 19.00, '', '林海', NULL, NULL, NULL, DATE_ADD(NOW(), INTERVAL -155 MINUTE), DATE_ADD(NOW(), INTERVAL -150 MINUTE), 0),
 (2011, 'HYDEMO0011', 50, 2, 1, 2, 1004, '410011', DATE_SUB(NOW(), INTERVAL 180 MINUTE), DATE_ADD(NOW(), INTERVAL -182 MINUTE), 3, 1, 14.00, '', '陈雨', NULL, NULL, NULL, DATE_ADD(NOW(), INTERVAL -175 MINUTE), DATE_ADD(NOW(), INTERVAL -170 MINUTE), 0),
 (2012, 'HYDEMO0012', 50, 3, 2, 3, 1007, '410012', DATE_SUB(NOW(), INTERVAL 210 MINUTE), DATE_ADD(NOW(), INTERVAL -212 MINUTE), 3, 1, 25.00, '', '王宁', NULL, NULL, NULL, DATE_ADD(NOW(), INTERVAL -205 MINUTE), DATE_ADD(NOW(), INTERVAL -200 MINUTE), 0),
 (2013, 'HYDEMO0013', 50, 1, 1, 1, 1025, '410013', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 2 MINUTE), 3, 1, 18.00, '', '林海', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 20 MINUTE), 0),
 (2014, 'HYDEMO0014', 50, 2, 1, 2, 1028, '410014', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 2 MINUTE), 3, 1, 22.00, '', '陈雨', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 20 MINUTE), 0),
 (2015, 'HYDEMO0015', 50, 3, 2, 3, 1055, '410015', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 2 MINUTE), 3, 1, 18.00, '', '王宁', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 20 MINUTE), 0),
 (2016, 'HYDEMO0016', 50, 4, 2, 4, 1058, '410016', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 2 MINUTE), 3, 1, 34.00, '', '李悦', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 20 MINUTE), 0),
 (2017, 'HYDEMO0017', 50, 1, 1, 1, 1073, '410017', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 2 MINUTE), 3, 1, 16.00, '', '林海', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 20 MINUTE), 0),
 (2018, 'HYDEMO0018', 50, 2, 1, 2, 1076, '410018', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 2 MINUTE), 3, 1, 13.00, '', '陈雨', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 20 MINUTE), 0),
 (2019, 'HYDEMO0019', 50, 3, 1, 5, 1109, '410019', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 4 DAY), INTERVAL 2 MINUTE), 3, 1, 14.00, '', '王宁', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 4 DAY), INTERVAL 20 MINUTE), 0),
 (2020, 'HYDEMO0020', 50, 4, 4, 6, 1112, '410020', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 4 DAY), INTERVAL 2 MINUTE), 3, 1, 16.00, '', '李悦', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 4 DAY), INTERVAL 20 MINUTE), 0),
 (2021, 'HYDEMO0021', 50, 1, 2, 3, 1127, '410021', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 DAY), INTERVAL 2 MINUTE), 3, 1, 13.00, '', '林海', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 DAY), INTERVAL 20 MINUTE), 0),
 (2022, 'HYDEMO0022', 50, 2, 2, 4, 1130, '410022', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 DAY), INTERVAL 2 MINUTE), 3, 1, 22.00, '', '陈雨', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 5 DAY), INTERVAL 20 MINUTE), 0),
 (2023, 'HYDEMO0023', 50, 3, 1, 1, 1145, '410023', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 6 DAY), INTERVAL 2 MINUTE), 3, 1, 16.00, '', '王宁', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 6 DAY), INTERVAL 20 MINUTE), 0),
 (2024, 'HYDEMO0024', 50, 4, 1, 2, 1148, '410024', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 6 DAY), INTERVAL 2 MINUTE), 3, 1, 14.00, '', '李悦', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 6 DAY), INTERVAL 20 MINUTE), 0),
 (2025, 'HYDEMO0025', 50, 1, 1, 5, 1181, '410025', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 7 DAY), INTERVAL 2 MINUTE), 3, 1, 18.00, '', '林海', NULL, NULL, NULL, NULL, DATE_ADD(DATE_SUB(NOW(), INTERVAL 7 DAY), INTERVAL 20 MINUTE), 0),
 (2026, 'HYDEMO0026', 70, 2, 2, 4, 1058, '410026', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 2 MINUTE), 3, 2, 22.00, '', '陈雨', '档口缺货退款', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, NULL, 1);

-- 订单明细
INSERT INTO order_detail (id,name,image,order_id,dish_id,setmeal_id,dish_flavor,number,amount) VALUES
 (3001, '海南鸡饭', NULL, 2001, 101, NULL, NULL, 1, 16.00),
 (3002, '紫菜蛋花汤', NULL, 2001, 105, NULL, NULL, 1, 3.00),
 (3003, '牛肉汤面', NULL, 2002, 107, NULL, NULL, 1, 14.00),
 (3004, '番茄牛腩饭', NULL, 2003, 102, NULL, NULL, 1, 18.00),
 (3005, '清补凉', NULL, 2003, 106, NULL, NULL, 1, 8.00),
 (3006, '鸡胸肉沙拉', NULL, 2004, 111, NULL, NULL, 1, 18.00),
 (3007, '椰子水', NULL, 2004, 114, NULL, NULL, 1, 7.00),
 (3008, '牛腩粉', NULL, 2005, 110, NULL, NULL, 1, 15.00),
 (3009, '金枪鱼三明治', NULL, 2006, 112, NULL, NULL, 1, 13.00),
 (3010, '文昌鸡', NULL, 2007, 117, NULL, NULL, 1, 22.00),
 (3011, '咖喱鸡饭', NULL, 2008, 104, NULL, NULL, 1, 17.00),
 (3012, '云吞面', NULL, 2009, 108, NULL, NULL, 1, 13.00),
 (3013, '海南鸡饭', NULL, 2010, 101, NULL, NULL, 1, 16.00),
 (3014, '紫菜蛋花汤', NULL, 2010, 105, NULL, NULL, 1, 3.00),
 (3015, '牛肉汤面', NULL, 2011, 107, NULL, NULL, 1, 14.00),
 (3016, '鸡胸肉沙拉', NULL, 2012, 111, NULL, NULL, 1, 18.00),
 (3017, '椰子水', NULL, 2012, 114, NULL, NULL, 1, 7.00),
 (3018, '番茄牛腩饭', NULL, 2013, 102, NULL, NULL, 1, 18.00),
 (3019, '牛肉汤面', NULL, 2014, 107, NULL, NULL, 1, 14.00),
 (3020, '清补凉', NULL, 2014, 106, NULL, NULL, 1, 8.00),
 (3021, '鸡胸肉沙拉', NULL, 2015, 111, NULL, NULL, 1, 18.00),
 (3022, '文昌鸡', NULL, 2016, 117, NULL, NULL, 1, 22.00),
 (3023, '椰子饭', NULL, 2016, 119, NULL, NULL, 1, 12.00),
 (3024, '海南鸡饭', NULL, 2017, 101, NULL, NULL, 1, 16.00),
 (3025, '云吞面', NULL, 2018, 108, NULL, NULL, 1, 13.00),
 (3026, '海南粉', NULL, 2019, 120, NULL, NULL, 1, 10.00),
 (3027, '南瓜粥', NULL, 2019, 124, NULL, NULL, 1, 4.00),
 (3028, '兰州牛肉面', NULL, 2020, 126, NULL, NULL, 1, 16.00),
 (3029, '金枪鱼三明治', NULL, 2021, 112, NULL, NULL, 1, 13.00),
 (3030, '文昌鸡', NULL, 2022, 117, NULL, NULL, 1, 22.00),
 (3031, '海南鸡饭', NULL, 2023, 101, NULL, NULL, 1, 16.00),
 (3032, '牛肉汤面', NULL, 2024, 107, NULL, NULL, 1, 14.00),
 (3033, '抱罗粉', NULL, 2025, 121, NULL, NULL, 1, 11.00),
 (3034, '皮蛋瘦肉粥', NULL, 2025, 125, NULL, NULL, 1, 7.00),
 (3035, '文昌鸡', NULL, 2026, 117, NULL, NULL, 1, 22.00);

-- 数据摘要
SELECT (SELECT COUNT(*) FROM canteen) AS canteens, (SELECT COUNT(*) FROM stall) AS stalls, (SELECT COUNT(*) FROM dish) AS dishes, (SELECT COUNT(*) FROM pickup_slot) AS slots, (SELECT COUNT(*) FROM orders) AS orders, (SELECT COUNT(*) FROM employee) AS employees;
