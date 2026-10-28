-- ============================================================
-- V7：校园食堂预约取餐模型
-- 在已经导入了基础表结构的 haiyun_meal 库上执行。
-- ============================================================

USE haiyun_meal;

CREATE TABLE IF NOT EXISTS canteen (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  address VARCHAR(255),
  open_time TIME,
  close_time TIME,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  sort INT NOT NULL DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_canteen_name (name)
) COMMENT '校园食堂';

CREATE TABLE IF NOT EXISTS stall (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  canteen_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  description VARCHAR(255),
  location VARCHAR(128),
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  sort INT NOT NULL DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_stall (canteen_id, name),
  KEY idx_stall_canteen (canteen_id, status)
) COMMENT '食堂档口';

CREATE TABLE IF NOT EXISTS pickup_slot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  stall_id BIGINT NOT NULL,
  slot_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  capacity INT NOT NULL DEFAULT 50,
  booked INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  version INT NOT NULL DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_slot (stall_id, slot_date, start_time),
  KEY idx_slot_query (stall_id, slot_date, status),
  CONSTRAINT chk_slot_capacity CHECK (booked >= 0 AND booked <= capacity)
) COMMENT '取餐时段库存';

ALTER TABLE category ADD COLUMN stall_id BIGINT NULL COMMENT '所属档口' AFTER id;
ALTER TABLE category ADD KEY idx_category_stall (stall_id, status);

ALTER TABLE shopping_cart ADD COLUMN stall_id BIGINT NULL COMMENT '购物车所属档口' AFTER user_id;
ALTER TABLE shopping_cart ADD KEY idx_cart_stall (stall_id);

ALTER TABLE user ADD COLUMN student_no VARCHAR(32) NULL COMMENT '学号' AFTER id;
ALTER TABLE user ADD COLUMN password VARCHAR(100) NULL COMMENT 'BCrypt密码' AFTER student_no;
ALTER TABLE user ADD UNIQUE KEY uk_student_no (student_no);

ALTER TABLE orders
  ADD COLUMN canteen_id BIGINT NULL AFTER user_id,
  ADD COLUMN stall_id BIGINT NULL AFTER canteen_id,
  ADD COLUMN slot_id BIGINT NULL AFTER stall_id,
  ADD COLUMN pickup_code VARCHAR(8) NULL AFTER slot_id,
  ADD COLUMN paid_at DATETIME NULL AFTER order_time,
  ADD COLUMN ready_at DATETIME NULL AFTER cancel_time,
  ADD COLUMN picked_at DATETIME NULL AFTER ready_at,
  ADD COLUMN slot_released TINYINT NOT NULL DEFAULT 0 AFTER picked_at,
  MODIFY COLUMN address_book_id BIGINT NULL COMMENT '历史字段，仅用于兼容旧数据',
  ADD UNIQUE KEY uk_order_pickup_code (pickup_code),
  ADD KEY idx_order_stall_status (stall_id, status, order_time);

INSERT IGNORE INTO permission (code, name) VALUES
  ('canteen:query', '食堂查询'),
  ('canteen:add', '食堂新增'),
  ('canteen:update', '食堂修改'),
  ('stall:query', '档口查询'),
  ('stall:add', '档口新增'),
  ('stall:update', '档口修改'),
  ('slot:query', '取餐时段查询'),
  ('slot:add', '取餐时段生成'),
  ('slot:update', '取餐时段修改');

INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission
WHERE code IN ('canteen:query','canteen:add','canteen:update','stall:query','stall:add','stall:update','slot:query','slot:add','slot:update');

INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT 2, id FROM permission
WHERE code IN ('canteen:query','stall:query','stall:add','stall:update','slot:query','slot:add','slot:update');
