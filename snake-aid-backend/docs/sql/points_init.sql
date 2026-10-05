ALTER TABLE app_user ADD COLUMN points INT DEFAULT 0 COMMENT '总积分';
ALTER TABLE app_user ADD COLUMN level INT DEFAULT 1 COMMENT '等级';

CREATE TABLE user_points_log (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT NOT NULL,
  event_type  VARCHAR(30) NOT NULL,
  points      INT NOT NULL,
  ref_id      BIGINT,
  description VARCHAR(100),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE level_config (
  level            INT PRIMARY KEY,
  required_points  INT NOT NULL,
  title            VARCHAR(20) NOT NULL,
  icon             VARCHAR(10) NOT NULL,
  UNIQUE KEY uk_points (required_points)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO level_config (level, required_points, title, icon) VALUES
(1, 0,    '萌新学徒', '🥚'),
(2, 100,  '蛇类学徒', '🐣'),
(3, 300,  '识别新手', '🐍'),
(4, 600,  '急救入门', '🏥'),
(5, 1000, '毒蛇猎手', '🎯'),
(6, 1500, '急救达人', '🦸'),
(7, 2200, '抗毒勇士', '⚔️'),
(8, 3000, '社区之星', '⭐'),
(9, 4000, '救援专家', '👑'),
(10, 5500,'传奇蛇医', '🏆');

UPDATE app_user SET points = 50, level = 2 WHERE points = 0;
