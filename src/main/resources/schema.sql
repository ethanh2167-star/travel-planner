-- =============================================
--  旅遊規劃網站 資料庫初始化腳本
--  執行環境：MariaDB 10.x+
-- =============================================

CREATE DATABASE IF NOT EXISTS travel_planner
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE travel_planner;

-- 會員資料表
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,          -- BCrypt hash
    full_name   VARCHAR(100),
    role        VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER',
    enabled     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 行程主表
CREATE TABLE IF NOT EXISTS trips (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL,
    title        VARCHAR(200) NOT NULL,
    destination  VARCHAR(200) NOT NULL,
    start_date   DATE         NOT NULL,
    end_date     DATE         NOT NULL,
    description  TEXT,
    budget       DECIMAL(12,2),
    status       VARCHAR(20)  NOT NULL DEFAULT 'PLANNING',  -- PLANNING / ONGOING / COMPLETED
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 行程景點 / 活動明細
CREATE TABLE IF NOT EXISTS trip_items (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    trip_id     BIGINT       NOT NULL,
    day_number  INT          NOT NULL DEFAULT 1,   -- 第幾天
    item_time   TIME,
    place_name  VARCHAR(200) NOT NULL,
    category    VARCHAR(50),                       -- 景點/餐廳/住宿/交通
    note        TEXT,
    cost        DECIMAL(10,2),
    sort_order  INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 預設管理員帳號 (密碼: admin123, BCrypt)
INSERT IGNORE INTO users (username, email, password, full_name, role)
VALUES (
    'admin',
    'admin@travel.com',
    '$2a$12$K8HbhAHNhPIPXoNAqPLvjutx1r7wLt3J2EvVxcVpZGMZRiQsINVXe',
    '系統管理員',
    'ROLE_ADMIN'
);
