-- ==========================================
-- 用户与权限模块
-- ==========================================

-- 1. 后台管理用户表
CREATE TABLE sys_user (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                          username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
                          password VARCHAR(100) NOT NULL COMMENT '加密密码',
                          real_name VARCHAR(50) COMMENT '真实姓名',
                          role_type TINYINT NOT NULL COMMENT '角色类型：1-内容管理员, 2-医疗顾问, 3-系统管理员',
                          phone VARCHAR(20) COMMENT '手机号',
                          status TINYINT DEFAULT 1 COMMENT '账号状态：0-禁用, 1-正常',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除'
) COMMENT '后台管理用户表';

-- 2. APP端普通用户表
CREATE TABLE app_user (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                          phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号（登录凭证）',
                          nickname VARCHAR(50) COMMENT '昵称',
                          avatar VARCHAR(255) COMMENT '头像URL',
                          last_login_time DATETIME COMMENT '最后登录时间',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) COMMENT 'APP端普通用户表';


-- ==========================================
-- 蛇种识别图库模块（核心业务）
-- ==========================================

-- 3. 蛇种识别图库表
CREATE TABLE snake_species (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                               name VARCHAR(50) NOT NULL COMMENT '中文名称',
                               scientific_name VARCHAR(100) COMMENT '拉丁学名',
                               is_poisonous TINYINT DEFAULT 1 COMMENT '是否有毒：0-无毒, 1-有毒',
                               venom_type VARCHAR(50) COMMENT '毒性类型：神经毒/血液毒/混合毒',

                               head_shape VARCHAR(50) COMMENT '头型：三角形/椭圆形等',
                               pattern VARCHAR(50) COMMENT '花纹特征',
                               color VARCHAR(50) COMMENT '颜色特征',
                               tail_shape VARCHAR(50) COMMENT '尾部特征',

                               appearance_desc TEXT COMMENT '外观描述',
                               distribution TEXT COMMENT '分布地区描述',
                               symptoms TEXT COMMENT '被咬后特异性症状',
                               first_aid_specifics TEXT COMMENT '特异性处理要点',

                               status TINYINT DEFAULT 0 COMMENT '状态：0-草稿, 1-待审, 2-已发布, 3-已下线',
                               audit_by BIGINT COMMENT '审核人ID（关联 sys_user）',
                               audit_time DATETIME COMMENT '审核时间',

                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '蛇种识别图库表';

-- 4. 蛇种图库图片表
CREATE TABLE snake_image (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             snake_id BIGINT NOT NULL COMMENT '关联蛇种ID',
                             image_url VARCHAR(255) NOT NULL COMMENT '图片地址',
                             is_cover TINYINT DEFAULT 0 COMMENT '是否封面：0-否, 1-是'
) COMMENT '蛇种图库图片表';

-- 5. 地区字典表
CREATE TABLE region (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        parent_id BIGINT DEFAULT 0 COMMENT '父级ID（省市区层级）',
                        name VARCHAR(50) NOT NULL COMMENT '地区名称',
                        level TINYINT COMMENT '层级：1-省, 2-市'
) COMMENT '地区字典表';


-- ==========================================
-- 紧急自救模块
-- ==========================================

-- 6. 自救指南主表
CREATE TABLE first_aid_guide (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 title VARCHAR(100) NOT NULL COMMENT '指南标题（如：神经毒类蛇咬伤自救）',
                                 snake_type VARCHAR(50) COMMENT '适用的蛇种类型/通用指南',
                                 status TINYINT DEFAULT 0 COMMENT '状态：0-草稿, 1-待审, 2-已发布, 3-已下线',
                                 audit_by BIGINT COMMENT '审核人ID',
                                 audit_time DATETIME COMMENT '审核时间',
                                 is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                 create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '自救指南主表';

-- 自救步骤详情表
CREATE TABLE first_aid_step (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                guide_id BIGINT NOT NULL COMMENT '关联指南ID',
                                step_order INT NOT NULL COMMENT '步骤顺序 (如1,2,3...)',
                                title VARCHAR(100) COMMENT '步骤标题（如：制动伤肢）',
                                content TEXT COMMENT '步骤详细说明',
                                warning TEXT COMMENT '禁止动作警示'
) COMMENT '自救步骤详情表';


-- ==========================================
-- 医院与血清库存模块
-- ==========================================

-- 7. 医院信息表
CREATE TABLE hospital (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL COMMENT '医院名称',
                          address VARCHAR(255) COMMENT '详细地址',
                          province VARCHAR(50) COMMENT '省',
                          city VARCHAR(50) COMMENT '市',
                          longitude DECIMAL(10,6) COMMENT '经度 (用于距离计算)',
                          latitude DECIMAL(10,6) COMMENT '纬度 (用于距离计算)',
                          phone VARCHAR(20) COMMENT '急救电话',
                          level VARCHAR(20) COMMENT '医院等级（三甲等）',
                          has_serum TINYINT DEFAULT 0 COMMENT '是否有抗蛇毒血清：0-无, 1-有',
                          is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '医院信息表';

-- 医院血清库存表
CREATE TABLE serum_stock (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             hospital_id BIGINT NOT NULL COMMENT '关联医院ID',
                             serum_type VARCHAR(50) COMMENT '血清类型（如：抗蝮蛇毒血清）',
                             quantity INT DEFAULT 0 COMMENT '库存数量',
                             update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '库存更新时间'
) COMMENT '医院血清库存表';


-- ==========================================
-- 系统支撑模块
-- ==========================================

-- 8. 科普文章表
CREATE TABLE article (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(100) NOT NULL,
                         content TEXT NOT NULL,
                         cover_image VARCHAR(255),
                         status TINYINT DEFAULT 0 COMMENT '0-草稿, 1-待审, 2-已发布, 3-已下线',
                         audit_by BIGINT,
                         audit_time DATETIME,
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                         is_deleted TINYINT DEFAULT 0
) COMMENT '科普文章表';

-- 内容版本记录表
CREATE TABLE content_version (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 version_number VARCHAR(20) NOT NULL COMMENT '版本号（如 v1.0.1）',
                                 update_desc VARCHAR(255) COMMENT '更新说明',
                                 release_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
                                 is_current TINYINT DEFAULT 0 COMMENT '是否当前最新版本'
) COMMENT '内容版本记录表';

-- 访问统计表
CREATE TABLE access_statistics (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   event_type VARCHAR(50) COMMENT '事件类型（如：TRIGGER_EMERGENCY, QUERY_SNAKE）',
                                   province VARCHAR(50) COMMENT '触发省份',
                                   city VARCHAR(50) COMMENT '触发城市',
                                   stat_date DATE COMMENT '统计日期',
                                   count INT DEFAULT 1 COMMENT '次数',
                                   UNIQUE KEY uk_event_date_region (event_type, stat_date, province, city)
) COMMENT '访问统计表';

-- 后台操作日志表
CREATE TABLE operation_log (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT COMMENT '操作人ID (关联 sys_user)',
                               operation VARCHAR(100) COMMENT '操作描述（如：审核通过蛇种）',
                               method VARCHAR(200) COMMENT '请求方法',
                               params TEXT COMMENT '请求参数',
                               ip VARCHAR(50) COMMENT '操作IP',
                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间'
) COMMENT '后台操作日志表';
