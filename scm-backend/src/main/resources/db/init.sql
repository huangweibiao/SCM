-- SCM Database Initialization Script
-- Create database
CREATE DATABASE IF NOT EXISTS scm_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE scm_db;

-- User Table (for OAuth2 SSO mapping)
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE COMMENT 'Username',
    password VARCHAR(128) COMMENT 'Password (optional for OAuth2 users)',
    nickname VARCHAR(64) COMMENT 'Nickname',
    email VARCHAR(128) COMMENT 'Email',
    phone VARCHAR(20) COMMENT 'Phone number',
    avatar VARCHAR(512) COMMENT 'Avatar URL',
    status INT DEFAULT 1 COMMENT 'Status: 1-Active, 0-Inactive',
    oauth2_id VARCHAR(128) COMMENT 'OAuth2 Provider User ID',
    oauth2_provider VARCHAR(32) COMMENT 'OAuth2 Provider name',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    is_deleted TINYINT(1) DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_oauth2 (oauth2_id, oauth2_provider)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System User Table';

-- Supplier Table
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE COMMENT 'Supplier Code',
    name VARCHAR(128) NOT NULL COMMENT 'Supplier Name',
    contact VARCHAR(64) COMMENT 'Contact Person',
    phone VARCHAR(20) COMMENT 'Contact Phone',
    email VARCHAR(128) COMMENT 'Email',
    address VARCHAR(512) COMMENT 'Address',
    status INT DEFAULT 1 COMMENT 'Status: 1-Active, 0-Inactive',
    credit_level INT DEFAULT 0 COMMENT 'Credit Level: 0-5',
    remark TEXT COMMENT 'Remarks',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    is_deleted TINYINT(1) DEFAULT 0,
    INDEX idx_code (code),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Supplier Table';

-- Purchase Order Table
CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT 'Order Number',
    supplier_id BIGINT NOT NULL COMMENT 'Supplier ID',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT 'Total Amount',
    status VARCHAR(32) DEFAULT 'DRAFT' COMMENT 'Status: DRAFT, PENDING, APPROVED, COMPLETED, CANCELLED',
    order_date DATE COMMENT 'Order Date',
    expected_date DATE COMMENT 'Expected Delivery Date',
    remark TEXT COMMENT 'Remarks',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    is_deleted TINYINT(1) DEFAULT 0,
    INDEX idx_order_no (order_no),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Purchase Order Table';

-- Inventory Table
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(32) NOT NULL UNIQUE COMMENT 'SKU Code',
    name VARCHAR(128) NOT NULL COMMENT 'Product Name',
    category VARCHAR(64) COMMENT 'Category',
    quantity INT DEFAULT 0 COMMENT 'Current Quantity',
    unit VARCHAR(16) COMMENT 'Unit',
    warehouse VARCHAR(64) COMMENT 'Warehouse',
    min_quantity INT DEFAULT 0 COMMENT 'Minimum Quantity Alert',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    is_deleted TINYINT(1) DEFAULT 0,
    INDEX idx_sku (sku),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Inventory Table';

-- Spring Session Table (for OAuth2 session persistence)
CREATE TABLE IF NOT EXISTS SPRING_SESSION (
    PRIMARY_ID CHAR(36) NOT NULL,
    SESSION_ID CHAR(36) NOT NULL,
    CREATION_TIME BIGINT NOT NULL,
    LAST_ACCESS_TIME BIGINT NOT NULL,
    MAX_INACTIVE_INTERVAL INT NOT NULL,
    EXPIRY_TIME BIGINT NOT NULL,
    PRINCIPAL_NAME VARCHAR(100),
    CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE IF NOT EXISTS SPRING_SESSION_ATTRIBUTES (
    SESSION_PRIMARY_ID CHAR(36) NOT NULL,
    ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES BLOB NOT NULL,
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default admin user (for testing)
-- Password is bcrypt encoded 'admin123' - only used if not using OAuth2
INSERT INTO sys_user (username, password, nickname, email, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'System Admin', 'admin@scm.com', 1)
ON DUPLICATE KEY UPDATE username = username;
