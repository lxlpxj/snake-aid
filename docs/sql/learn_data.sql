-- 1. snake_species 加字段
ALTER TABLE snake_species ADD COLUMN memory_tip VARCHAR(500) COMMENT '保命记忆口诀' AFTER venom_type;
ALTER TABLE snake_species ADD COLUMN tail_shape VARCHAR(100) COMMENT '尾部特征' AFTER memory_tip;

-- 2. 12 条蛇补 memory_tip 和 tail_shape
UPDATE snake_species SET memory_tip='银黑两色环,夜晚会发光,极毒!', tail_shape='细尖尾' WHERE id=1;
UPDATE snake_species SET memory_tip='黄黑两色环,和银环蛇亲戚', tail_shape='细尖尾' WHERE id=2;
UPDATE snake_species SET memory_tip='会竖起展开颈部像眼镜!', tail_shape='中等尾' WHERE id=3;
UPDATE snake_species SET memory_tip='三角头+大块斑+尖吻,极毒!', tail_shape='细短尾' WHERE id=4;
UPDATE snake_species SET memory_tip='翠绿色小蛇,三角头,常挂树枝!', tail_shape='细短尾' WHERE id=5;
UPDATE snake_species SET memory_tip='全国最常见毒蛇!三角头+圆斑', tail_shape='短尾' WHERE id=6;
UPDATE snake_species SET memory_tip='头大如烙铁,X/Y花纹', tail_shape='细短尾' WHERE id=7;
UPDATE snake_species SET memory_tip='圆斑+三角头,毒性剧烈!', tail_shape='细尾' WHERE id=8;
UPDATE snake_species SET memory_tip='海里的蛇!尾巴侧扁像桨!', tail_shape='侧扁桨状' WHERE id=9;
UPDATE snake_species SET memory_tip='极危!世界最大烙铁头,尾部白色!', tail_shape='尾部白色' WHERE id=10;
UPDATE snake_species SET memory_tip='常见无毒蛇,椭圆头,性情温和', tail_shape='细尖尾' WHERE id=11;
UPDATE snake_species SET memory_tip='菜花蛇!体型大,无毒还吃毒蛇!', tail_shape='中等尾' WHERE id=12;

-- 3. 清空旧题目再塞 20 道
DELETE FROM question;

INSERT INTO question (category, question_type, question, option_a, option_b, option_c, option_d, correct_answer, score, analysis, status) VALUES
('LEARN',1,'毒蛇最典型的头部形状是?','圆形','三角形','正方形','扁形','B',10,'三角头是毒蛇最显著的标志!',1),
('LEARN',1,'银环蛇和金环蛇属于什么毒?','血液毒','神经毒','混合毒','无毒','B',10,'银环金环都是神经毒之王!咬后不疼但会窒息!',1),
('LEARN',1,'被毒蛇咬伤后应该?','立刻跑回家','保持冷静制动伤肢','用嘴吸毒','敷草药','B',10,'制动能减慢毒液扩散!',1),
('LEARN',1,'哪个是无毒蛇?','五步蛇','竹叶青','乌梢蛇','圆斑蝰','C',10,'乌梢蛇是典型无毒蛇,椭圆头!',1),
('LEARN',1,'蛇伤急救三大禁忌不包括?','不切伤口','不吸毒','不敷草药','不吃鸡蛋','D',10,'不吃鸡蛋不是禁忌!三大禁忌是不切不吸不敷!',1),
('LEARN',1,'竹叶青蛇的典型颜色是?','黑色','绿色','红色','白色','B',10,'竹叶青=翠绿色!挂在树枝上像嫩叶!',1),
('LEARN',1,'海蛇的尾巴有什么特征?','尖细','侧扁像桨','分叉','短粗','B',10,'海蛇尾巴侧扁是桨状,适合游泳!',1),
('LEARN',1,'下面哪种不是毒蛇?','眼镜蛇','王锦蛇','烙铁头','莽山烙铁头','B',10,'王锦蛇=菜花蛇!无毒!还吃毒蛇!',1),
('LEARN',1,'五步蛇为什么叫五步蛇?','走五步就死','毒性剧烈','走五步中毒','身长五步','B',10,'五步蛇毒性剧烈!不是真走五步就死!',1),
('LEARN',1,'被银环蛇咬后什么症状?','伤口剧痛','伤口不疼但会呼吸困难','立刻昏迷','伤口出血不止','B',10,'银环蛇神经毒!咬后不疼,1-4小时后呼吸困难窒息!',1),
('PRACTICE',1,'菜园看到翠绿色三角头小蛇挂菜叶上怎么办?','无毒抓来泡酒','有毒!立刻远离报120','用棍子打死','拍照发朋友圈','B',10,'翠绿色+三角头=竹叶青!剧毒血液毒!',1),
('PRACTICE',1,'晚上走路踩到黑白环纹黑蛇?','无毒没事','有毒!神经毒!立刻送医','用嘴吸','敷草药','B',10,'银环蛇!神经毒之王!必须送医院!',1),
('PRACTICE',1,'夏天田埂上棕褐色三角头有圆斑的蛇?','无毒菜花蛇','有毒!保持冷静制动伤肢','赶紧跑回家','切开伤口排毒','B',10,'短尾蝮!全国最常见毒蛇!别跑!制动!',1),
('PRACTICE',1,'河边游泳被咬,伤口不痛肌肉疼,尾巴像桨?','无毒水蛇','海蛇!神经毒!立刻上岸报120','冰敷止痛','继续游回家','B',10,'海蛇!剧毒神经毒!尾巴侧扁是标志!',1),
('PRACTICE',1,'看到大蛇椭圆头黄色花斑?','有毒三角头','无毒菜花蛇!安全走开','剧毒开枪','抓来炖汤','B',10,'王锦蛇=菜花蛇!椭圆头=无毒标志!',1),
('PRACTICE',1,'野外被蛇咬,哪种做法正确?','用嘴吸伤口','切开伤口排毒','保持冷静制动伤肢','敷草药止血','C',10,'三大禁忌!不切不吸不敷!制动是对的!',1),
('PRACTICE',1,'发现蛇在你家门口怎么办?','拿棍子打死','慢慢退开,不要惊扰它','直接冲过去','用热水浇','B',10,'蛇怕人!慢慢退开就好!不要惊扰!',1),
('PRACTICE',1,'蛇咬伤后应该喝什么?','酒精消毒水','大量清水','浓茶','糖水','B',10,'多喝水帮助代谢!别碰酒精!',1),
('PRACTICE',1,'抗蛇毒血清怎么用?','口服','伤口外敷','静脉注射','涂在伤口上','C',10,'血清必须静脉注射!而且越早越好!',1),
('PRACTICE',1,'被毒蛇咬后多久送医合适?','12小时内','6小时内','越快越好!1-2小时内最佳','24小时内','C',10,'越早越好!1-2小时内注射血清效果最好!',1);
