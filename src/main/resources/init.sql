-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_management;

-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  nickname VARCHAR(50) COMMENT '昵称',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  avatar VARCHAR(255) COMMENT '头像',
  status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
  is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0否，1是',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL COMMENT '角色名称',
  description VARCHAR(200) COMMENT '角色描述',
  status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
  is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0否，1是',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL COMMENT '资源名称',
  `key` VARCHAR(100) NOT NULL COMMENT '资源KEY',
  type TINYINT NOT NULL COMMENT '资源类型：1菜单，2按钮',
  path VARCHAR(255) COMMENT '资源URI',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
  is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0否，1是',
  icon VARCHAR(50) COMMENT '图标',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_user_role (user_id, role_id),
  KEY idx_user_id (user_id),
  KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_role_permission (role_id, permission_id),
  KEY idx_role_id (role_id),
  KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 创建书籍表
CREATE TABLE IF NOT EXISTS book (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL COMMENT '书名',
  author VARCHAR(100) NOT NULL COMMENT '作者',
  isbn VARCHAR(20) COMMENT 'ISBN号',
  publisher VARCHAR(100) COMMENT '出版社',
  publish_date DATE COMMENT '出版日期',
  category VARCHAR(50) COMMENT '分类',
  total_count INT DEFAULT 0 COMMENT '总数量',
  available_count INT DEFAULT 0 COMMENT '可借数量',
  description TEXT COMMENT '描述',
  cover VARCHAR(255) COMMENT '封面图片',
  status TINYINT DEFAULT 1 COMMENT '状态：0下架，1上架',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍表';

-- 创建借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  book_id BIGINT NOT NULL COMMENT '书籍ID',
  borrow_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
  return_time DATETIME COMMENT '归还时间',
  expected_return_time DATETIME NOT NULL COMMENT '预计归还时间',
  status TINYINT DEFAULT 1 COMMENT '状态：1借阅中，2已归还，3逾期',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_user_id (user_id),
  KEY idx_book_id (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 插入初始数据
-- 插入管理员用户（密码：admin123）
INSERT INTO sys_user (username, password, nickname, email, phone, status) 
VALUES ('admin', '$2a$10$6Qx5fMkXQe5x8Kj5lM7eCOoUeF9fQ7y1QoZ9wJ5y1QoZ9wJ5y1QoZ9wJ5y1Qo', '系统管理员', 'admin@example.com', '13800138000', 1);

-- 插入初始权限数据
INSERT INTO sys_permission (name, `key`, type, path, sort, status, icon) VALUES
('用户管理', 'system:user:index', 1, '/admin/user/index', 1, 1, 'el-icon-user'),
('用户编辑', 'system:user:edit', 2, '', 2, 1, ''),
('用户删除', 'system:user:delete', 2, '', 3, 1, ''),
('用户添加', 'system:user:add', 2, '', 4, 1, ''),
('角色分配', 'system:user:grant', 2, '', 5, 1, '');