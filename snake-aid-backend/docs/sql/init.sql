-- ============================================================
-- 毒蛇急救系统 init.sql
-- 数据库: snake
-- 字符集: utf8mb4
-- ============================================================

USE snake;

SET NAMES utf8mb4;

-- -----------------------------------------------------------
-- 1. 后台管理用户
--    role_type: SYS_ADMIN(系统管理员) / CONTENT_ADMIN(内容管理员) / MEDICAL_ADVISOR(医疗顾问)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  username    VARCHAR(50)  NOT NULL COMMENT '登录账号',
  password    VARCHAR(100) NOT NULL COMMENT 'BCrypt加密密码',
  real_name   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
  role_type   VARCHAR(30)  NOT NULL COMMENT 'SYS_ADMIN/CONTENT_ADMIN/MEDICAL_ADVISOR',
  phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  status      INT          NOT NULL DEFAULT 1 COMMENT '0-禁用 1-正常',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted  INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username),
  KEY idx_role_type (role_type)
) ENGINE=InnoDB COMMENT='后台管理用户';

-- BCrypt('123456') = $2a$10$/mHAZJWbldWjWXe3ws2TLuufTj56ocrP91/ociWxI4bNexLeDbZdG
INSERT INTO sys_user (username, password, real_name, role_type, phone, status) VALUES
('sysadmin',     '$2a$10$/mHAZJWbldWjWXe3ws2TLuufTj56ocrP91/ociWxI4bNexLeDbZdG', '系统管理员', 'SYS_ADMIN',       '13800000001', 1),
('contentadmin', '$2a$10$/mHAZJWbldWjWXe3ws2TLuufTj56ocrP91/ociWxI4bNexLeDbZdG', '内容运营',   'CONTENT_ADMIN',   '13800000002', 1),
('doctor',       '$2a$10$/mHAZJWbldWjWXe3ws2TLuufTj56ocrP91/ociWxI4bNexLeDbZdG', '张医生',     'MEDICAL_ADVISOR', '13800000003', 1);

-- -----------------------------------------------------------
-- 2. APP端普通用户
-- -----------------------------------------------------------
DROP TABLE IF EXISTS app_user;
CREATE TABLE app_user (
  id              BIGINT       NOT NULL AUTO_INCREMENT,
  phone           VARCHAR(20)  NOT NULL COMMENT '手机号',
  nickname        VARCHAR(50)  DEFAULT NULL,
  avatar          VARCHAR(255) DEFAULT NULL,
  last_login_time DATETIME     DEFAULT NULL,
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB COMMENT='APP端普通用户';

INSERT INTO app_user (phone, nickname) VALUES
('13900000001', '野外老炮'),
('13900000002', '驴友小王'),
('13900000003', '学习中的张同学');

-- -----------------------------------------------------------
-- 3. 蛇种识别图库
-- -----------------------------------------------------------
DROP TABLE IF EXISTS snake_species;
CREATE TABLE snake_species (
  id               BIGINT        NOT NULL AUTO_INCREMENT,
  name             VARCHAR(100)  NOT NULL COMMENT '中文名称',
  scientific_name  VARCHAR(100)  DEFAULT NULL COMMENT '拉丁学名',
  is_poisonous     INT           NOT NULL COMMENT '0-无毒 1-有毒',
  venom_type       VARCHAR(30)   DEFAULT NULL COMMENT '神经毒/血液毒/混合毒',
  head_shape       VARCHAR(50)   DEFAULT NULL COMMENT '头型',
  pattern          VARCHAR(255)  DEFAULT NULL COMMENT '花纹特征',
  color            VARCHAR(255)  DEFAULT NULL COMMENT '颜色特征',
  tail_shape       VARCHAR(100)  DEFAULT NULL COMMENT '尾部特征',
  appearance_desc  TEXT          COMMENT '外观描述',
  distribution     VARCHAR(255)  DEFAULT NULL COMMENT '分布地区',
  symptoms         TEXT          COMMENT '被咬后特异性症状',
  first_aid_specifics TEXT       COMMENT '特异性处理要点',
  status           INT           NOT NULL DEFAULT 2 COMMENT '0-草稿 1-待审 2-已发布 3-已下线',
  audit_status     INT           NOT NULL DEFAULT 1 COMMENT '0-草稿 1-待审 2-已发布 3-已驳回',
  audit_by         BIGINT        DEFAULT NULL,
  audit_time       DATETIME      DEFAULT NULL,
  create_time      DATETIME      DEFAULT CURRENT_TIMESTAMP,
  is_deleted       INT           NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_poisonous (is_poisonous),
  KEY idx_status (status),
  KEY idx_audit_status (audit_status)
) ENGINE=InnoDB COMMENT='蛇种识别图库';

INSERT INTO snake_species (name, scientific_name, is_poisonous, venom_type, head_shape, pattern, color, tail_shape, appearance_desc, distribution, symptoms, first_aid_specifics, status, audit_by, audit_time) VALUES
('银环蛇',  'Bungarus multicinctus', 1, '神经毒', '椭圆形', '黑白相间环纹', '黑色+白色环纹', '短粗',
 '体背黑色，有30-50条白色窄环纹，腹面白色。全长1-1.5米。', '长江以南各省，尤多见于华南、西南',
 '咬伤后伤口不痛不痒，1-4小时后出现眼睑下垂、复视、四肢无力、吞咽困难，严重者呼吸衰竭。',
 '神经毒作用快，必须立即送医。保持制动，禁止切开伤口。抗银环蛇血清特效。', 2, 1, NOW()),
('金环蛇',  'Bungarus fasciatus', 1, '神经毒', '椭圆形', '黑黄相间环纹', '黑色+黄色环纹', '短粗',
 '体背黑色，有20-30条黄色宽环纹，腹面白色。全长1-1.8米。', '广东、广西、福建、云南、海南',
 '与银环蛇类似，神经毒作用，伤口轻微疼痛。', '同银环蛇，抗金环蛇血清。', 2, 1, NOW()),
('眼镜蛇',  'Naja atra', 1, '混合毒', '椭圆形可膨扁', '颈部有眼镜状斑纹', '黑褐色', '圆柱状',
 '颈部可膨扁如眼镜状，体长1-2米。激怒时前半身直立。', '长江以南各省',
 '咬伤后伤口红肿疼痛迅速扩大，伴头晕、眼花、呼吸困难。严重者组织坏死。',
 '保持伤口低于心脏，禁止切开。抗眼镜蛇血清。', 2, 2, NOW()),
('五步蛇',  'Deinagkistrodon acutus', 1, '血液毒', '三角形', '体背有大方形斑纹', '棕褐色+深棕色方块', '尖尾',
 '头大呈三角形，吻端尖出。体长1.2-1.5米。', '长江中下游及以南山区',
 '伤口剧痛、出血不止、肿胀迅速蔓延，皮下出血、血尿。', '必须保持制动，禁止切开。抗五步蛇血清。', 2, 2, NOW()),
('竹叶青',  'Trimeresurus stejnegeri', 1, '血液毒', '三角形', '无明显斑纹', '翠绿色', '红色尾尖',
 '通体翠绿色，尾尖红色或红褐色。体长0.6-0.9米。', '南方竹林、山区',
 '咬伤后局部剧痛、肿胀明显，一般较少全身症状。', '保持制动，局部冷敷。抗蝮蛇毒血清有效。', 2, 2, NOW()),
('乌梢蛇',  'Zaocys dhumnades', 0, NULL, '椭圆形', '体背有4条黑纵纹', '绿褐色', '渐细',
 '全长1.5-2.5米，是常见大型无毒蛇。', '全国大部分地区',
 '无毒蛇咬伤，伤口为两排整齐牙印，无中毒症状。', '伤口清洗消毒，防感染即可。', 2, 1, NOW()),
('王锦蛇',  'Elaphe carinata', 0, NULL, '椭圆形', '头部有"王"字斑纹', '黄褐色+黑色斑纹', '渐细',
 '全长1.5-2米，无毒但性情凶猛。', '长江以南各省',
 '无毒，咬伤后伤口消毒即可。', '局部清洗包扎。', 2, 1, NOW());

-- -----------------------------------------------------------
-- 4. 蛇种图片（示例）
-- -----------------------------------------------------------
DROP TABLE IF EXISTS snake_image;
CREATE TABLE snake_image (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  snake_id   BIGINT       NOT NULL,
  image_url  VARCHAR(500) NOT NULL,
  is_cover   INT          NOT NULL DEFAULT 0 COMMENT '0-否 1-是',
  PRIMARY KEY (id),
  KEY idx_snake_id (snake_id)
) ENGINE=InnoDB COMMENT='蛇种图库图片';

INSERT INTO snake_image (snake_id, image_url, is_cover) VALUES
(1, 'https://img.example.com/silver_krait_1.jpg', 1),
(2, 'https://img.example.com/gold_krait_1.jpg', 1),
(3, 'https://img.example.com/cobra_1.jpg', 1),
(4, 'https://img.example.com/hundred_pacer_1.jpg', 1),
(5, 'https://img.example.com/bamboo_viper_1.jpg', 1),
(6, 'https://img.example.com/black_snake_1.jpg', 1),
(7, 'https://img.example.com/ratsnake_1.jpg', 1);

-- -----------------------------------------------------------
-- 5. 医院信息
-- -----------------------------------------------------------
DROP TABLE IF EXISTS hospital;
CREATE TABLE hospital (
  id          BIGINT        NOT NULL AUTO_INCREMENT,
  name        VARCHAR(200)  NOT NULL,
  address     VARCHAR(300)  DEFAULT NULL,
  province    VARCHAR(50)   DEFAULT NULL,
  city        VARCHAR(50)   DEFAULT NULL,
  longitude   DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
  latitude    DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
  phone       VARCHAR(30)   DEFAULT NULL COMMENT '急救电话',
  level       VARCHAR(20)   DEFAULT NULL COMMENT '三甲/三乙/二甲等',
  has_serum   INT           NOT NULL DEFAULT 0 COMMENT '0-无 1-有抗蛇毒血清',
  update_time DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted  INT           NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_has_serum (has_serum),
  KEY idx_city (province, city)
) ENGINE=InnoDB COMMENT='医院信息';

INSERT INTO hospital (name, address, province, city, longitude, latitude, phone, level, has_serum) VALUES
('广州市第一人民医院',  '广东省广州市越秀区盘福路1号', '广东省', '广州市', 113.2664, 23.1333, '020-81048888', '三甲', 1),
('广州医科大学附属第一医院', '广东省广州市越秀区沿江西路151号', '广东省', '广州市', 113.2443, 23.1136, '020-83062002', '三甲', 1),
('深圳市人民医院',    '广东省深圳市罗湖区东门北路1017号', '广东省', '深圳市', 114.1315, 22.5431, '0755-25533018', '三甲', 1),
('深圳市第二人民医院','广东省深圳市福田区笋岗西路3002号', '广东省', '深圳市', 114.0859, 22.5421, '0755-83366366', '三甲', 0),
('佛山市第一人民医院','广东省佛山市禅城区岭南大道北81号', '广东省', '佛山市', 113.1168, 23.0218, '0757-83163120', '三甲', 1),
('上海市急救中心',    '上海市徐汇区宜山路638号',       '上海市', '上海市', 121.4351, 31.1903, '120',          '三甲', 1),
('北京协和医院',      '北京市东城区帅府园1号',         '北京市', '北京市', 116.4105, 39.9139, '010-69156114', '三甲', 0),
('成都市第一人民医院','四川省成都市高新区万象北路18号', '四川省', '成都市', 104.0665, 30.5728, '028-61881789', '三甲', 1),
('云南省第一人民医院','云南省昆明市西山区金碧路157号', '云南省', '昆明市', 102.7183, 25.0389, '0871-63639911', '三甲', 1);

-- -----------------------------------------------------------
-- 6. 血清库存
-- -----------------------------------------------------------
DROP TABLE IF EXISTS serum_stock;
CREATE TABLE serum_stock (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  hospital_id BIGINT       NOT NULL,
  serum_type  VARCHAR(50)  NOT NULL COMMENT '抗银环蛇毒血清/抗五步蛇毒血清...',
  quantity    INT          NOT NULL DEFAULT 0,
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_hospital (hospital_id)
) ENGINE=InnoDB COMMENT='医院血清库存';

INSERT INTO serum_stock (hospital_id, serum_type, quantity) VALUES
(1, '抗银环蛇毒血清', 20),
(1, '抗五步蛇毒血清', 15),
(1, '抗眼镜蛇毒血清', 10),
(2, '抗银环蛇毒血清', 30),
(2, '抗眼镜蛇毒血清', 20),
(3, '抗银环蛇毒血清', 15),
(3, '抗竹叶青毒血清', 12),
(5, '抗银环蛇毒血清', 8),
(6, '抗蝮蛇毒血清',   25),
(8, '抗银环蛇毒血清', 10),
(9, '抗银环蛇毒血清', 35),
(9, '抗眼镜蛇毒血清', 20),
(9, '抗五步蛇毒血清', 18);

-- -----------------------------------------------------------
-- 7. 自救指南
-- -----------------------------------------------------------
DROP TABLE IF EXISTS first_aid_guide;
CREATE TABLE first_aid_guide (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  title       VARCHAR(200) NOT NULL,
  snake_type  VARCHAR(50)  DEFAULT NULL COMMENT '适用蛇种类型: default/neurotoxic/hemotoxic',
  status      INT          NOT NULL DEFAULT 2 COMMENT '0-草稿 1-待审 2-已发布 3-已下线',
  audit_status INT         NOT NULL DEFAULT 1 COMMENT '0-草稿 1-待审 2-已发布 3-已驳回',
  audit_by    BIGINT       DEFAULT NULL,
  audit_time  DATETIME     DEFAULT NULL,
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
  is_deleted  INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_snake_type (snake_type),
  KEY idx_audit_status (audit_status)
) ENGINE=InnoDB COMMENT='自救指南主表';

INSERT INTO first_aid_guide (title, snake_type, status, audit_by, audit_time) VALUES
('毒蛇咬伤通用自救指南', 'default',    2, 2, NOW()),
('神经毒类咬伤自救指南', 'neurotoxic', 2, 2, NOW()),
('血液毒类咬伤自救指南', 'hemotoxic',  2, 2, NOW());

-- -----------------------------------------------------------
-- 8. 自救步骤
-- -----------------------------------------------------------
DROP TABLE IF EXISTS first_aid_step;
CREATE TABLE first_aid_step (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  guide_id    BIGINT       NOT NULL,
  step_order  INT          NOT NULL COMMENT '步骤顺序 1-6',
  title       VARCHAR(100) NOT NULL,
  content     TEXT         NOT NULL,
  warning     VARCHAR(300) DEFAULT NULL COMMENT '禁止动作',
  PRIMARY KEY (id),
  KEY idx_guide_id (guide_id)
) ENGINE=InnoDB COMMENT='自救步骤详情';

-- 通用自救指南 6 步
INSERT INTO first_aid_step (guide_id, step_order, title, content, warning) VALUES
(1, 1, '立即脱离', '迅速远离蛇可能出没的区域，避免二次攻击。如为蛇咬伤已发生，不要试图捕捉或击打蛇，记住蛇的外观特征以便告知医生。', '禁止奔跑，奔跑会加速血液循环使毒素扩散更快。'),
(1, 2, '保持冷静', '保持冷静，避免情绪激动。坐下或躺下休息，伤肢制动，切勿活动。', '禁止饮酒、吸烟，这些会加速毒素吸收。'),
(1, 3, '伤肢制动', '用夹板或硬物将伤肢固定在低于心脏的位置（约30°角），减少血液回流。如无夹板可将伤肢与健康肢体绑在一起。', '禁止高举伤肢，不要按摩、挤压伤口。'),
(1, 4, '松解束缚', '立即去除伤肢上的戒指、手表、紧身衣物，防止后续肿胀导致组织坏死。', '禁止在伤肢上系止血带（除非大动脉出血，否则会造成组织缺血坏死）。'),
(1, 5, '伤口冲洗', '用大量清水或生理盐水冲洗伤口15分钟以上。有条件可用弱肥皂水清洗伤口周围皮肤，但不要让肥皂水进入伤口深部。', '禁止用刀片切开伤口、用嘴吸吮伤口、涂抹药膏或草药。'),
(1, 6, '紧急就医', '立即拨打120，前往有抗蛇毒血清的医疗机构。就医时告知医生：被蛇咬伤、时间、蛇的外观特征、咬伤部位。', '禁止自行驾车，必须由他人驾车或叫急救车。');

-- 神经毒类（银环蛇/金环蛇）——重点强调快速就医
INSERT INTO first_aid_step (guide_id, step_order, title, content, warning) VALUES
(2, 1, '紧急！立即送医', '神经毒类（银环蛇/金环蛇）咬伤后伤口可能无明显痛感，但1-4小时后会出现呼吸衰竭。**必须立即拨打120**，同时电话联系有血清的医院。', '禁止等待症状出现再就医，一旦出现呼吸困难已极危险。'),
(2, 2, '伤肢严格制动', '伤者绝对卧床休息，伤肢完全制动，不要移动伤者。', '禁止任何活动，包括走路、坐起。'),
(2, 3, '保持呼吸道通畅', '注意观察呼吸，如有窒息迹象立即呼救。让伤者侧卧防止舌后坠。', '禁止平躺时头部过仰。'),
(2, 4, '伤口冲洗', '用清水简单冲洗伤口即可，不需要特殊处理。', '禁止切开伤口、禁止吸吮。'),
(2, 5, '不要自行用药', '不要给伤者服用任何药物，包括所谓的"蛇药"。', '禁止使用不明来源的中药/偏方。'),
(2, 6, '配合医生', '到达医院后立即告知：被银环蛇/金环蛇咬伤，就医时间。主动提及需要抗银环蛇毒血清。', '禁止隐瞒被蛇咬伤史。');

-- -----------------------------------------------------------
-- 9. 科普文章
-- -----------------------------------------------------------
DROP TABLE IF EXISTS article;
CREATE TABLE article (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  title       VARCHAR(200) NOT NULL,
  content     TEXT         NOT NULL,
  cover_image VARCHAR(500) DEFAULT NULL,
  status      INT          NOT NULL DEFAULT 2 COMMENT '0-草稿 1-待审 2-已发布 3-已下线',
  audit_status INT         NOT NULL DEFAULT 1 COMMENT '0-草稿 1-待审 2-已发布 3-已驳回',
  audit_by    BIGINT       DEFAULT NULL,
  audit_time  DATETIME     DEFAULT NULL,
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
  is_deleted  INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_status (status),
  KEY idx_audit_status (audit_status)
) ENGINE=InnoDB COMMENT='科普文章';

INSERT INTO article (title, content, cover_image, status, audit_by, audit_time) VALUES
('如何区分有毒蛇和无毒蛇', '<p>民间有很多辨别蛇是否有毒的说法……</p><p>科学方法：1. 看头型——毒蛇多为三角形；2. 看瞳孔——毒蛇多为竖瞳；3. 看牙印——毒蛇有两个较深的牙痕。</p>', NULL, 2, 2, NOW()),
('被蛇咬伤后的7大误区', '<p>1. 用嘴吸伤口——口腔细菌会感染；2. 用刀切十字——切得不好反而损伤血管神经；3. 绑止血带——会让组织坏死；4. 喝酒解毒——酒精加速血液循环；5. 跑步就医——加速毒素扩散；6. 涂草药/牙膏——没用还可能感染；7. 等症状出来再去医院——有些毒1-2小时就致命。</p>', NULL, 2, 2, NOW()),
('野外作业防蛇攻略', '<p>1. 穿高帮厚底鞋，裤脚扎紧；2. 手持长棍"打草惊蛇"；3. 避开蛇类出没地（水边、石缝、草丛）；4. 傍晚和清晨是蛇活跃高峰，尽量不出没；5. 随身携带急救包。</p>', NULL, 2, 1, NOW());

-- -----------------------------------------------------------
-- 10. 地区字典
-- -----------------------------------------------------------
DROP TABLE IF EXISTS region;
CREATE TABLE region (
  id         BIGINT      NOT NULL AUTO_INCREMENT,
  parent_id  BIGINT      DEFAULT 0 COMMENT '0表示省',
  name       VARCHAR(50) NOT NULL,
  level      INT         NOT NULL COMMENT '1-省 2-市',
  PRIMARY KEY (id),
  KEY idx_parent (parent_id)
) ENGINE=InnoDB COMMENT='地区字典';

INSERT INTO region (parent_id, name, level) VALUES
(0, '广东省', 1), (0, '上海市', 1), (0, '北京市', 1), (0, '四川省', 1), (0, '云南省', 1),
(1, '广州市', 2), (1, '深圳市', 2), (1, '佛山市', 2),
(2, '上海市', 2),
(3, '北京市', 2),
(4, '成都市', 2),
(5, '昆明市', 2);

-- -----------------------------------------------------------
-- 11. 访问统计
-- -----------------------------------------------------------
DROP TABLE IF EXISTS access_statistics;
CREATE TABLE access_statistics (
  id          BIGINT      NOT NULL AUTO_INCREMENT,
  event_type  VARCHAR(50) NOT NULL COMMENT 'emergency/guide/snake/list...',
  province    VARCHAR(50) DEFAULT NULL,
  city        VARCHAR(50) DEFAULT NULL,
  stat_date   DATE        NOT NULL,
  count       INT         NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_date_type (stat_date, event_type)
) ENGINE=InnoDB COMMENT='访问统计';

-- -----------------------------------------------------------
-- 12. 操作日志
-- -----------------------------------------------------------
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  user_id     BIGINT       DEFAULT NULL,
  username    VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名',
  category    VARCHAR(30)  DEFAULT 'CONTENT' COMMENT 'SYSTEM/CONTENT/AUDIT/LOGIN',
  operation   VARCHAR(200) DEFAULT NULL,
  method      VARCHAR(200) DEFAULT NULL,
  params      TEXT,
  ip          VARCHAR(50)  DEFAULT NULL,
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_category (category),
  KEY idx_time (create_time)
) ENGINE=InnoDB COMMENT='后台操作日志';

-- -----------------------------------------------------------
-- 13. 内容版本（预留）
-- -----------------------------------------------------------
DROP TABLE IF EXISTS content_version;
CREATE TABLE content_version (
  id             BIGINT       NOT NULL AUTO_INCREMENT,
  version_number VARCHAR(50)  NOT NULL,
  update_desc    VARCHAR(500) DEFAULT NULL,
  release_time   DATETIME     DEFAULT NULL,
  is_current     INT          NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='内容版本';

INSERT INTO content_version (version_number, update_desc, release_time, is_current) VALUES
('v1.0.0', '初始版本，上线7种主要蛇类识别、通用自救指南、9家定点医院数据', NOW(), 1);

-- ============================================================
-- 完成
-- ============================================================
SELECT '✅ 所有表和初始数据已就绪！' AS result;
SELECT TABLE_NAME, TABLE_ROWS FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'snake' ORDER BY TABLE_NAME;
