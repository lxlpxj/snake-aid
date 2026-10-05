-- ===== 人工咨询工单 =====
CREATE TABLE IF NOT EXISTS consultation_ticket (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id       BIGINT NOT NULL COMMENT 'App 用户',
  title         VARCHAR(100) NOT NULL COMMENT '咨询标题',
  category      VARCHAR(30) DEFAULT '蛇伤急救' COMMENT '蛇伤急救/蛇种识别/症状咨询/医院推荐/其他',
  status        VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING待接单/IN_PROGRESS处理中/RESOLVED已解决/CLOSED已关闭',
  assignee_id   BIGINT COMMENT '指派的医疗顾问 sys_user.id',
  last_message  VARCHAR(500) COMMENT '最后一条消息（冗余，用于列表预览）',
  last_time     DATETIME COMMENT '最后消息时间',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_status (status),
  INDEX idx_assignee (assignee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人工咨询工单';

-- ===== 工单消息（用户+顾问对话）=====
CREATE TABLE IF NOT EXISTS consultation_message (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_id     BIGINT NOT NULL,
  sender_type   VARCHAR(10) NOT NULL COMMENT 'USER用户/ADMIN顾问',
  sender_id     BIGINT NOT NULL COMMENT 'app_user.id 或 sys_user.id',
  content       TEXT NOT NULL,
  is_read       TINYINT DEFAULT 0 COMMENT '0未读/1已读',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_ticket (ticket_id),
  INDEX idx_sender (sender_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单对话消息';

-- ===== AI 对话历史（可选，方便用户回看 AI 回答）=====
CREATE TABLE IF NOT EXISTS ai_chat_history (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id       BIGINT NOT NULL,
  role          VARCHAR(10) NOT NULL COMMENT 'user/assistant',
  content       TEXT NOT NULL,
  intent        VARCHAR(50) COMMENT 'AI 识别的意图（蛇种识别/急救步骤...）',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 助手对话历史';
