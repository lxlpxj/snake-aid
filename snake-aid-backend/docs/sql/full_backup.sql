-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: localhost    Database: snake
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `access_statistics`
--

DROP TABLE IF EXISTS `access_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'emergency/guide/snake/list...',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `stat_date` date NOT NULL,
  `count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_date` (`event_type`,`province`,`city`,`stat_date`),
  KEY `idx_date_type` (`stat_date`,`event_type`)
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问统计';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_statistics`
--

LOCK TABLES `access_statistics` WRITE;
/*!40000 ALTER TABLE `access_statistics` DISABLE KEYS */;
INSERT INTO `access_statistics` VALUES (1,'snake_filter',NULL,NULL,'2026-10-02',1),(2,'snake_filter',NULL,NULL,'2026-10-02',1),(3,'snake_filter',NULL,NULL,'2026-10-02',1),(4,'snake_filter',NULL,NULL,'2026-10-02',1),(5,'snake_filter','河北省',NULL,'2026-10-02',1),(6,'snake_view',NULL,NULL,'2026-10-02',1),(7,'snake_view',NULL,NULL,'2026-10-02',1),(8,'snake_filter','河北省',NULL,'2026-10-02',1),(9,'snake_filter',NULL,NULL,'2026-10-02',1),(10,'snake_filter',NULL,NULL,'2026-10-02',1),(11,'hospital_view',NULL,NULL,'2026-10-02',1),(12,'snake_filter',NULL,NULL,'2026-10-02',1),(13,'snake_filter',NULL,NULL,'2026-10-02',1),(14,'quiz_submit',NULL,NULL,'2026-10-02',1),(15,'quiz_submit',NULL,NULL,'2026-10-02',1),(16,'snake_view',NULL,NULL,'2026-10-02',1),(17,'snake_view',NULL,NULL,'2026-10-02',1),(18,'quiz_submit',NULL,NULL,'2026-10-03',1),(19,'snake_view',NULL,NULL,'2026-10-03',1),(20,'hospital_view','广东',NULL,'2026-10-03',1),(21,'snake_filter',NULL,NULL,'2026-10-03',1),(22,'snake_filter',NULL,NULL,'2026-10-03',1),(23,'snake_view',NULL,NULL,'2026-10-03',1),(24,'snake_filter',NULL,NULL,'2026-10-03',1),(25,'snake_filter',NULL,NULL,'2026-10-03',1),(26,'hospital_view',NULL,NULL,'2026-10-03',1),(27,'snake_filter',NULL,NULL,'2026-10-04',1),(28,'snake_filter',NULL,NULL,'2026-10-04',1),(29,'hospital_view',NULL,NULL,'2026-10-04',1),(30,'snake_filter',NULL,NULL,'2026-10-04',1),(31,'snake_filter',NULL,NULL,'2026-10-04',1),(32,'hospital_view',NULL,NULL,'2026-10-04',1),(33,'snake_filter',NULL,NULL,'2026-10-04',1),(34,'snake_filter',NULL,NULL,'2026-10-04',1),(35,'hospital_view',NULL,NULL,'2026-10-04',1),(36,'snake_filter',NULL,NULL,'2026-10-04',1),(37,'snake_filter',NULL,NULL,'2026-10-04',1),(38,'snake_filter',NULL,NULL,'2026-10-04',1),(39,'snake_filter',NULL,NULL,'2026-10-04',1),(40,'snake_view',NULL,NULL,'2026-10-04',1),(41,'snake_filter',NULL,NULL,'2026-10-04',1),(42,'snake_filter',NULL,NULL,'2026-10-04',1),(43,'snake_filter',NULL,NULL,'2026-10-04',1),(44,'snake_filter',NULL,NULL,'2026-10-04',1),(45,'snake_filter',NULL,NULL,'2026-10-04',1),(46,'snake_filter',NULL,NULL,'2026-10-04',1),(47,'snake_filter',NULL,NULL,'2026-10-04',1),(48,'snake_filter',NULL,NULL,'2026-10-04',1),(49,'snake_filter',NULL,NULL,'2026-10-04',1),(50,'snake_filter',NULL,NULL,'2026-10-04',1),(51,'snake_filter',NULL,NULL,'2026-10-04',1),(52,'snake_filter',NULL,NULL,'2026-10-04',1),(53,'snake_filter',NULL,NULL,'2026-10-04',1),(54,'snake_filter',NULL,NULL,'2026-10-04',1),(55,'snake_filter',NULL,NULL,'2026-10-04',1),(56,'snake_filter',NULL,NULL,'2026-10-04',1),(57,'snake_filter',NULL,NULL,'2026-10-04',1),(58,'snake_filter',NULL,NULL,'2026-10-04',1),(59,'snake_filter',NULL,NULL,'2026-10-04',1),(60,'snake_filter',NULL,NULL,'2026-10-04',1),(61,'snake_filter',NULL,NULL,'2026-10-04',1),(62,'snake_filter',NULL,NULL,'2026-10-04',1),(63,'snake_view',NULL,NULL,'2026-10-04',1),(64,'snake_view',NULL,NULL,'2026-10-04',1),(65,'snake_filter',NULL,NULL,'2026-10-04',1),(66,'snake_filter',NULL,NULL,'2026-10-04',1),(67,'snake_filter',NULL,NULL,'2026-10-04',1),(68,'snake_filter','福建省',NULL,'2026-10-04',1),(69,'snake_filter','福建省',NULL,'2026-10-04',1),(70,'snake_filter','福建省',NULL,'2026-10-04',1),(71,'snake_filter',NULL,NULL,'2026-10-04',1),(72,'snake_filter','福建省',NULL,'2026-10-04',1),(73,'snake_filter','福建省',NULL,'2026-10-04',1),(74,'snake_filter',NULL,NULL,'2026-10-04',1),(75,'snake_filter','福建省',NULL,'2026-10-04',1),(76,'snake_filter',NULL,NULL,'2026-10-04',1),(77,'snake_filter',NULL,NULL,'2026-10-04',1),(78,'snake_filter',NULL,NULL,'2026-10-04',1),(79,'snake_filter',NULL,NULL,'2026-10-04',1),(80,'snake_filter',NULL,NULL,'2026-10-04',1),(81,'snake_filter',NULL,NULL,'2026-10-04',1),(82,'snake_filter',NULL,NULL,'2026-10-04',1),(83,'snake_view',NULL,NULL,'2026-10-04',1),(84,'snake_filter',NULL,NULL,'2026-10-04',1),(85,'snake_filter',NULL,NULL,'2026-10-04',1),(86,'snake_filter',NULL,NULL,'2026-10-04',1),(87,'snake_filter',NULL,NULL,'2026-10-04',1),(88,'snake_filter',NULL,NULL,'2026-10-04',1),(89,'snake_filter',NULL,NULL,'2026-10-04',1),(90,'snake_filter',NULL,NULL,'2026-10-04',1),(91,'snake_filter',NULL,NULL,'2026-10-04',1),(92,'snake_filter',NULL,NULL,'2026-10-04',1),(93,'snake_filter',NULL,NULL,'2026-10-04',1),(94,'snake_filter',NULL,NULL,'2026-10-04',1),(95,'snake_filter',NULL,NULL,'2026-10-04',1),(96,'snake_filter',NULL,NULL,'2026-10-04',1),(97,'snake_view',NULL,NULL,'2026-10-04',1),(98,'snake_filter',NULL,NULL,'2026-10-04',1),(99,'snake_filter',NULL,NULL,'2026-10-04',1),(100,'snake_filter',NULL,NULL,'2026-10-04',1),(101,'snake_filter',NULL,NULL,'2026-10-04',1),(102,'snake_filter',NULL,NULL,'2026-10-04',1),(103,'snake_filter',NULL,NULL,'2026-10-04',1),(104,'snake_filter',NULL,NULL,'2026-10-04',1),(105,'snake_filter',NULL,NULL,'2026-10-04',1),(106,'snake_filter',NULL,NULL,'2026-10-05',1),(107,'snake_filter',NULL,NULL,'2026-10-05',1),(108,'snake_filter',NULL,NULL,'2026-10-05',1),(109,'snake_filter',NULL,NULL,'2026-10-05',1),(110,'snake_filter',NULL,NULL,'2026-10-05',1),(111,'snake_filter',NULL,NULL,'2026-10-05',1),(112,'snake_filter',NULL,NULL,'2026-10-05',1),(113,'snake_filter',NULL,NULL,'2026-10-05',1),(114,'snake_filter',NULL,NULL,'2026-10-05',1),(115,'snake_filter',NULL,NULL,'2026-10-05',1),(116,'snake_filter',NULL,NULL,'2026-10-05',1),(117,'snake_filter',NULL,NULL,'2026-10-05',1),(118,'snake_filter',NULL,NULL,'2026-10-05',1),(119,'snake_filter',NULL,NULL,'2026-10-05',1),(120,'snake_filter',NULL,NULL,'2026-10-05',1),(121,'snake_filter',NULL,NULL,'2026-10-05',1),(122,'snake_filter',NULL,NULL,'2026-10-05',1),(123,'snake_filter',NULL,NULL,'2026-10-05',1),(124,'snake_filter',NULL,NULL,'2026-10-05',1),(125,'snake_filter',NULL,NULL,'2026-10-05',1),(126,'snake_filter',NULL,NULL,'2026-10-05',1),(127,'snake_filter',NULL,NULL,'2026-10-05',1),(128,'snake_filter',NULL,NULL,'2026-10-05',1),(129,'snake_filter',NULL,NULL,'2026-10-05',1),(130,'snake_filter',NULL,NULL,'2026-10-05',1),(131,'snake_filter',NULL,NULL,'2026-10-05',1),(132,'snake_filter',NULL,NULL,'2026-10-05',1),(133,'snake_filter',NULL,NULL,'2026-10-05',1),(134,'snake_filter',NULL,NULL,'2026-10-05',1),(135,'snake_filter',NULL,NULL,'2026-10-05',1),(136,'snake_filter',NULL,NULL,'2026-10-05',1),(137,'hospital_view',NULL,NULL,'2026-10-05',1),(138,'hospital_view',NULL,NULL,'2026-10-05',1),(139,'snake_filter',NULL,NULL,'2026-10-05',1),(140,'snake_filter',NULL,NULL,'2026-10-05',1);
/*!40000 ALTER TABLE `access_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_chat_history`
--

DROP TABLE IF EXISTS `ai_chat_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_chat_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role` varchar(10) NOT NULL COMMENT 'user/assistant',
  `content` text NOT NULL,
  `intent` varchar(50) DEFAULT NULL COMMENT 'AI 识别的意图（蛇种识别/急救步骤...）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI 助手对话历史';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_chat_history`
--

LOCK TABLES `ai_chat_history` WRITE;
/*!40000 ALTER TABLE `ai_chat_history` DISABLE KEYS */;
INSERT INTO `ai_chat_history` VALUES (1,4,'user','被蛇咬了怎么办？','first_aid','2026-10-04 13:14:30'),(2,4,'assistant','🚨 **毒蛇咬伤急救 7 步走** 🚨\n\n① **保持冷静** → 不要慌张，不要剧烈运动\n② **制动伤肢** → 坐下/躺下，把咬伤的腿/手固定在低于心脏的位置\n③ **移除紧箍物** → 摘掉戒指、手镯、袜子（肿胀会勒紧）\n④ **用清水冲洗伤口** → 至少 15 分钟，不要用碘酒/酒精\n⑤ **不要做这些！** → ❌不要切开伤口 ❌不要吸吮毒液 ❌不要用止血带\n⑥ **立刻就医** → 打 120 或开车去最近的医院\n⑦ **拍下蛇的照片** → 如果能安全拍到（不要抓！），告诉医生蛇的特征\n\n💡 **提示**：我国已知有近 200 种蛇，其中约 50 种有毒。\n只有专业医生才能判断是否需要抗蛇毒血清！\n','first_aid','2026-10-04 13:14:30'),(3,4,'user','我在厦门，去哪个医院','hospital','2026-10-04 13:14:46'),(4,4,'assistant','🏥 **就医建议**\n\n✅ **需要抗蛇毒血清的毒蛇咬伤**，必须去有血清的医院！\n\n📍 **查询附近有血清的医院**：\n   App → 「附近医院」页面可以查看合作医院清单\n   （也可以直接拨 120 告诉急救人员被毒蛇咬伤）\n\n⚠️ **注意事项**：\n   • 抗蛇毒血清分种类——银环蛇毒血清、五步蛇毒血清等各不相同\n   • 告诉医生蛇的样子/拍下的照片，帮助快速判断用哪种血清\n   • 如果条件允许，可以打电话先确认医院是否有对应血清\n\n🚨 **黄金 6 小时**：越早使用血清越好！\n','hospital','2026-10-04 13:14:46'),(5,4,'user','你帮我查查','general','2026-10-04 13:14:58'),(6,4,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你：\n📸 **识别蛇种** → 输入蛇名或描述特征\n🚨 **急救指导** → 被咬后该怎么做\n🩺 **症状判断** → 不同蛇伤有什么表现\n🏥 **就医建议** → 去哪里找血清\n🛡️ **预防方法** → 野外如何防蛇\n\n💬 **试试问我**：\n• 「银环蛇有毒吗？」\n• 「被蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「附近哪里有血清？」\n\n⚠️ 重要提醒：我提供的是参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤请立即拨打 120 或前往医院！\n','general','2026-10-04 13:14:58'),(7,3,'user','??','general','2026-10-04 13:19:17'),(8,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(9,3,'user','??????','general','2026-10-04 13:19:17'),(10,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(11,3,'user','?????????','general','2026-10-04 13:19:17'),(12,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(13,3,'user','??????????','general','2026-10-04 13:19:17'),(14,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(15,3,'user','???????','general','2026-10-04 13:19:17'),(16,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(17,3,'user','???????','general','2026-10-04 13:19:17'),(18,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(19,3,'user','??????','general','2026-10-04 13:19:17'),(20,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(21,3,'user','???????','general','2026-10-04 13:19:17'),(22,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(23,3,'user','??????','general','2026-10-04 13:19:17'),(24,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(25,3,'user','??????','general','2026-10-04 13:19:17'),(26,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(27,3,'user','?????','general','2026-10-04 13:19:17'),(28,3,'assistant','🐍 你好！我是**蛇伤急救 AI 助手** 🐍\n\n我可以帮你（直接问就行）：\n📸 **识别蛇种** → 「银环蛇有毒吗？」\n🚨 **急救指导** → 「被蛇咬了怎么办？」\n🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）\n📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」\n🏥 **就医建议** → 「附近哪里有血清？」\n🧪 **毒型科普** → 「银环蛇是什么毒？」\n🛡️ **预防方法** → 「野外怎么防蛇？」\n\n💬 **试试这些**：\n• 「被银环蛇咬了怎么办？」\n• 「五步蛇有什么症状？」\n• 「竹叶青毒性大吗？」\n\n⚠️ 我提供参考信息，不能替代专业医疗诊断！\n真正被蛇咬伤 → **立即拨打 120！**\n','general','2026-10-04 13:19:17'),(29,3,'user','银环蛇有毒吗','venom_type','2026-10-04 13:21:05'),(30,3,'assistant','🧪 **毒蛇毒型科普**\n\n🔵 **神经毒**（银环蛇、眼镜蛇、海蛇）：\n   作用于神经系统 → 肌肉麻痹、呼吸衰竭\n   伤口可能不红不肿 → 但数小时内可致命！\n\n🔴 **血循毒**（五步蛇、竹叶青、蝰蛇）：\n   破坏血管和凝血 → 肿胀、出血、组织坏死\n   伤口剧痛、迅速肿胀\n\n🟠 **混合毒**（眼镜王蛇、蝮蛇）：\n   两者同时出现 → 更加凶险\n\n✅ 知道毒型可帮助医生快速选血清。\n但对普通人来说 → **被蛇咬了立刻就医**更重要！\n\n💡 想知道**某条蛇的毒型**？告诉我蛇名！\n','venom_type','2026-10-04 13:21:05'),(31,3,'user','它有什么症状','snake_bite_symptom','2026-10-04 13:21:05'),(32,3,'assistant','📋 **毒蛇咬伤常见症状**\n\n🧪 **神经毒类**（银环蛇、眼镜蛇、海蛇）：\n   → 伤口可能不红不肿，但非常危险！\n   → 早期：眼睑下垂、视物模糊、声音嘶哑\n   → 中期：肌肉酸痛、吞咽困难\n   → 晚期：呼吸困难、呼吸麻痹 → 窒息\n   ⏰ 银环蛇咬伤可能 1-2 小时就到危险阶段！\n\n🩸 **血循毒类**（五步蛇、竹叶青、蝰蛇）：\n   → 伤口剧烈灼痛、迅速肿胀（1-2 小时扩散整个肢体）\n   → 出血不止、皮肤瘀斑、血泡\n   → 严重：组织坏死（发黑）、肾衰竭、心悸休克\n\n🟠 **混合毒**（眼镜王蛇、蝮蛇）：两者同时出现\n\n🏥 **无论出现什么症状，哪怕很轻微，都要立即就医！**\n\n💡 想知道**某条蛇的特定症状**？告诉我蛇名！\n   比如「竹叶青咬伤有什么症状？」\n','snake_bite_symptom','2026-10-04 13:21:05'),(33,3,'user','那该怎么办','first_aid','2026-10-04 13:21:05'),(34,3,'assistant','🚨 **毒蛇咬伤急救 7 步走** 🚨\n\n① **保持冷静** → 不要慌张，不要剧烈运动\n② **制动伤肢** → 坐下/躺下，把咬伤的腿/手固定在低于心脏的位置\n③ **移除紧箍物** → 摘掉戒指、手镯、袜子（肿胀会勒紧）\n④ **用清水冲洗伤口** → 至少 15 分钟，不要用碘酒/酒精\n⑤ **不要做这些！** → ❌不要切开伤口 ❌不要吸吮毒液 ❌不要用止血带 ❌不要冰敷\n⑥ **立刻就医** → 打 120 或开车去最近的医院\n⑦ **拍下蛇的照片** → 如果能安全拍到（不要抓！），告诉医生蛇的特征\n\n💡 想知道**某条特定蛇**该怎么急救？告诉我蛇的名字！\n   比如「被五步蛇咬了怎么办？」我会针对性回答\n','first_aid','2026-10-04 13:21:05');
/*!40000 ALTER TABLE `ai_chat_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_user`
--

DROP TABLE IF EXISTS `app_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `points` int DEFAULT '0' COMMENT '总积分',
  `level` int DEFAULT '1' COMMENT '等级',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='APP端普通用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_user`
--

LOCK TABLES `app_user` WRITE;
/*!40000 ALTER TABLE `app_user` DISABLE KEYS */;
INSERT INTO `app_user` VALUES (1,'13900000001','野外老炮',NULL,'2026-10-03 09:55:54','2026-09-25 12:47:47',54,1),(2,'13900000002','驴友小王',NULL,'2026-10-02 22:49:15','2026-09-25 12:47:47',52,1),(3,'13900000003','学习中的张同学',NULL,NULL,'2026-09-25 12:47:47',50,1),(4,'18150909556','用户9556',NULL,'2026-10-04 11:05:23','2026-10-02 23:58:35',42,1);
/*!40000 ALTER TABLE `app_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '科普' COMMENT '分类',
  `summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `cover_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` int NOT NULL DEFAULT '2' COMMENT '0-草稿 1-待审 2-已发布 3-已下线',
  `audit_by` bigint DEFAULT NULL,
  `audit_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` int NOT NULL DEFAULT '0',
  `audit_status` int NOT NULL DEFAULT '1' COMMENT '0-草稿 1-待审 2-已发布 3-已驳回',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普文章';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (1,'如何区分有毒蛇和无毒蛇','科普',NULL,'<p>民间有很多辨别蛇是否有毒的说法……</p><p>科学方法：1. 看头型——毒蛇多为三角形；2. 看瞳孔——毒蛇多为竖瞳；3. 看牙印——毒蛇有两个较深的牙痕。</p>',NULL,0,2,'2026-09-25 12:47:49','2026-10-01 22:00:13','2026-09-25 12:47:49',0,1),(2,'被蛇咬伤后的7大误区','科普',NULL,'<p>1. 用嘴吸伤口——口腔细菌会感染；2. 用刀切十字——切得不好反而损伤血管神经；3. 绑止血带——会让组织坏死；4. 喝酒解毒——酒精加速血液循环；5. 跑步就医——加速毒素扩散；6. 涂草药/牙膏——没用还可能感染；7. 等症状出来再去医院——有些毒1-2小时就致命。</p>',NULL,0,2,'2026-09-25 12:47:49','2026-10-01 22:00:13','2026-09-25 12:47:49',0,1),(3,'野外作业防蛇攻略','科普',NULL,'<p>1. 穿高帮厚底鞋，裤脚扎紧；2. 手持长棍\"打草惊蛇\"；3. 避开蛇类出没地（水边、石缝、草丛）；4. 傍晚和清晨是蛇活跃高峰，尽量不出没；5. 随身携带急救包。</p>',NULL,0,1,'2026-09-25 12:47:49','2026-10-01 22:00:13','2026-09-25 12:47:49',0,1);
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_record`
--

DROP TABLE IF EXISTS `audit_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型: article/guide/snake',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标标题/名称快照',
  `status` tinyint DEFAULT '1' COMMENT '1=待审 2=通过 3=驳回',
  `submitter_id` bigint DEFAULT NULL COMMENT '提交人ID',
  `submitter_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提交人姓名',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `reviewer_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核人姓名',
  `review_comment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核意见',
  `submit_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `review_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_record`
--

LOCK TABLES `audit_record` WRITE;
/*!40000 ALTER TABLE `audit_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultation_message`
--

DROP TABLE IF EXISTS `consultation_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ticket_id` bigint NOT NULL,
  `sender_type` varchar(10) NOT NULL COMMENT 'USER用户/ADMIN顾问',
  `sender_id` bigint NOT NULL COMMENT 'app_user.id 或 sys_user.id',
  `content` text NOT NULL,
  `is_read` tinyint DEFAULT '0' COMMENT '0未读/1已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ticket` (`ticket_id`),
  KEY `idx_sender` (`sender_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单对话消息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultation_message`
--

LOCK TABLES `consultation_message` WRITE;
/*!40000 ALTER TABLE `consultation_message` DISABLE KEYS */;
INSERT INTO `consultation_message` VALUES (4,3,'USER',4,'疼痛',0,'2026-10-04 11:05:46'),(6,3,'USER',4,'1',0,'2026-10-04 12:32:06'),(8,3,'USER',4,'好的',0,'2026-10-04 12:53:00'),(9,2,'ADMIN',3,'??,?????,?????????',0,'2026-10-04 13:06:12'),(10,3,'ADMIN',3,'欧克',0,'2026-10-04 13:08:40'),(11,6,'USER',4,'111',0,'2026-10-04 13:09:07'),(13,8,'USER',4,'22',0,'2026-10-04 13:13:30'),(20,10,'USER',1,'医生您好！我下午在山上不小心被一条翠绿色带红尾巴的蛇咬了手指，现在手指越来越肿，有点害怕！',0,'2026-10-02 16:02:43'),(21,10,'ADMIN',3,'不要慌！你描述的应该是竹叶青（血循毒类），请立刻：1.坐下制动；2.清水冲伤口15分钟；3.摘戒指；4.马上就医！',0,'2026-10-02 16:12:43'),(22,10,'USER',1,'好的医生！已经在去医院路上了，大概还要半小时到',0,'2026-10-02 16:17:43'),(23,10,'ADMIN',3,'好的！告诉医生是竹叶青咬伤，可能需要抗五步蛇毒血清。伤肢固定在低于心脏位置',0,'2026-10-02 16:22:43'),(24,11,'USER',1,'银环蛇是不是特别毒？据说咬了可能救不回来？',0,'2026-10-03 16:02:43'),(25,11,'ADMIN',3,'银环蛇确实是剧毒蛇！它是神经毒类，伤口可能不红不肿，但1-2小时内可能出现呼吸困难！及时注射血清存活率很高',0,'2026-10-03 16:07:43'),(26,12,'USER',2,'周末要去武夷山露营，听说那边蛇很多，有什么防蛇建议吗？',0,'2026-10-04 10:02:43'),(27,12,'ADMIN',3,'武夷山确实蛇多。建议：1.厚长裤+高帮鞋；2.带长棍打草惊蛇；3.夜间强光手电；4.帐篷随时拉拉链；5.清晨傍晚格外小心',0,'2026-10-04 11:02:43'),(28,13,'USER',2,'请问被五步蛇咬伤需要用什么血清？普通医院有吗？',0,'2026-10-04 14:02:43'),(29,14,'USER',1,'我朋友被眼镜蛇咬了，现在伤口有点红肿但他说没什么特别感觉，会不会很危险？',0,'2026-10-04 15:32:43');
/*!40000 ALTER TABLE `consultation_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultation_ticket`
--

DROP TABLE IF EXISTS `consultation_ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation_ticket` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'App 用户',
  `title` varchar(100) NOT NULL COMMENT '咨询标题',
  `category` varchar(30) DEFAULT '蛇伤急救' COMMENT '蛇伤急救/蛇种识别/症状咨询/医院推荐/其他',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT 'PENDING待接单/IN_PROGRESS处理中/RESOLVED已解决/CLOSED已关闭',
  `assignee_id` bigint DEFAULT NULL COMMENT '指派的医疗顾问 sys_user.id',
  `last_message` varchar(500) DEFAULT NULL COMMENT '最后一条消息（冗余，用于列表预览）',
  `last_time` datetime DEFAULT NULL COMMENT '最后消息时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_assignee` (`assignee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人工咨询工单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultation_ticket`
--

LOCK TABLES `consultation_ticket` WRITE;
/*!40000 ALTER TABLE `consultation_ticket` DISABLE KEYS */;
INSERT INTO `consultation_ticket` VALUES (3,4,'被咬伤','蛇伤急救','IN_PROGRESS',3,'欧克','2026-10-04 13:08:40','2026-10-04 11:05:45','2026-10-04 11:05:45'),(6,4,'11','蛇伤急救','IN_PROGRESS',3,'111','2026-10-04 13:09:07','2026-10-04 13:09:06','2026-10-04 13:09:06'),(8,4,'22','蛇伤急救','PENDING',3,'22','2026-10-04 13:13:30','2026-10-04 13:13:30','2026-10-04 13:13:30'),(10,1,'被竹叶青咬了，紧急！','蛇伤急救','RESOLVED',3,'好的！到了医院告诉医生是竹叶青咬伤...','2026-10-02 16:22:43','2026-10-04 16:02:43','2026-10-04 16:02:43'),(11,1,'想了解银环蛇毒性','蛇种咨询','RESOLVED',3,'银环蛇确实是剧毒蛇！它是神经毒类...','2026-10-03 16:07:43','2026-10-04 16:02:43','2026-10-04 16:02:43'),(12,2,'野外露营防蛇建议','预防咨询','IN_PROGRESS',3,'好的！武夷山确实蛇多...','2026-10-04 11:02:43','2026-10-04 16:02:43','2026-10-04 16:02:43'),(13,2,'五步蛇咬伤该打什么血清','治疗咨询','PENDING',3,NULL,'2026-10-04 14:02:43','2026-10-04 16:02:43','2026-10-04 16:02:43'),(14,1,'眼镜蛇咬伤症状很严重吗','症状咨询','PENDING',3,NULL,'2026-10-04 15:32:43','2026-10-04 16:02:43','2026-10-04 16:02:43');
/*!40000 ALTER TABLE `consultation_ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_version`
--

DROP TABLE IF EXISTS `content_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `content_version` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `update_desc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `release_time` datetime DEFAULT NULL,
  `is_current` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容版本';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_version`
--

LOCK TABLES `content_version` WRITE;
/*!40000 ALTER TABLE `content_version` DISABLE KEYS */;
INSERT INTO `content_version` VALUES (1,'v1.0.0','初始版本，上线7种主要蛇类识别、通用自救指南、9家定点医院数据','2026-09-25 12:47:49',1);
/*!40000 ALTER TABLE `content_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `first_aid_guide`
--

DROP TABLE IF EXISTS `first_aid_guide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `first_aid_guide` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_generic` tinyint DEFAULT '1' COMMENT '1=通用 0=蛇种专属',
  `snake_id` bigint DEFAULT NULL COMMENT '关联蛇种ID',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '急救步骤正文',
  `video_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频URL',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `snake_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '适用蛇种类型: default/neurotoxic/hemotoxic',
  `status` int NOT NULL DEFAULT '2' COMMENT '0-草稿 1-待审 2-已发布 3-已下线',
  `audit_by` bigint DEFAULT NULL,
  `audit_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` int NOT NULL DEFAULT '0',
  `audit_status` int NOT NULL DEFAULT '1' COMMENT '0-草稿 1-待审 2-已发布 3-已驳回',
  PRIMARY KEY (`id`),
  KEY `idx_snake_type` (`snake_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自救指南主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `first_aid_guide`
--

LOCK TABLES `first_aid_guide` WRITE;
/*!40000 ALTER TABLE `first_aid_guide` DISABLE KEYS */;
INSERT INTO `first_aid_guide` VALUES (1,'毒蛇咬伤通用自救指南',1,NULL,NULL,NULL,0,'default',2,2,'2026-09-25 12:47:48','2026-10-01 16:49:31','2026-09-25 12:47:48',0,1),(2,'神经毒类咬伤自救指南',1,NULL,NULL,NULL,0,'neurotoxic',2,2,'2026-09-25 12:47:48','2026-10-01 16:49:31','2026-09-25 12:47:48',0,1),(3,'血液毒类咬伤自救指南',1,NULL,NULL,NULL,0,'hemotoxic',2,2,'2026-09-25 12:47:48','2026-10-01 16:49:31','2026-09-25 12:47:48',0,1);
/*!40000 ALTER TABLE `first_aid_guide` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `first_aid_step`
--

DROP TABLE IF EXISTS `first_aid_step`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `first_aid_step` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `guide_id` bigint NOT NULL,
  `step_order` int NOT NULL COMMENT '步骤顺序 1-6',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `warning` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '禁止动作',
  PRIMARY KEY (`id`),
  KEY `idx_guide_id` (`guide_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自救步骤详情';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `first_aid_step`
--

LOCK TABLES `first_aid_step` WRITE;
/*!40000 ALTER TABLE `first_aid_step` DISABLE KEYS */;
INSERT INTO `first_aid_step` VALUES (1,1,1,'立即脱离','迅速远离蛇可能出没的区域，避免二次攻击。如为蛇咬伤已发生，不要试图捕捉或击打蛇，记住蛇的外观特征以便告知医生。','禁止奔跑，奔跑会加速血液循环使毒素扩散更快。'),(2,1,2,'保持冷静','保持冷静，避免情绪激动。坐下或躺下休息，伤肢制动，切勿活动。','禁止饮酒、吸烟，这些会加速毒素吸收。'),(3,1,3,'伤肢制动','用夹板或硬物将伤肢固定在低于心脏的位置（约30°角），减少血液回流。如无夹板可将伤肢与健康肢体绑在一起。','禁止高举伤肢，不要按摩、挤压伤口。'),(4,1,4,'松解束缚','立即去除伤肢上的戒指、手表、紧身衣物，防止后续肿胀导致组织坏死。','禁止在伤肢上系止血带（除非大动脉出血，否则会造成组织缺血坏死）。'),(5,1,5,'伤口冲洗','用大量清水或生理盐水冲洗伤口15分钟以上。有条件可用弱肥皂水清洗伤口周围皮肤，但不要让肥皂水进入伤口深部。','禁止用刀片切开伤口、用嘴吸吮伤口、涂抹药膏或草药。'),(6,1,6,'紧急就医','立即拨打120，前往有抗蛇毒血清的医疗机构。就医时告知医生：被蛇咬伤、时间、蛇的外观特征、咬伤部位。','禁止自行驾车，必须由他人驾车或叫急救车。'),(7,2,1,'紧急！立即送医','神经毒类（银环蛇/金环蛇）咬伤后伤口可能无明显痛感，但1-4小时后会出现呼吸衰竭。**必须立即拨打120**，同时电话联系有血清的医院。','禁止等待症状出现再就医，一旦出现呼吸困难已极危险。'),(8,2,2,'伤肢严格制动','伤者绝对卧床休息，伤肢完全制动，不要移动伤者。','禁止任何活动，包括走路、坐起。'),(9,2,3,'保持呼吸道通畅','注意观察呼吸，如有窒息迹象立即呼救。让伤者侧卧防止舌后坠。','禁止平躺时头部过仰。'),(10,2,4,'伤口冲洗','用清水简单冲洗伤口即可，不需要特殊处理。','禁止切开伤口、禁止吸吮。'),(11,2,5,'不要自行用药','不要给伤者服用任何药物，包括所谓的\"蛇药\"。','禁止使用不明来源的中药/偏方。'),(12,2,6,'配合医生','到达医院后立即告知：被银环蛇/金环蛇咬伤，就医时间。主动提及需要抗银环蛇毒血清。','禁止隐瞒被蛇咬伤史。');
/*!40000 ALTER TABLE `first_aid_step` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hospital`
--

DROP TABLE IF EXISTS `hospital`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hospital` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `phone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '急救电话',
  `level` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '三甲/三乙/二甲等',
  `has_serum` int NOT NULL DEFAULT '0' COMMENT '0-无 1-有抗蛇毒血清',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_has_serum` (`has_serum`),
  KEY `idx_city` (`province`,`city`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医院信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hospital`
--

LOCK TABLES `hospital` WRITE;
/*!40000 ALTER TABLE `hospital` DISABLE KEYS */;
INSERT INTO `hospital` VALUES (1,'广州市第一人民医院','广东省广州市越秀区盘福路1号','广东省','广州市',113.266400,23.133300,'020-81048888','三甲',1,'2026-09-25 12:47:48',0),(2,'广州医科大学附属第一医院','广东省广州市越秀区沿江西路151号','广东省','广州市',113.244300,23.113600,'020-83062002','三甲',1,'2026-09-25 12:47:48',0),(3,'深圳市人民医院','广东省深圳市罗湖区东门北路1017号','广东省','深圳市',114.131500,22.543100,'0755-25533018','三甲',1,'2026-09-25 12:47:48',0),(4,'深圳市第二人民医院','广东省深圳市福田区笋岗西路3002号','广东省','深圳市',114.085900,22.542100,'0755-83366366','三甲',0,'2026-09-25 12:47:48',0),(5,'佛山市第一人民医院','广东省佛山市禅城区岭南大道北81号','广东省','佛山市',113.116800,23.021800,'0757-83163120','三甲',1,'2026-09-25 12:47:48',0),(6,'上海市急救中心','上海市徐汇区宜山路638号','上海市','上海市',121.435100,31.190300,'120','三甲',1,'2026-09-25 12:47:48',0),(7,'北京协和医院','北京市东城区帅府园1号','北京市','北京市',116.410500,39.913900,'010-69156114','三甲',0,'2026-09-25 12:47:48',0),(8,'成都市第一人民医院','四川省成都市高新区万象北路18号','四川省','成都市',104.066500,30.572800,'028-61881789','三甲',1,'2026-09-25 12:47:48',0),(9,'云南省第一人民医院','云南省昆明市西山区金碧路157号','云南省','昆明市',102.718300,25.038900,'0871-63639911','三甲',1,'2026-09-25 12:47:48',0);
/*!40000 ALTER TABLE `hospital` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `level_config`
--

DROP TABLE IF EXISTS `level_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `level_config` (
  `level` int NOT NULL,
  `required_points` int NOT NULL,
  `title` varchar(20) NOT NULL,
  `icon` varchar(10) NOT NULL,
  PRIMARY KEY (`level`),
  UNIQUE KEY `uk_points` (`required_points`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `level_config`
--

LOCK TABLES `level_config` WRITE;
/*!40000 ALTER TABLE `level_config` DISABLE KEYS */;
INSERT INTO `level_config` VALUES (1,0,'萌新学徒','🥚'),(2,100,'蛇类学徒','🐣'),(3,300,'识别新手','🐍'),(4,600,'急救入门','🏥'),(5,1000,'毒蛇猎手','🎯'),(6,1500,'急救达人','🦸'),(7,2200,'抗毒勇士','⚔️'),(8,3000,'社区之星','⭐'),(9,4000,'救援专家','👑'),(10,5500,'传奇蛇医','🏆');
/*!40000 ALTER TABLE `level_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `category` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'CONTENT' COMMENT 'SYSTEM/CONTENT/AUDIT/LOGIN',
  `operation` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `method` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `params` text COLLATE utf8mb4_unicode_ci,
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=382 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台操作日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (1,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-27 00:26:12'),(2,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-27 00:26:12'),(3,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-27 00:26:17'),(4,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-27 00:26:17'),(5,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-27 00:26:29'),(6,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-27 00:26:29'),(7,1,'sysadmin','LOGIN','登录: sysadmin','AuthController#login',NULL,'172.18.0.1','2026-09-27 00:28:09'),(8,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-09-27 00:28:09'),(9,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-27 00:28:09'),(10,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-27 00:28:10'),(11,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-27 00:28:14'),(12,2,'contentadmin','LOGIN','登录: contentadmin','AuthController#login',NULL,'172.18.0.1','2026-09-27 00:28:25'),(13,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-09-27 00:28:25'),(14,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-27 00:28:25'),(15,2,'contentadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-27 00:28:26'),(16,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-27 00:28:28'),(17,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-27 00:30:41'),(18,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-27 00:30:41'),(19,1,'sysadmin','LOGIN','登录: sysadmin','AuthController#login',NULL,'172.18.0.1','2026-09-27 00:34:44'),(20,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-09-27 00:34:44'),(21,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-27 00:34:45'),(22,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-27 00:35:16'),(23,2,'contentadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-27 00:35:21'),(24,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-27 00:37:02'),(25,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-27 00:37:02'),(26,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-28 20:49:23'),(27,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-28 20:49:23'),(28,1,'sysadmin','LOGIN','登录: sysadmin','AuthController#login',NULL,'172.18.0.1','2026-09-28 20:50:11'),(29,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-09-28 20:50:11'),(30,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-28 20:50:11'),(31,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-28 20:50:14'),(32,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-28 20:50:15'),(33,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-29 00:21:30'),(34,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 00:21:31'),(35,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-29 00:21:33'),(36,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 00:22:02'),(37,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 00:22:02'),(38,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 00:22:05'),(39,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 00:22:06'),(40,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 00:22:06'),(41,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 00:22:07'),(42,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','features','[\"\",\"\",\"\",\"\"]','172.18.0.1','2026-09-29 00:22:11'),(43,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 00:22:16'),(44,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 00:22:17'),(45,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 00:22:27'),(46,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 00:22:27'),(47,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-29 10:56:50'),(48,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-29 10:56:52'),(49,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 10:56:53'),(50,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-29 10:56:55'),(51,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-29 10:57:36'),(52,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 10:57:40'),(53,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-29 10:57:49'),(54,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-29 10:57:51'),(55,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 10:57:53'),(56,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','详情','[1]','172.18.0.1','2026-09-29 10:58:17'),(57,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-09-29 10:58:59'),(58,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-09-29 10:59:15'),(59,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-09-29 11:00:54'),(60,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-09-29 11:00:54'),(61,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-09-29 11:00:54'),(62,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:13:45'),(63,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:13:45'),(64,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 11:13:48'),(65,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:19:34'),(66,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:19:34'),(67,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 11:19:40'),(68,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:22:05'),(69,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:22:05'),(70,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:22:17'),(71,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:22:17'),(72,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-09-29 11:25:37'),(73,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-29 11:25:50'),(74,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 11:25:53'),(75,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[3,10,null,null,null]','172.18.0.1','2026-09-29 11:25:55'),(76,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[2,10,null,null,null]','172.18.0.1','2026-09-29 11:25:57'),(77,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 11:25:59'),(78,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[4,10,null,null,null]','172.18.0.1','2026-09-29 11:26:01'),(79,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[3,10,null,null,null]','172.18.0.1','2026-09-29 11:26:03'),(80,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[2,10,null,null,null]','172.18.0.1','2026-09-29 11:26:05'),(81,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[5,10,null,null,null]','172.18.0.1','2026-09-29 11:26:07'),(82,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[6,10,null,null,null]','172.18.0.1','2026-09-29 11:26:09'),(83,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[8,10,null,null,null]','172.18.0.1','2026-09-29 11:26:12'),(84,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[9,10,null,null,null]','172.18.0.1','2026-09-29 11:26:14'),(85,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-29 11:26:16'),(86,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-29 11:26:21'),(87,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-09-29 11:26:28'),(88,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:26:51'),(89,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:26:51'),(90,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 11:26:54'),(91,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:27:02'),(92,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:27:02'),(93,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:29:15'),(94,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:29:15'),(95,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 11:29:19'),(96,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:29:21'),(97,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:29:21'),(98,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 11:29:26'),(99,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:29:29'),(100,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:29:29'),(101,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:31:07'),(102,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:31:07'),(103,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:33:59'),(104,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:33:59'),(105,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:38:01'),(106,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:38:01'),(107,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 11:38:04'),(108,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 11:38:07'),(109,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:38:09'),(110,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:38:09'),(111,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:38:32'),(112,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:38:32'),(113,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,0]','172.18.0.1','2026-09-29 11:38:34'),(114,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:38:37'),(115,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:38:37'),(116,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-29 11:40:36'),(117,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 11:40:38'),(118,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-29 11:40:41'),(119,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 11:41:41'),(120,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 11:44:06'),(121,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,6,null,null,0]','172.18.0.1','2026-09-29 11:44:07'),(122,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,20,null,null,1]','172.18.0.1','2026-09-29 11:44:07'),(123,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-29 11:46:50'),(124,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 11:46:52'),(125,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 11:46:52'),(126,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[2,10,null,null,null]','172.18.0.1','2026-09-29 11:46:55'),(127,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[3,10,null,null,null]','172.18.0.1','2026-09-29 11:46:58'),(128,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[4,10,null,null,null]','172.18.0.1','2026-09-29 11:47:00'),(129,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 13:50:43'),(130,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 13:54:49'),(131,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-09-29 13:55:07'),(132,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-29 17:11:29'),(133,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-29 17:11:31'),(134,1,'sysadmin','LOGIN','登录: sysadmin','AuthController#login',NULL,'172.18.0.1','2026-09-30 09:20:12'),(135,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-09-30 09:20:12'),(136,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-09-30 09:20:13'),(137,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-09-30 09:20:16'),(138,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-09-30 09:20:17'),(139,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','submit','[\"Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiIzIiwiaWF0IjoxNzg5OTU2OTcwLCJleHAiOjE3ODk5NjA1NzB9.RQpaJJoWKYTqghdAUHrwLOVBdCvPESJjBfqKFtvP7Y9kEDm_uA3PpTYzqkgTzbHqLD1tKv7_ev5chvCC9-uRUg\",{\"details\":[{\"questionId\":1,\"userAnswer\":\"A\"},{\"questionId\":5,\"userAnswer\":\"B\"},{\"questionId\":2,\"userAnswer\":\"A\"},{\"questionId\":4,\"userAnswer\":\"B\"},{\"questionId\":17,\"userAnswer\":\"D\"},{\"questionId\":18,\"userAnswer\":\"D\"},{\"questionId\":6,\"userAnswer\":\"B\"},{\"questionId\":3,\"userAnswer\":\"ABC\"}],\"durationSec\":93}]','172.18.0.1','2026-09-30 09:25:38'),(140,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','submit','[\"Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiIzIiwiaWF0IjoxNzg5OTU2OTcwLCJleHAiOjE3ODk5NjA1NzB9.RQpaJJoWKYTqghdAUHrwLOVBdCvPESJjBfqKFtvP7Y9kEDm_uA3PpTYzqkgTzbHqLD1tKv7_ev5chvCC9-uRUg\",{\"details\":[{\"questionId\":1,\"userAnswer\":\"A\"},{\"questionId\":5,\"userAnswer\":\"B\"},{\"questionId\":2,\"userAnswer\":\"A\"},{\"questionId\":4,\"userAnswer\":\"B\"},{\"questionId\":17,\"userAnswer\":\"D\"},{\"questionId\":18,\"userAnswer\":\"D\"},{\"questionId\":6,\"userAnswer\":\"B\"},{\"questionId\":3,\"userAnswer\":\"ABC\"}],\"durationSec\":93}]','172.18.0.1','2026-09-30 09:25:49'),(141,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','submit','[\"Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiIzIiwiaWF0IjoxNzg5OTU2OTcwLCJleHAiOjE3ODk5NjA1NzB9.RQpaJJoWKYTqghdAUHrwLOVBdCvPESJjBfqKFtvP7Y9kEDm_uA3PpTYzqkgTzbHqLD1tKv7_ev5chvCC9-uRUg\",{\"details\":[{\"questionId\":1,\"userAnswer\":\"A\"},{\"questionId\":5,\"userAnswer\":\"B\"},{\"questionId\":2,\"userAnswer\":\"A\"},{\"questionId\":4,\"userAnswer\":\"B\"},{\"questionId\":17,\"userAnswer\":\"D\"},{\"questionId\":18,\"userAnswer\":\"D\"},{\"questionId\":6,\"userAnswer\":\"B\"},{\"questionId\":3,\"userAnswer\":\"ABC\"}],\"durationSec\":93}]','172.18.0.1','2026-09-30 09:27:00'),(142,NULL,NULL,'SYSTEM','异常: BadSqlGrammarException - ','submit','[\"Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiIzIiwiaWF0IjoxNzg5OTU2OTcwLCJleHAiOjE3ODk5NjA1NzB9.RQpaJJoWKYTqghdAUHrwLOVBdCvPESJjBfqKFtvP7Y9kEDm_uA3PpTYzqkgTzbHqLD1tKv7_ev5chvCC9-uRUg\",{\"details\":[{\"questionId\":2,\"userAnswer\":\"B\"},{\"questionId\":3,\"userAnswer\":\"ABC\"},{\"questionId\":5,\"userAnswer\":\"A\"},{\"questionId\":18,\"userAnswer\":\"D\"},{\"questionId\":1,\"userAnswer\":\"D\"}],\"durationSec\":42}]','172.18.0.1','2026-09-30 23:41:30'),(143,NULL,NULL,'SYSTEM','异常: ClassCastException - ','submit','[null,{\"userId\":1,\"answers\":[{\"questionId\":1,\"userAnswer\":\"A\"},{\"questionId\":2,\"userAnswer\":\"B\"}]}]','172.18.0.1','2026-09-30 23:42:34'),(144,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-10-01 16:33:01'),(145,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-10-01 16:33:06'),(146,2,'contentadmin','LOGIN','登录: contentadmin','AuthController#login',NULL,'172.18.0.1','2026-10-01 16:33:16'),(147,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-01 16:33:16'),(148,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-01 16:33:16'),(149,1,'sysadmin','LOGIN','登录: sysadmin','AuthController#login',NULL,'172.18.0.1','2026-10-01 16:34:07'),(150,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-01 16:34:07'),(151,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-01 16:34:07'),(152,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-10-01 16:34:09'),(153,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-01 16:34:10'),(154,2,'contentadmin','LOGIN','登录: contentadmin','AuthController#login',NULL,'172.18.0.1','2026-10-01 16:34:31'),(155,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-01 16:34:31'),(156,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-01 16:34:31'),(157,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-01 16:42:30'),(158,2,'contentadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-01 16:42:38'),(159,2,'contentadmin','SYSTEM','异常: BadSqlGrammarException - 查询科普文章','分页列表','[null,1,null,100,null,null]','172.18.0.1','2026-10-01 16:42:43'),(160,2,'contentadmin','CONTENT','查询蛇种','分页列表','[null,1,null,100,null,null,null]','172.18.0.1','2026-10-01 16:42:44'),(161,2,'contentadmin','SYSTEM','异常: BadSqlGrammarException - 查询急救指南','分页列表','[1,100,null]','172.18.0.1','2026-10-01 16:42:44'),(162,2,'contentadmin','CONTENT','查询医院','分页列表','[null,1,null,10,null,null,null]','172.18.0.1','2026-10-01 16:43:05'),(163,2,'contentadmin','SYSTEM','异常: BadSqlGrammarException - 查询急救指南','分页列表','[1,10,null]','172.18.0.1','2026-10-01 16:43:07'),(164,2,'contentadmin','SYSTEM','异常: BadSqlGrammarException - 查询科普文章','分页列表','[null,1,null,10,null,null]','172.18.0.1','2026-10-01 16:43:18'),(165,2,'contentadmin','CONTENT','查询蛇种','分页列表','[null,1,null,10,null,null,\"\"]','172.18.0.1','2026-10-01 16:43:27'),(166,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-01 16:43:28'),(167,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-10-01 16:53:22'),(168,2,'contentadmin','CONTENT','查询蛇种','分页列表','[null,1,null,10,null,null,\"\"]','172.18.0.1','2026-10-01 22:00:11'),(169,2,'contentadmin','CONTENT','查询科普文章','分页列表','[null,1,null,10,null,null]','172.18.0.1','2026-10-01 22:00:12'),(170,2,'contentadmin','SYSTEM','修改科普文章','修改保存',NULL,'172.18.0.1','2026-10-01 22:00:13'),(171,2,'contentadmin','SYSTEM','修改科普文章','修改保存',NULL,'172.18.0.1','2026-10-01 22:00:13'),(172,2,'contentadmin','SYSTEM','修改科普文章','修改保存',NULL,'172.18.0.1','2026-10-01 22:00:13'),(173,2,'contentadmin','CONTENT','查询急救指南','分页列表','[1,10,null]','172.18.0.1','2026-10-01 22:00:13'),(174,2,'contentadmin','CONTENT','查询医院','分页列表','[null,1,null,10,null,null,null]','172.18.0.1','2026-10-01 22:00:14'),(175,2,'contentadmin','CONTENT','查询科普文章','分页列表','[null,1,null,100,null,null]','172.18.0.1','2026-10-01 22:00:15'),(176,2,'contentadmin','CONTENT','查询蛇种','分页列表','[null,1,null,100,null,null,null]','172.18.0.1','2026-10-01 22:00:15'),(177,2,'contentadmin','CONTENT','查询急救指南','分页列表','[1,100,null]','172.18.0.1','2026-10-01 22:00:15'),(178,2,'contentadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-01 22:00:17'),(179,2,'contentadmin','CONTENT','查询科普文章','分页列表','[null,1,null,100,null,null]','172.18.0.1','2026-10-01 22:00:19'),(180,2,'contentadmin','CONTENT','查询蛇种','分页列表','[null,1,null,100,null,null,null]','172.18.0.1','2026-10-01 22:00:19'),(181,2,'contentadmin','CONTENT','查询急救指南','分页列表','[1,100,null]','172.18.0.1','2026-10-01 22:00:20'),(182,2,'contentadmin','CONTENT','查询医院','分页列表','[null,1,null,10,null,null,null]','172.18.0.1','2026-10-01 22:00:21'),(183,2,'contentadmin','CONTENT','查询急救指南','分页列表','[1,10,null]','172.18.0.1','2026-10-01 22:00:23'),(184,2,'contentadmin','CONTENT','查询科普文章','分页列表','[null,1,null,10,null,null]','172.18.0.1','2026-10-01 22:00:24'),(185,2,'contentadmin','CONTENT','查询蛇种','分页列表','[null,1,null,10,null,null,\"\"]','172.18.0.1','2026-10-01 22:00:27'),(186,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-01 22:00:28'),(187,2,'contentadmin','CONTENT','查询急救指南','分页列表','[1,10,null]','172.18.0.1','2026-10-01 22:01:01'),(188,2,'contentadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-01 23:17:15'),(189,2,'contentadmin','CONTENT','查询科普文章','分页列表','[null,1,null,100,null,null]','172.18.0.1','2026-10-01 23:17:16'),(190,2,'contentadmin','CONTENT','查询蛇种','分页列表','[null,1,null,100,null,null,null]','172.18.0.1','2026-10-01 23:17:16'),(191,2,'contentadmin','CONTENT','查询急救指南','分页列表','[1,100,null]','172.18.0.1','2026-10-01 23:17:16'),(192,2,'contentadmin','CONTENT','查询医院','分页列表','[null,1,null,10,null,null,null]','172.18.0.1','2026-10-01 23:17:16'),(193,2,'contentadmin','CONTENT','查询急救指南','分页列表','[1,10,null]','172.18.0.1','2026-10-01 23:17:17'),(194,2,'contentadmin','CONTENT','查询科普文章','分页列表','[null,1,null,10,null,null]','172.18.0.1','2026-10-01 23:17:19'),(195,2,'contentadmin','CONTENT','查询蛇种','分页列表','[null,1,null,10,null,null,\"\"]','172.18.0.1','2026-10-01 23:17:19'),(196,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-01 23:17:20'),(197,2,'contentadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-01 23:38:19'),(198,2,'contentadmin','LOGIN','登录: contentadmin','AuthController#login',NULL,'172.18.0.1','2026-10-01 23:39:21'),(199,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-01 23:39:21'),(200,2,'contentadmin','SYSTEM','查询question','分页列表','[1,5,null,null,null,null]','172.18.0.1','2026-10-01 23:39:22'),(201,2,'contentadmin','LOGIN','登录: contentadmin','AuthController#login',NULL,'172.18.0.1','2026-10-01 23:43:27'),(202,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-01 23:43:27'),(203,2,'contentadmin','SYSTEM','查询question','分页列表','[1,5,null,null,null,null]','172.18.0.1','2026-10-01 23:43:27'),(204,2,'contentadmin','CONTENT','查询科普文章','分页列表','[null,1,null,100,null,null]','172.18.0.1','2026-10-02 10:21:50'),(205,2,'contentadmin','CONTENT','查询蛇种','分页列表','[null,1,null,100,null,null,null]','172.18.0.1','2026-10-02 10:21:50'),(206,2,'contentadmin','CONTENT','查询急救指南','分页列表','[1,100,null]','172.18.0.1','2026-10-02 10:21:50'),(207,2,'contentadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-02 10:21:51'),(208,2,'contentadmin','SYSTEM','查询question','分页列表','[1,10,null,null,null,null]','172.18.0.1','2026-10-02 10:21:57'),(209,2,'contentadmin','SYSTEM','查询question','分页列表','[2,10,null,null,null,null]','172.18.0.1','2026-10-02 10:22:01'),(210,2,'contentadmin','SYSTEM','新增question','新增保存',NULL,'172.18.0.1','2026-10-02 10:22:12'),(211,2,'contentadmin','SYSTEM','查询question','分页列表','[2,10,null,null,null,null]','172.18.0.1','2026-10-02 10:22:12'),(212,2,'contentadmin','SYSTEM','查询question','分页列表','[1,10,null,null,null,null]','172.18.0.1','2026-10-02 10:22:17'),(213,2,'contentadmin','SYSTEM','删除question','删除','[21]','172.18.0.1','2026-10-02 10:22:29'),(214,2,'contentadmin','SYSTEM','查询question','分页列表','[1,10,null,null,null,null]','172.18.0.1','2026-10-02 10:22:29'),(215,1,'sysadmin','LOGIN','登录: sysadmin','AuthController#login',NULL,'172.18.0.1','2026-10-02 22:15:17'),(216,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-02 22:15:17'),(217,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-02 22:15:17'),(218,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-10-02 22:15:20'),(219,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-02 22:15:21'),(220,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-02 22:15:22'),(221,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-10-02 22:15:26'),(222,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-02 22:15:27'),(223,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-02 22:15:27'),(224,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-02 22:17:46'),(225,1,'sysadmin','SYSTEM','查询数据统计','provinceTop',NULL,'172.18.0.1','2026-10-02 22:17:46'),(226,1,'sysadmin','SYSTEM','异常: BadSqlGrammarException - 查询数据统计','snakeTraits',NULL,'172.18.0.1','2026-10-02 22:17:47'),(227,1,'sysadmin','SYSTEM','查询数据统计','quizStats',NULL,'172.18.0.1','2026-10-02 22:17:47'),(228,1,'sysadmin','SYSTEM','查询数据统计','trend',NULL,'172.18.0.1','2026-10-02 22:17:47'),(229,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-10-02 22:20:51'),(230,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-02 22:23:43'),(231,1,'sysadmin','SYSTEM','查询数据统计','provinceTop',NULL,'172.18.0.1','2026-10-02 22:23:43'),(232,1,'sysadmin','SYSTEM','查询数据统计','snakeTraits',NULL,'172.18.0.1','2026-10-02 22:23:43'),(233,1,'sysadmin','SYSTEM','查询数据统计','quizStats',NULL,'172.18.0.1','2026-10-02 22:23:43'),(234,1,'sysadmin','SYSTEM','查询数据统计','trend',NULL,'172.18.0.1','2026-10-02 22:23:44'),(235,1,'sysadmin','LOGIN','登录: sysadmin','AuthController#login',NULL,'172.18.0.1','2026-10-02 22:24:19'),(236,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-02 22:24:19'),(237,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-02 22:24:19'),(238,1,'sysadmin','SYSTEM','查询数据统计','snakeTraits',NULL,'172.18.0.1','2026-10-02 22:24:19'),(239,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-02 22:30:26'),(240,1,'sysadmin','SYSTEM','查询数据统计','provinceTop',NULL,'172.18.0.1','2026-10-02 22:30:26'),(241,1,'sysadmin','SYSTEM','查询数据统计','snakeTraits',NULL,'172.18.0.1','2026-10-02 22:30:26'),(242,1,'sysadmin','SYSTEM','查询数据统计','quizStats',NULL,'172.18.0.1','2026-10-02 22:30:26'),(243,1,'sysadmin','SYSTEM','查询数据统计','trend',NULL,'172.18.0.1','2026-10-02 22:30:26'),(244,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-02 22:30:53'),(245,1,'sysadmin','SYSTEM','查询数据统计','provinceTop',NULL,'172.18.0.1','2026-10-02 22:30:53'),(246,1,'sysadmin','SYSTEM','查询数据统计','snakeTraits',NULL,'172.18.0.1','2026-10-02 22:30:53'),(247,1,'sysadmin','SYSTEM','查询数据统计','quizStats',NULL,'172.18.0.1','2026-10-02 22:30:53'),(248,1,'sysadmin','SYSTEM','查询数据统计','trend',NULL,'172.18.0.1','2026-10-02 22:30:53'),(249,NULL,NULL,'SYSTEM','异常: BusinessException - ','登录','[{\"phone\":\"13800000001\",\"password\":\"123456\"}]','172.18.0.1','2026-10-02 22:45:10'),(250,NULL,NULL,'SYSTEM','异常: BusinessException - ','登录','[{\"phone\":\"13800000001\",\"password\":\"123456\"}]','172.18.0.1','2026-10-02 22:45:22'),(251,NULL,NULL,'SYSTEM','异常: BusinessException - ','登录','[{\"phone\":\"13800000001\",\"password\":\"admin123\"}]','172.18.0.1','2026-10-02 22:45:22'),(252,NULL,NULL,'SYSTEM','异常: BusinessException - ','登录','[{\"phone\":\"13800000001\",\"password\":\"password\"}]','172.18.0.1','2026-10-02 22:45:22'),(253,NULL,NULL,'SYSTEM','异常: BusinessException - ','登录','[{\"phone\":\"13800000001\",\"password\":\"123123\"}]','172.18.0.1','2026-10-02 22:45:22'),(254,1,'sysadmin','SYSTEM','查询账号','分页列表','[1,10,null,null]','172.18.0.1','2026-10-02 23:30:36'),(255,1,'sysadmin','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-02 23:30:37'),(256,1,'sysadmin','SYSTEM','查询数据统计','统计概览',NULL,'172.18.0.1','2026-10-02 23:30:38'),(257,1,'sysadmin','SYSTEM','查询数据统计','provinceTop',NULL,'172.18.0.1','2026-10-02 23:30:38'),(258,1,'sysadmin','SYSTEM','查询数据统计','snakeTraits',NULL,'172.18.0.1','2026-10-02 23:30:38'),(259,1,'sysadmin','SYSTEM','查询数据统计','quizStats',NULL,'172.18.0.1','2026-10-02 23:30:38'),(260,1,'sysadmin','SYSTEM','查询数据统计','trend',NULL,'172.18.0.1','2026-10-02 23:30:38'),(261,1,'sysadmin','LOGIN','登录: sysadmin','AuthController#login',NULL,'172.18.0.1','2026-10-02 23:37:23'),(262,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-02 23:37:23'),(263,1,'sysadmin','SYSTEM','查询数据统计','leaderboard','[10]','172.18.0.1','2026-10-02 23:37:24'),(264,1,'sysadmin','SYSTEM','查询数据统计','levelDistribution',NULL,'172.18.0.1','2026-10-02 23:37:24'),(265,3,'doctor','LOGIN','登录: doctor','AuthController#login',NULL,'172.18.0.1','2026-10-03 23:50:17'),(266,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-03 23:50:17'),(267,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,100,1,null]','172.18.0.1','2026-10-03 23:50:18'),(268,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,100,1,null,null]','172.18.0.1','2026-10-03 23:50:18'),(269,3,'doctor','CONTENT','查询急救指南','分页列表','[1,100,1]','172.18.0.1','2026-10-03 23:50:18'),(270,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,10,null,null,\"\"]','172.18.0.1','2026-10-03 23:50:19'),(271,3,'doctor','CONTENT','查询急救指南','分页列表','[1,10,null]','172.18.0.1','2026-10-03 23:50:20'),(272,3,'doctor','SYSTEM','查询question','分页列表','[1,10,null,null,null,null]','172.18.0.1','2026-10-03 23:50:21'),(273,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,10,null,null]','172.18.0.1','2026-10-03 23:50:21'),(274,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,100,1,null]','172.18.0.1','2026-10-03 23:50:22'),(275,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,100,1,null,null]','172.18.0.1','2026-10-03 23:50:22'),(276,3,'doctor','CONTENT','查询急救指南','分页列表','[1,100,1]','172.18.0.1','2026-10-03 23:50:22'),(277,3,'doctor','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-03 23:50:23'),(278,3,'doctor','SYSTEM','查询question','分页列表','[1,10,null,null,null,null]','172.18.0.1','2026-10-03 23:52:32'),(279,3,'doctor','CONTENT','查询急救指南','分页列表','[1,10,null]','172.18.0.1','2026-10-03 23:52:33'),(280,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,10,null,null]','172.18.0.1','2026-10-03 23:52:34'),(281,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,10,null,null,\"\"]','172.18.0.1','2026-10-03 23:52:36'),(282,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,100,1,null]','172.18.0.1','2026-10-03 23:52:37'),(283,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,100,1,null,null]','172.18.0.1','2026-10-03 23:52:37'),(284,3,'doctor','CONTENT','查询急救指南','分页列表','[1,100,1]','172.18.0.1','2026-10-03 23:52:37'),(285,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,10,null,null,\"\"]','172.18.0.1','2026-10-03 23:52:46'),(286,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,100,1,null]','172.18.0.1','2026-10-03 23:52:47'),(287,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,100,1,null,null]','172.18.0.1','2026-10-03 23:52:47'),(288,3,'doctor','CONTENT','查询急救指南','分页列表','[1,100,1]','172.18.0.1','2026-10-03 23:52:47'),(289,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,100,2,null]','172.18.0.1','2026-10-03 23:52:51'),(290,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,100,2,null,null]','172.18.0.1','2026-10-03 23:52:51'),(291,3,'doctor','CONTENT','查询急救指南','分页列表','[1,100,2]','172.18.0.1','2026-10-03 23:52:51'),(292,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,100,3,null]','172.18.0.1','2026-10-03 23:52:52'),(293,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,100,3,null,null]','172.18.0.1','2026-10-03 23:52:52'),(294,3,'doctor','CONTENT','查询急救指南','分页列表','[1,100,3]','172.18.0.1','2026-10-03 23:52:52'),(295,3,'doctor','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-04 00:28:04'),(296,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,10,null,null,\"\"]','172.18.0.1','2026-10-04 00:28:05'),(297,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,10,null,null]','172.18.0.1','2026-10-04 00:28:05'),(298,3,'doctor','CONTENT','查询急救指南','分页列表','[1,10,null]','172.18.0.1','2026-10-04 00:28:06'),(299,3,'doctor','SYSTEM','查询question','分页列表','[1,10,null,null,null,null]','172.18.0.1','2026-10-04 00:28:07'),(300,3,'doctor','SYSTEM','查询question','分页列表','[1,10,null,null,null,null]','172.18.0.1','2026-10-04 00:28:09'),(301,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,100,1,null]','172.18.0.1','2026-10-04 00:28:13'),(302,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,100,1,null,null]','172.18.0.1','2026-10-04 00:28:13'),(303,3,'doctor','CONTENT','查询急救指南','分页列表','[1,100,1]','172.18.0.1','2026-10-04 00:28:13'),(304,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 00:28:15'),(305,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,100,1,null]','172.18.0.1','2026-10-04 11:05:57'),(306,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,100,1,null,null]','172.18.0.1','2026-10-04 11:05:57'),(307,3,'doctor','CONTENT','查询急救指南','分页列表','[1,100,1]','172.18.0.1','2026-10-04 11:05:57'),(308,3,'doctor','SYSTEM','查询操作日志','分页列表','[1,10,null,null,null]','172.18.0.1','2026-10-04 11:06:00'),(309,3,'doctor','SYSTEM','查询question','分页列表','[1,10,null,null,null,null]','172.18.0.1','2026-10-04 11:06:01'),(310,3,'doctor','CONTENT','查询急救指南','分页列表','[1,10,null]','172.18.0.1','2026-10-04 11:06:01'),(311,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,10,null,null]','172.18.0.1','2026-10-04 11:06:03'),(312,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,10,null,null,\"\"]','172.18.0.1','2026-10-04 11:06:05'),(313,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 11:06:06'),(314,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 11:06:09'),(315,3,'doctor','CONTENT','查询科普文章','分页列表','[null,1,null,100,1,null]','172.18.0.1','2026-10-04 12:08:39'),(316,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:08:39'),(317,3,'doctor','CONTENT','查询蛇种','分页列表','[null,1,null,100,1,null,null]','172.18.0.1','2026-10-04 12:08:39'),(318,3,'doctor','CONTENT','查询急救指南','分页列表','[1,100,1]','172.18.0.1','2026-10-04 12:08:39'),(319,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:08:40'),(320,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 12:10:08'),(321,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 12:10:31'),(322,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 12:16:46'),(323,1,'sysadmin','LOGIN','登录: sysadmin','AuthController#login',NULL,'172.18.0.1','2026-10-04 12:17:01'),(324,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 12:17:01'),(325,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 12:17:02'),(326,NULL,NULL,'SYSTEM','异常: BusinessException - 新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 12:17:02'),(327,3,'doctor','LOGIN','登录: doctor','AuthController#login',NULL,'172.18.0.1','2026-10-04 12:17:21'),(328,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 12:17:21'),(329,3,'doctor','SYSTEM','查询consult','tickets','[null,1,10]','172.18.0.1','2026-10-04 12:17:21'),(330,3,'doctor','SYSTEM','查询consult','tickets','[null,1,10]','172.18.0.1','2026-10-04 12:17:21'),(331,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:31:50'),(332,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:32:09'),(333,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:32:25'),(334,3,'doctor','LOGIN','登录: doctor','AuthController#login',NULL,'172.18.0.1','2026-10-04 12:34:10'),(335,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 12:34:10'),(336,3,'doctor','SYSTEM','查询consult','tickets','[null,1,10]','172.18.0.1','2026-10-04 12:34:10'),(337,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:37:21'),(338,3,'doctor','SYSTEM','查询consult','详情','[4]','172.18.0.1','2026-10-04 12:37:25'),(339,3,'doctor','SYSTEM','查询consult','详情','[5]','172.18.0.1','2026-10-04 12:37:28'),(340,3,'doctor','SYSTEM','查询consult','详情','[3]','172.18.0.1','2026-10-04 12:37:29'),(341,3,'doctor','SYSTEM','查询consult','详情','[2]','172.18.0.1','2026-10-04 12:37:32'),(342,3,'doctor','LOGIN','登录: doctor','AuthController#login',NULL,'172.18.0.1','2026-10-04 12:46:59'),(343,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 12:47:00'),(344,3,'doctor','SYSTEM','删除consult','deleteTicket','[5]','172.18.0.1','2026-10-04 12:47:00'),(345,3,'doctor','SYSTEM','删除consult','deleteMessage','[1]','172.18.0.1','2026-10-04 12:47:00'),(346,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:52:30'),(347,3,'doctor','SYSTEM','查询consult','详情','[3]','172.18.0.1','2026-10-04 12:52:33'),(348,3,'doctor','SYSTEM','查询consult','详情','[4]','172.18.0.1','2026-10-04 12:52:38'),(349,3,'doctor','SYSTEM','删除consult','deleteTicket','[4]','172.18.0.1','2026-10-04 12:52:42'),(350,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:52:42'),(351,3,'doctor','SYSTEM','查询consult','详情','[2]','172.18.0.1','2026-10-04 12:52:42'),(352,3,'doctor','SYSTEM','删除consult','deleteTicket','[2]','172.18.0.1','2026-10-04 12:52:44'),(353,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:52:44'),(354,3,'doctor','SYSTEM','查询consult','详情','[1]','172.18.0.1','2026-10-04 12:52:45'),(355,3,'doctor','SYSTEM','删除consult','deleteTicket','[1]','172.18.0.1','2026-10-04 12:52:47'),(356,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 12:52:47'),(357,3,'doctor','SYSTEM','查询consult','详情','[3]','172.18.0.1','2026-10-04 12:53:03'),(358,3,'doctor','SYSTEM','异常: DataIntegrityViolationException - 新增consult','reply','[3,{\"content\":\"欧克\"}]','172.18.0.1','2026-10-04 12:53:09'),(359,3,'doctor','SYSTEM','异常: DataIntegrityViolationException - 新增consult','reply','[3,{\"content\":\"欧克\"}]','172.18.0.1','2026-10-04 12:53:34'),(360,3,'doctor','SYSTEM','异常: DataIntegrityViolationException - 新增consult','reply','[3,{\"content\":\"欧克\"}]','172.18.0.1','2026-10-04 12:54:28'),(361,3,'doctor','LOGIN','登录: doctor','AuthController#login',NULL,'172.18.0.1','2026-10-04 13:02:57'),(362,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 13:02:57'),(363,3,'doctor','SYSTEM','异常: DataIntegrityViolationException - 新增consult','reply','[2,{\"content\":\"??,?????,?????????\"}]','172.18.0.1','2026-10-04 13:02:58'),(364,3,'doctor','LOGIN','登录: doctor','AuthController#login',NULL,'172.18.0.1','2026-10-04 13:06:12'),(365,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 13:06:12'),(366,3,'doctor','SYSTEM','新增consult','reply','[2,{\"content\":\"??,?????,?????????\"}]','172.18.0.1','2026-10-04 13:06:12'),(367,3,'doctor','SYSTEM','新增consult','reply','[3,{\"content\":\"欧克\"}]','172.18.0.1','2026-10-04 13:08:40'),(368,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 13:08:40'),(369,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 13:09:12'),(370,3,'doctor','LOGIN','登录: doctor','AuthController#login',NULL,'172.18.0.1','2026-10-04 13:11:03'),(371,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 13:11:03'),(372,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 13:13:11'),(373,3,'doctor','SYSTEM','查询consult','详情','[7]','172.18.0.1','2026-10-04 13:13:16'),(374,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 13:13:34'),(375,3,'doctor','SYSTEM','查询consult','详情','[7]','172.18.0.1','2026-10-04 13:13:38'),(376,3,'doctor','SYSTEM','删除consult','deleteTicket','[7]','172.18.0.1','2026-10-04 13:13:41'),(377,3,'doctor','SYSTEM','查询consult','tickets','[null,1,20]','172.18.0.1','2026-10-04 13:13:41'),(378,3,'doctor','LOGIN','登录: doctor','AuthController#login',NULL,'172.18.0.1','2026-10-04 13:19:16'),(379,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 13:19:16'),(380,3,'doctor','LOGIN','登录: doctor','AuthController#login',NULL,'172.18.0.1','2026-10-04 13:21:05'),(381,NULL,NULL,'SYSTEM','新增登录认证','登录',NULL,'172.18.0.1','2026-10-04 13:21:05');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'FIRST_AID' COMMENT 'FIRST_AID急救知识/SNAKE蛇种识别/HOSPITAL医院常识',
  `question_type` tinyint NOT NULL DEFAULT '1' COMMENT '1-单选 2-多选 3-判断',
  `question` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目内容',
  `option_a` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `option_b` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `option_c` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `option_d` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correct_answer` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '正确答案: A/B/C/D 或 AB/CD',
  `score` int NOT NULL DEFAULT '10' COMMENT '分值',
  `analysis` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '答案解析',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '0-禁用 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='急救知识题库';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (1,'FIRST_AID',1,'被毒蛇咬伤后，以下哪项是正确的急救第一步？','立即用嘴吸出毒液','立即用清水冲洗伤口','立即拨打120并保持冷静','立即用绳子绑住伤口上方止血','C',10,'保持冷静是最重要的！慌乱会加速血液循环，促进毒素扩散。应立即拨打急救电话，让伤者保持静止。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(2,'FIRST_AID',1,'被毒蛇咬伤后，伤口应该如何处理？','用嘴吸出毒液','用清水或肥皂水冲洗','用酒精消毒','用热水浸泡','B',10,'用大量清水或肥皂水冲洗伤口可以减少体表毒素。绝对不要用嘴吸！',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(3,'FIRST_AID',2,'以下哪些是神经毒毒蛇咬伤的典型症状？（多选）','伤口红肿疼痛剧烈','眼睑下垂、复视','呼吸困难、吞咽困难','伤口流血不止','BC',15,'神经毒主要攻击神经系统，表现为肌肉麻痹和呼吸/吞咽困难。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(4,'FIRST_AID',1,'被毒蛇咬伤后，伤者应该保持什么姿势？','平躺，伤口高于心脏','平躺，伤口低于心脏','坐着，双腿悬空','站立慢慢走动','B',10,'保持伤口低于心脏可以减缓毒液向心脏扩散的速度。绝对不要走动！',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(5,'FIRST_AID',3,'被毒蛇咬伤后，应该用冰敷在伤口上。','正确','错误','','','B',10,'错误！冰敷会导致血管收缩，反而可能使毒素在局部组织中浓度升高，加重组织坏死。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(6,'FIRST_AID',1,'急救时，是否应该给伤者喝水？','绝对不能','可以少量喝水','应该大量喝水稀释毒素','可以喝酒消毒','B',10,'可以少量喝水避免脱水，但不要大量饮水或喝酒——酒精会扩张血管加速毒素吸收。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(7,'SNAKE',1,'被咬伤时，如何判断是毒蛇还是无毒蛇？','看蛇的大小','看蛇的颜色','看伤口牙痕（两个深牙痕往往是毒蛇）','看蛇是不是主动攻击','C',10,'毒蛇通常有两个较深的毒牙痕，而无毒蛇是两排细密的牙痕。但最可靠的方法还是及时就医！',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(8,'SNAKE',1,'中国境内分布最广的有毒蛇种是？','眼镜蛇','银环蛇','蝮蛇','五步蛇','C',10,'蝮蛇是中国分布最广、数量最多的毒蛇，除了青藏高原外几乎遍布全国。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(9,'SNAKE',1,'银环蛇属于什么毒性类型？','血液毒','神经毒','混合毒','细胞毒','B',10,'银环蛇是典型的神经毒蛇，毒液中含有突触后神经毒素，中毒后可能在数小时内呼吸衰竭死亡。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(10,'SNAKE',1,'五步蛇（尖吻蝮）的典型特征是？','头部三角形，身上有灰白色菱斑','头部圆形，通体绿色','头大颈细，颈部可膨胀','通体黑色有白色环纹','A',10,'五步蛇头部呈明显三角形，身上有灰白色方胜（菱斑）图案，是血液毒毒蛇。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(11,'SNAKE',2,'以下哪些是有毒蛇？（多选）','王锦蛇','眼镜蛇','金环蛇','乌梢蛇','BC',15,'眼镜蛇和金环蛇都是毒蛇。王锦蛇和乌梢蛇都是无毒蛇，王锦蛇还是吃蛇的蛇！',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(12,'SNAKE',1,'竹叶青蛇属于什么毒性？','神经毒','血液毒','混合毒','细胞毒','C',10,'竹叶青是混合毒毒蛇，既有血液毒成分，也有少量神经毒成分。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(13,'HOSPITAL',1,'被毒蛇咬伤后，应该选择什么样的医院？','最近的社区医院就行','有血清储备的医院','大型综合医院最好','中医院，用草药治','B',10,'关键是要有对应血清的医院！没有血清，即使是三甲综合医院也无能为力。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(14,'HOSPITAL',1,'抗蛇毒血清注射的最佳时间是？','咬伤后1小时内','咬伤后2-4小时','咬伤后6-12小时','任何时候都一样','A',10,'越早越好！最好在1小时内使用。超过6小时，毒素已大量与组织结合，血清效果会大打折扣。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(15,'HOSPITAL',3,'抗蛇毒血清只有被咬伤后才能用，不能提前打。','正确','错误','','','A',10,'正确！目前没有针对蛇伤的常规预防接种。血清只能在被咬伤后使用。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(16,'HOSPITAL',1,'如果当地医院没有血清，最正确的做法是？','自己打车去外地','打120让急救中心协调','用土草药自己处理','在家里观察看看','B',10,'120急救中心有跨医院协调能力，可以联系附近有血清的医院并安排转院。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(17,'FIRST_AID',1,'被毒蛇咬伤后，以下哪种做法是错误的？','让伤者保持冷静','记录蛇的特征供医生参考','在伤口上方用止血带扎紧','移除伤者身上可能限制肿胀的物品','C',10,'不建议用止血带！研究表明止血带效果有限且可能加重局部组织损伤。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(18,'FIRST_AID',1,'蛇咬伤后出现呼吸困难，最可能是哪种毒液导致的？','血液毒','神经毒','细胞毒','以上都有可能','B',10,'呼吸困难是神经毒的典型表现！神经毒会麻痹呼吸肌，这是最危险的情况。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(19,'SNAKE',1,'蛇出没最多的时间通常是？','正午12点','清晨和傍晚','深夜','只有下雨后','B',10,'蛇是变温动物，清晨和傍晚温度适宜时最活跃。正午太热、深夜太冷，蛇都会躲起来。',1,'2026-09-28 20:32:33','2026-09-28 20:32:33'),(20,'HOSPITAL',1,'关于抗蛇毒血清，以下说法正确的是？','一种血清能治所有蛇','血清是万能的，打了就没事','血清可能引起过敏，需要皮试','血清常温保存就行','C',10,'血清是从马血清中提取的异种蛋白，约20%-30%的人会过敏，使用前必须皮试！',1,'2026-09-28 20:32:33','2026-09-28 20:32:33');
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `region` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT '0' COMMENT '0表示省',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `level` int NOT NULL COMMENT '1-省 2-市',
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地区字典';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `region`
--

LOCK TABLES `region` WRITE;
/*!40000 ALTER TABLE `region` DISABLE KEYS */;
INSERT INTO `region` VALUES (1,0,'广东省',1),(2,0,'上海市',1),(3,0,'北京市',1),(4,0,'四川省',1),(5,0,'云南省',1),(6,1,'广州市',2),(7,1,'深圳市',2),(8,1,'佛山市',2),(9,2,'上海市',2),(10,3,'北京市',2),(11,4,'成都市',2),(12,5,'昆明市',2);
/*!40000 ALTER TABLE `region` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `serum_stock`
--

DROP TABLE IF EXISTS `serum_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `serum_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hospital_id` bigint NOT NULL,
  `serum_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '抗银环蛇毒血清/抗五步蛇毒血清...',
  `quantity` int NOT NULL DEFAULT '0',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_hospital` (`hospital_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医院血清库存';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `serum_stock`
--

LOCK TABLES `serum_stock` WRITE;
/*!40000 ALTER TABLE `serum_stock` DISABLE KEYS */;
INSERT INTO `serum_stock` VALUES (1,1,'抗银环蛇毒血清',20,'2026-09-25 12:47:48'),(2,1,'抗五步蛇毒血清',15,'2026-09-25 12:47:48'),(3,1,'抗眼镜蛇毒血清',10,'2026-09-25 12:47:48'),(4,2,'抗银环蛇毒血清',30,'2026-09-25 12:47:48'),(5,2,'抗眼镜蛇毒血清',20,'2026-09-25 12:47:48'),(6,3,'抗银环蛇毒血清',15,'2026-09-25 12:47:48'),(7,3,'抗竹叶青毒血清',12,'2026-09-25 12:47:48'),(8,5,'抗银环蛇毒血清',8,'2026-09-25 12:47:48'),(9,6,'抗蝮蛇毒血清',25,'2026-09-25 12:47:48'),(10,8,'抗银环蛇毒血清',10,'2026-09-25 12:47:48'),(11,9,'抗银环蛇毒血清',35,'2026-09-25 12:47:48'),(12,9,'抗眼镜蛇毒血清',20,'2026-09-25 12:47:48'),(13,9,'抗五步蛇毒血清',18,'2026-09-25 12:47:48');
/*!40000 ALTER TABLE `serum_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `snake_image`
--

DROP TABLE IF EXISTS `snake_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `snake_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `snake_id` bigint NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_cover` int NOT NULL DEFAULT '0' COMMENT '0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_snake_id` (`snake_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='蛇种图库图片';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `snake_image`
--

LOCK TABLES `snake_image` WRITE;
/*!40000 ALTER TABLE `snake_image` DISABLE KEYS */;
INSERT INTO `snake_image` VALUES (1,1,'/uploads/snakes/01_silver_krait.jpg',1),(2,2,'/uploads/snakes/02_gold_krait.jpg',1),(3,3,'/uploads/snakes/03_cobra.jpg',1),(4,4,'/uploads/snakes/04_hundred_pacer.jpg',1),(5,5,'/uploads/snakes/05_bamboo_viper.jpg',1),(6,6,'/uploads/snakes/06_black_snake.jpg',1),(7,7,'/uploads/snakes/07_ratsnake.jpg',1);
/*!40000 ALTER TABLE `snake_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `snake_species`
--

DROP TABLE IF EXISTS `snake_species`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `snake_species` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `scientific_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拉丁学名',
  `is_poisonous` int NOT NULL COMMENT '0-无毒 1-有毒',
  `venom_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '神经毒/血液毒/混合毒',
  `head_shape` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头型',
  `pattern` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '花纹特征',
  `color` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '颜色特征',
  `tail_shape` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '尾部特征',
  `appearance_desc` text COLLATE utf8mb4_unicode_ci COMMENT '外观描述',
  `distribution` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分布地区',
  `symptoms` text COLLATE utf8mb4_unicode_ci COMMENT '被咬后特异性症状',
  `first_aid_specifics` text COLLATE utf8mb4_unicode_ci COMMENT '特异性处理要点',
  `status` int NOT NULL DEFAULT '2' COMMENT '0-草稿 1-待审 2-已发布 3-已下线',
  `audit_by` bigint DEFAULT NULL,
  `audit_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` int NOT NULL DEFAULT '0',
  `audit_status` int NOT NULL DEFAULT '1' COMMENT '0-草稿 1-待审 2-已发布 3-已驳回',
  PRIMARY KEY (`id`),
  KEY `idx_poisonous` (`is_poisonous`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='蛇种识别图库';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `snake_species`
--

LOCK TABLES `snake_species` WRITE;
/*!40000 ALTER TABLE `snake_species` DISABLE KEYS */;
INSERT INTO `snake_species` VALUES (1,'银环蛇','Bungarus multicinctus',1,'神经毒','椭圆形','黑白相间环纹','黑色+白色环纹','短粗','体背黑色，有30-50条白色窄环纹，腹面白色。全长1-1.5米。','长江以南各省，尤多见于华南、西南','咬伤后伤口不痛不痒，1-4小时后出现眼睑下垂、复视、四肢无力、吞咽困难，严重者呼吸衰竭。','神经毒作用快，必须立即送医。保持制动，禁止切开伤口。抗银环蛇血清特效。',1,1,'2026-09-25 12:47:47','2026-09-25 12:47:47',0,2),(2,'金环蛇','Bungarus fasciatus',1,'神经毒','椭圆形','黑黄相间环纹','黑色+黄色环纹','短粗','体背黑色，有20-30条黄色宽环纹，腹面白色。全长1-1.8米。','广东、广西、福建、云南、海南','与银环蛇类似，神经毒作用，伤口轻微疼痛。','同银环蛇，抗金环蛇血清。',1,1,'2026-09-25 12:47:47','2026-09-25 12:47:47',0,2),(3,'眼镜蛇','Naja atra',1,'混合毒','椭圆形可膨扁','颈部有眼镜状斑纹','黑褐色','圆柱状','颈部可膨扁如眼镜状，体长1-2米。激怒时前半身直立。','长江以南各省','咬伤后伤口红肿疼痛迅速扩大，伴头晕、眼花、呼吸困难。严重者组织坏死。','保持伤口低于心脏，禁止切开。抗眼镜蛇血清。',1,2,'2026-09-25 12:47:47','2026-09-25 12:47:47',0,2),(4,'五步蛇','Deinagkistrodon acutus',1,'血液毒','三角形','体背有大方形斑纹','棕褐色+深棕色方块','尖尾','头大呈三角形，吻端尖出。体长1.2-1.5米。','长江中下游及以南山区','伤口剧痛、出血不止、肿胀迅速蔓延，皮下出血、血尿。','必须保持制动，禁止切开。抗五步蛇血清。',1,2,'2026-09-25 12:47:47','2026-09-25 12:47:47',0,2),(5,'竹叶青','Trimeresurus stejnegeri',1,'血液毒','三角形','无明显斑纹','翠绿色','红色尾尖','通体翠绿色，尾尖红色或红褐色。体长0.6-0.9米。','南方竹林、山区','咬伤后局部剧痛、肿胀明显，一般较少全身症状。','保持制动，局部冷敷。抗蝮蛇毒血清有效。',1,2,'2026-09-25 12:47:47','2026-09-25 12:47:47',0,2),(6,'乌梢蛇','Zaocys dhumnades',0,NULL,'椭圆形','体背有4条黑纵纹','绿褐色','渐细','全长1.5-2.5米，是常见大型无毒蛇。','全国大部分地区','无毒蛇咬伤，伤口为两排整齐牙印，无中毒症状。','伤口清洗消毒，防感染即可。',1,1,'2026-09-25 12:47:47','2026-09-25 12:47:47',0,2),(7,'王锦蛇','Elaphe carinata',0,NULL,'椭圆形','头部有\"王\"字斑纹','黄褐色+黑色斑纹','渐细','全长1.5-2米，无毒但性情凶猛。','长江以南各省','无毒，咬伤后伤口消毒即可。','局部清洗包扎。',1,1,'2026-09-25 12:47:47','2026-09-25 12:47:47',0,2);
/*!40000 ALTER TABLE `snake_species` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'BCrypt加密密码',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `role_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SYS_ADMIN/CONTENT_ADMIN/MEDICAL_ADVISOR',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `status` int NOT NULL DEFAULT '1' COMMENT '0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_type` (`role_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台管理用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'sysadmin','$2a$10$/mHAZJWbldWjWXe3ws2TLuufTj56ocrP91/ociWxI4bNexLeDbZdG','系统管理员','SYS_ADMIN','13800000001',1,'2026-09-25 12:47:47','2026-09-25 12:47:47',0),(2,'contentadmin','$2a$10$/mHAZJWbldWjWXe3ws2TLuufTj56ocrP91/ociWxI4bNexLeDbZdG','内容运营','CONTENT_ADMIN','13800000002',1,'2026-09-25 12:47:47','2026-09-25 12:47:47',0),(3,'doctor','$2a$10$/mHAZJWbldWjWXe3ws2TLuufTj56ocrP91/ociWxI4bNexLeDbZdG','张医生','MEDICAL_ADVISOR','13800000003',1,'2026-09-25 12:47:47','2026-10-04 12:17:20',0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_points_log`
--

DROP TABLE IF EXISTS `user_points_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_points_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `event_type` varchar(30) NOT NULL,
  `points` int NOT NULL,
  `ref_id` bigint DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_points_log`
--

LOCK TABLES `user_points_log` WRITE;
/*!40000 ALTER TABLE `user_points_log` DISABLE KEYS */;
INSERT INTO `user_points_log` VALUES (1,1,'snake_view',2,1,'学习蛇种知识','2026-10-02 22:45:51'),(2,2,'snake_view',2,3,'学习蛇种知识','2026-10-02 22:49:16'),(3,1,'snake_view',2,2,'学习蛇种知识','2026-10-02 23:53:35'),(4,4,'quiz_submit',30,7,'完成在线考试 综合','2026-10-03 00:03:27'),(5,4,'snake_view',2,3,'学习蛇种知识','2026-10-03 00:07:06'),(6,4,'hospital_view',1,NULL,'查看附近医院','2026-10-03 00:07:07'),(7,4,'snake_view',2,1,'学习蛇种知识','2026-10-03 00:08:21'),(8,4,'hospital_view',1,NULL,'查看附近医院','2026-10-03 23:47:49'),(9,4,'snake_view',2,1,'学习蛇种知识','2026-10-04 16:12:48'),(10,4,'snake_view',2,4,'学习蛇种知识','2026-10-04 21:54:34'),(11,4,'snake_view',2,6,'学习蛇种知识','2026-10-04 21:55:29');
/*!40000 ALTER TABLE `user_points_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_quiz`
--

DROP TABLE IF EXISTS `user_quiz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_quiz` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `user_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_score` int NOT NULL DEFAULT '0' COMMENT '总分',
  `score` int NOT NULL DEFAULT '0' COMMENT '得分',
  `correct_count` int NOT NULL DEFAULT '0' COMMENT '答对数量',
  `total_count` int NOT NULL DEFAULT '0' COMMENT '题目总数',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '考试类别',
  `duration_sec` int DEFAULT NULL COMMENT '用时（秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户考试记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_quiz`
--

LOCK TABLES `user_quiz` WRITE;
/*!40000 ALTER TABLE `user_quiz` DISABLE KEYS */;
INSERT INTO `user_quiz` VALUES (6,NULL,'匿名用户',50,50,5,5,NULL,38,'2026-10-02 23:51:30'),(7,4,'18150909556',55,40,4,5,NULL,277,'2026-10-03 00:03:27');
/*!40000 ALTER TABLE `user_quiz` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_quiz_detail`
--

DROP TABLE IF EXISTS `user_quiz_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_quiz_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quiz_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `user_answer` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correct_answer` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_correct` tinyint NOT NULL DEFAULT '0',
  `score` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_quiz` (`quiz_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试答题明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_quiz_detail`
--

LOCK TABLES `user_quiz_detail` WRITE;
/*!40000 ALTER TABLE `user_quiz_detail` DISABLE KEYS */;
INSERT INTO `user_quiz_detail` VALUES (1,1,1,'A','C',0,0),(2,1,2,'B','B',1,10),(3,2,1,'D','C',0,0),(4,2,2,'B','B',1,10),(5,2,3,'ABC','BC',0,0),(6,2,5,'A','B',0,0),(7,2,18,'D','B',0,0),(8,3,1,'C','C',1,10),(9,3,4,'B','B',1,10),(10,3,5,'A','B',0,0),(11,3,6,'B','B',1,10),(12,3,17,'D','C',0,0),(13,4,1,'C','C',1,10),(14,4,2,'B','B',1,10),(15,4,3,'ABC','BC',0,0),(16,4,4,'B','B',1,10),(17,4,17,'C','C',1,10),(18,5,1,'C','C',1,10),(19,5,2,'B','B',1,10),(20,5,4,'B','B',1,10),(21,5,5,'B','B',1,10),(22,5,6,'B','B',1,10),(23,6,1,'C','C',1,10),(24,6,2,'B','B',1,10),(25,6,4,'B','B',1,10),(26,6,6,'B','B',1,10),(27,6,17,'C','C',1,10),(28,7,1,'C','C',1,10),(29,7,2,'B','B',1,10),(30,7,3,'ABCD','BC',0,0),(31,7,5,'B','B',1,10),(32,7,17,'C','C',1,10);
/*!40000 ALTER TABLE `user_quiz_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'snake'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-05 22:39:54
