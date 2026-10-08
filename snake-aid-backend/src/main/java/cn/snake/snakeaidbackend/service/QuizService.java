package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.Question;
import cn.snake.snakeaidbackend.entity.UserQuiz;
import cn.snake.snakeaidbackend.entity.UserQuizDetail;
import cn.snake.snakeaidbackend.mapper.QuestionMapper;
import cn.snake.snakeaidbackend.mapper.UserQuizDetailMapper;
import cn.snake.snakeaidbackend.mapper.UserQuizMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private UserQuizMapper userQuizMapper;
    @Resource
    private UserQuizDetailMapper detailMapper;

    // ===== 后台：题库管理 =====
    public PageResult<Question> pageQuestions(long current, long size, String category, Integer type, String keyword, Integer status) {
        Page<Question> page = new Page<>(current, size);
        LambdaQueryWrapper<Question> w = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) w.eq(Question::getCategory, category);
        if (type != null) w.eq(Question::getQuestionType, type);
        if (status != null) w.eq(Question::getStatus, status);
        if (keyword != null && !keyword.isEmpty()) w.like(Question::getQuestion, keyword);
        w.orderByDesc(Question::getId);
        Page<Question> result = questionMapper.selectPage(page, w);
        return PageResult.of(result);
    }

    public Question getQuestion(Long id) {
        return questionMapper.selectById(id);
    }

    public Question saveQuestion(Question q) {
        if (q.getId() == null) {
            q.setCreateTime(LocalDateTime.now());
            questionMapper.insert(q);
        } else {
            questionMapper.updateById(q);
        }
        return q;
    }

    public boolean deleteQuestion(Long id) {
        return questionMapper.deleteById(id) > 0;
    }

    public boolean toggleStatus(Long id) {
        Question q = questionMapper.selectById(id);
        if (q == null) return false;
        q.setStatus(q.getStatus() == 1 ? 0 : 1);
        return questionMapper.updateById(q) > 0;
    }

    // ===== App 端：考试 =====

    /** 随机抽题 */
    public List<Question> pickQuestions(String category, Integer count) {
        LambdaQueryWrapper<Question> w = new LambdaQueryWrapper<>();
        w.eq(Question::getStatus, 1);
        if (category != null && !category.isEmpty()) w.eq(Question::getCategory, category);
        w.orderByDesc(Question::getId);
        List<Question> all = questionMapper.selectList(w);

        // 随机打乱取 count 个
        Collections.shuffle(all);
        int n = count != null ? count : Math.min(10, all.size());
        List<Question> picked = all.subList(0, Math.min(n, all.size()));
        // LEARN / PRACTICE 是学习场景，保留正确答案让前端即时反馈；
        // 其他 category（正式考试场景）清除正确答案防作弊
        boolean keepAnswer = "LEARN".equals(category) || "PRACTICE".equals(category);
        if (!keepAnswer) {
            picked.forEach(q -> q.setCorrectAnswer(null));
        }
        return picked;
    }

    /** 提交考试 + 自动判分 */
    @Transactional
    public Map<String, Object> submitQuiz(Long userId, String userName, String category, Integer durationSec, Map<Long, String> answers) {
        // 1. 取出所有题（含正确答案）
        List<Long> qids = new ArrayList<>(answers.keySet());
        List<Question> questionList = questionMapper.selectBatchIds(qids);
        Map<Long, Question> qMap = questionList.stream().collect(Collectors.toMap(Question::getId, q -> q));

        // 2. 判分
        int totalScore = 0;
        int score = 0;
        int correctCount = 0;
        List<UserQuizDetail> details = new ArrayList<>();

        for (Question q : questionList) {
            String userAns = answers.get(q.getId());
            boolean correct = isAnswerCorrect(q.getCorrectAnswer(), userAns, q.getQuestionType());
            if (correct) correctCount++;
            int earned = correct ? q.getScore() : 0;
            totalScore += q.getScore();
            score += earned;

            UserQuizDetail d = new UserQuizDetail();
            d.setQuestionId(q.getId());
            d.setUserAnswer(userAns);
            d.setCorrectAnswer(q.getCorrectAnswer());
            d.setIsCorrect(correct ? 1 : 0);
            d.setScore(earned);
            details.add(d);
        }

        // 3. 保存考试记录
        UserQuiz quiz = new UserQuiz();
        quiz.setUserId(userId);
        quiz.setUserName(userName);
        quiz.setTotalScore(totalScore);
        quiz.setScore(score);
        quiz.setCorrectCount(correctCount);
        quiz.setTotalCount(questionList.size());
        quiz.setCategory(category);
        quiz.setDurationSec(durationSec);
        quiz.setCreateTime(LocalDateTime.now());
        userQuizMapper.insert(quiz);

        // 4. 保存明细
        for (UserQuizDetail d : details) {
            d.setQuizId(quiz.getId());
            detailMapper.insert(d);
        }

        // 5. 返回结果（带每题解析）
        Map<String, Object> result = new HashMap<>();
        result.put("quizId", quiz.getId());
        result.put("score", score);
        result.put("totalScore", totalScore);
        result.put("correctCount", correctCount);
        result.put("totalCount", questionList.size());
        result.put("accuracy", questionList.size() > 0 ? Math.round(correctCount * 100.0 / questionList.size()) : 0);

        // 详情列表
        List<Map<String, Object>> detailList = new ArrayList<>();
        for (UserQuizDetail d : details) {
            Question q = qMap.get(d.getQuestionId());
            Map<String, Object> item = new HashMap<>();
            item.put("questionId", q.getId());
            item.put("question", q.getQuestion());
            item.put("questionType", q.getQuestionType());
            item.put("optionA", q.getOptionA());
            item.put("optionB", q.getOptionB());
            item.put("optionC", q.getOptionC());
            item.put("optionD", q.getOptionD());
            item.put("correctAnswer", d.getCorrectAnswer());
            item.put("userAnswer", d.getUserAnswer());
            item.put("isCorrect", d.getIsCorrect());
            item.put("analysis", q.getAnalysis());
            item.put("score", q.getScore());
            detailList.add(item);
        }
        result.put("details", detailList);
        return result;
    }

    private boolean isAnswerCorrect(String correct, String user, Integer type) {
        if (correct == null || user == null) return false;
        if (type == null || type == 1 || type == 3) {
            // 单选 / 判断：精确匹配
            return correct.trim().equalsIgnoreCase(user.trim());
        } else {
            // 多选：排序后比较
            char[] c = correct.trim().toUpperCase().toCharArray();
            char[] u = user.trim().toUpperCase().toCharArray();
            Arrays.sort(c);
            Arrays.sort(u);
            return new String(c).equals(new String(u));
        }
    }

    public List<Question> getQuestionsByIds(List<Long> ids) {
        return questionMapper.selectBatchIds(ids);
    }

    // ===== App 端：我的考试记录 =====
    public List<UserQuiz> listUserQuizzes(Long userId) {
        LambdaQueryWrapper<UserQuiz> w = new LambdaQueryWrapper<>();
        w.eq(UserQuiz::getUserId, userId);
        w.orderByDesc(UserQuiz::getCreateTime);
        return userQuizMapper.selectList(w);
    }

    public Map<String, Object> getQuizDetail(Long quizId) {
        UserQuiz quiz = userQuizMapper.selectById(quizId);
        List<UserQuizDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<UserQuizDetail>().eq(UserQuizDetail::getQuizId, quizId)
        );
        List<Long> qids = details.stream().map(UserQuizDetail::getQuestionId).toList();
        Map<Long, Question> qMap = qids.isEmpty() ? new HashMap<>() : questionMapper.selectBatchIds(qids).stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        List<Map<String, Object>> detailList = details.stream().map(d -> {
            Question q = qMap.get(d.getQuestionId());
            Map<String, Object> item = new HashMap<>();
            item.put("questionId", q != null ? q.getId() : d.getQuestionId());
            item.put("question", q != null ? q.getQuestion() : "题目已删除");
            item.put("questionType", q != null ? q.getQuestionType() : 1);
            item.put("optionA", q != null ? q.getOptionA() : null);
            item.put("optionB", q != null ? q.getOptionB() : null);
            item.put("optionC", q != null ? q.getOptionC() : null);
            item.put("optionD", q != null ? q.getOptionD() : null);
            item.put("correctAnswer", d.getCorrectAnswer());
            item.put("userAnswer", d.getUserAnswer());
            item.put("isCorrect", d.getIsCorrect());
            item.put("analysis", q != null ? q.getAnalysis() : null);
            item.put("score", d.getScore());
            return item;
        }).toList();

        Map<String, Object> result = new HashMap<>();
        if (quiz != null) {
            Map<String, Object> head = new HashMap<>();
            head.put("id", quiz.getId());
            head.put("score", quiz.getScore());
            head.put("totalScore", quiz.getTotalScore());
            head.put("correctCount", quiz.getCorrectCount());
            head.put("totalCount", quiz.getTotalCount());
            head.put("durationSec", quiz.getDurationSec());
            head.put("category", quiz.getCategory());
            head.put("createTime", quiz.getCreateTime());
            head.put("accuracy", quiz.getTotalCount() != null && quiz.getTotalCount() > 0
                    ? Math.round(quiz.getCorrectCount() * 100.0 / quiz.getTotalCount()) : 0);
            result.put("quiz", head);
        }
        result.put("details", detailList);
        return result;
    }

    // ===== 后台：考试记录 =====
    public PageResult<UserQuiz> pageQuizzes(long current, long size, String category) {
        Page<UserQuiz> page = new Page<>(current, size);
        LambdaQueryWrapper<UserQuiz> w = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) w.eq(UserQuiz::getCategory, category);
        w.orderByDesc(UserQuiz::getCreateTime);
        Page<UserQuiz> result = userQuizMapper.selectPage(page, w);
        return PageResult.of(result);
    }

    // 题库统计
    public Map<String, Object> getQuestionStats() {
        Long total = questionMapper.selectCount(null);
        LambdaQueryWrapper<Question> w = new LambdaQueryWrapper<>();
        w.eq(Question::getStatus, 1);
        Long enabled = questionMapper.selectCount(w);

        // 按类别统计
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("enabled", enabled);
        stats.put("disabled", total - enabled);

        List<Map<String, Object>> byCategory = new ArrayList<>();
        String[] cats = {"FIRST_AID", "SNAKE", "HOSPITAL", "LEARN", "PRACTICE"};
        for (String c : cats) {
            Map<String, Object> m = new HashMap<>();
            m.put("category", c);
            m.put("count", questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getCategory, c)));
            byCategory.add(m);
        }
        stats.put("byCategory", byCategory);
        return stats;
    }
}
