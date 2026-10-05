-- ============ article 表补列 ============
ALTER TABLE article ADD COLUMN category VARCHAR(50) DEFAULT '科普' COMMENT '分类' AFTER title;
ALTER TABLE article ADD COLUMN summary VARCHAR(500) DEFAULT NULL COMMENT '摘要' AFTER category;
ALTER TABLE article ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER audit_time;

-- ============ first_aid_guide 表补列 ============
ALTER TABLE first_aid_guide ADD COLUMN is_generic TINYINT DEFAULT 1 COMMENT '1=通用 0=蛇种专属' AFTER title;
ALTER TABLE first_aid_guide ADD COLUMN snake_id BIGINT DEFAULT NULL COMMENT '关联蛇种ID' AFTER is_generic;
ALTER TABLE first_aid_guide ADD COLUMN content TEXT DEFAULT NULL COMMENT '急救步骤正文' AFTER snake_id;
ALTER TABLE first_aid_guide ADD COLUMN video_url VARCHAR(500) DEFAULT NULL COMMENT '视频URL' AFTER content;
ALTER TABLE first_aid_guide ADD COLUMN sort_order INT DEFAULT 0 COMMENT '排序' AFTER video_url;
ALTER TABLE first_aid_guide ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER audit_time;

-- ============ audit_record 表（审核记录） ============
CREATE TABLE IF NOT EXISTS audit_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  target_type VARCHAR(50) NOT NULL COMMENT '目标类型: article/guide/snake',
  target_id BIGINT NOT NULL COMMENT '目标ID',
  title VARCHAR(200) DEFAULT NULL COMMENT '目标标题/名称快照',
  status TINYINT DEFAULT 1 COMMENT '1=待审 2=通过 3=驳回',
  submitter_id BIGINT DEFAULT NULL COMMENT '提交人ID',
  submitter_name VARCHAR(100) DEFAULT NULL COMMENT '提交人姓名',
  reviewer_id BIGINT DEFAULT NULL COMMENT '审核人ID',
  reviewer_name VARCHAR(100) DEFAULT NULL COMMENT '审核人姓名',
  review_comment VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
  submit_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  review_time DATETIME DEFAULT NULL,
  INDEX idx_target (target_type, target_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;