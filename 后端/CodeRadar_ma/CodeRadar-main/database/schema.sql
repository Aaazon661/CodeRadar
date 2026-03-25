-- CodeRadar 数据库表结构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS CodeRadar DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE CodeRadar;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    account VARCHAR(50) NOT NULL UNIQUE COMMENT '账号',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值（加密存储）',
    salt VARCHAR(64) COMMENT '密码盐值',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at DATETIME COMMENT '最后登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 用户文件表
CREATE TABLE IF NOT EXISTS user_file (
    file_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    original_file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_file_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    storage_path VARCHAR(500) NOT NULL COMMENT '存储路径',
    file_content LONGTEXT COMMENT '文件内容',
    file_type VARCHAR(20) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小（字节）',
    file_hash VARCHAR(64) COMMENT '文件哈希值',
    encoding VARCHAR(20) DEFAULT 'UTF-8' COMMENT '文件编码',
    newline VARCHAR(10) COMMENT '换行符类型',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_file_hash (file_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户文件表';

-- 3. 审查结果表
CREATE TABLE IF NOT EXISTS review_result (
    result_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结果ID',
    request_id VARCHAR(100) NOT NULL UNIQUE COMMENT 'AI请求ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    model VARCHAR(50) COMMENT 'AI模型名称',
    summary TEXT COMMENT '审查总结',
    review_time DATETIME COMMENT '审查时间',
    raw_json LONGTEXT COMMENT '原始JSON响应',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_request_id (request_id),
    INDEX idx_user_id (user_id),
    INDEX idx_file_id (file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审查结果表';

-- 4. 建议表
CREATE TABLE IF NOT EXISTS suggestion (
    suggestion_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '建议ID',
    result_id BIGINT NOT NULL COMMENT '结果ID',
    request_id VARCHAR(100) NOT NULL COMMENT '请求ID（冗余字段）',
    category VARCHAR(50) COMMENT '建议类别',
    line_numbers VARCHAR(50) COMMENT '行号范围',
    suggestion TEXT COMMENT '建议内容',
    severity VARCHAR(20) COMMENT '严重程度',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_result_id (result_id),
    INDEX idx_request_id (request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='建议表';

-- 插入测试用户
INSERT INTO user (username, password_hash, email) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@coderadar.com'),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test@coderadar.com');
