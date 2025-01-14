-- Department 테이블 생성
CREATE TABLE DEPARTMENT (
    dept_id VARCHAR(50) PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL
);

-- User 테이블 생성
CREATE TABLE USER (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(50) NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    pwd VARCHAR(255) NOT NULL,
    dept_id VARCHAR(50),
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES DEPARTMENT(dept_id),
    UNIQUE (employee_id, email)
);

-- Board 테이블 생성     *******************************************************
CREATE TABLE BOARD (
    board_id BIGINT PRIMARY KEY,
    board_title VARCHAR(100) NOT NULL,
    description TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP
);

-- Post 테이블 생성
CREATE TABLE POST (
    post_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    board_id BIGINT,
    post_title VARCHAR(200) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP,
    view_count BIGINT DEFAULT 0,
    FOREIGN KEY (board_id) REFERENCES BOARD(board_id)
);

-- Audit_Log 테이블 생성
CREATE TABLE AUDIT_LOG (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    dept_id VARCHAR(50),
    action_type VARCHAR(50) NOT NULL,
    target_id BIGINT,
    action_metadata JSON,
    action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(user_id),
    FOREIGN KEY (dept_id) REFERENCES DEPARTMENT(dept_id)
);

-- Model_Version 테이블 생성
CREATE TABLE MODEL_VERSION (
    model_id BIGINT PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    model_type VARCHAR(50),
    version VARCHAR(20),
    model_metadata JSON,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Board_Department 테이블 생성 (다대다 관계)
CREATE TABLE BOARD_DEPARTMENT (
    board_dept_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    board_id BIGINT,
    dept_id VARCHAR(50),
    FOREIGN KEY (board_id) REFERENCES BOARD(board_id),
    FOREIGN KEY (dept_id) REFERENCES DEPARTMENT(dept_id)
);

-- Content_Analysis 테이블 생성
CREATE TABLE CONTENT_ANALYSIS (
    analysis_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT,
    content_type VARCHAR(50),
    analysis_detail TEXT,
    analyzed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    FOREIGN KEY (post_id) REFERENCES POST(post_id)
);

-- Hate_Category 테이블 생성
CREATE TABLE HATE_CATEGORY (
    category_id BIGINT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    severity_level BIGINT,
    UNIQUE (category_name)
);

-- Analysis_Category_Result 테이블 생성
CREATE TABLE ANALYSIS_CATEGORY_RESULT (
    result_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    analysis_id BIGINT,
    category_id BIGINT,
    category_score FLOAT,
    detection_metadata JSON,
    FOREIGN KEY (analysis_id) REFERENCES CONTENT_ANALYSIS(analysis_id),
    FOREIGN KEY (category_id) REFERENCES HATE_CATEGORY(category_id)
);

-- 인덱스 생성
CREATE INDEX idx_user_email ON USER(email);
CREATE INDEX idx_user_dept ON USER(dept_id);
CREATE INDEX idx_post_board ON POST(board_id);
CREATE INDEX idx_audit_user ON AUDIT_LOG(user_id);
CREATE INDEX idx_audit_dept ON AUDIT_LOG(dept_id);
CREATE INDEX idx_content_analysis_post ON CONTENT_ANALYSIS(post_id);
CREATE INDEX idx_analysis_result_analysis ON ANALYSIS_CATEGORY_RESULT(analysis_id);
CREATE INDEX idx_analysis_result_category ON ANALYSIS_CATEGORY_RESULT(category_id);










-- 미완성 테이블 --


-- Role 테이블 생성*******************************************************
CREATE TABLE ROLE (
    role_id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    UNIQUE (role_name)
);

-- Permission 테이블 생성
CREATE TABLE PERMISSION (
    permission_id BIGINT PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50),
    UNIQUE (permission_name)
);

-- User_Role 테이블 생성 (다대다 관계)
CREATE TABLE USER_ROLE (
    user_role_id BIGINT PRIMARY KEY,
    user_id BIGINT,
    role_id BIGINT,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES USER(user_id),
    FOREIGN KEY (role_id) REFERENCES ROLE(role_id),
    UNIQUE (user_id, role_id)
);

-- Role_Permission 테이블 생성 (다대다 관계)
CREATE TABLE ROLE_PERMISSION (
    role_permission_id BIGINT PRIMARY KEY,
    role_id BIGINT,
    permission_id BIGINT,
    permission_name VARCHAR(50),
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES ROLE(role_id),
    FOREIGN KEY (permission_id) REFERENCES PERMISSION(permission_id),
    UNIQUE (role_id, permission_id)
);


