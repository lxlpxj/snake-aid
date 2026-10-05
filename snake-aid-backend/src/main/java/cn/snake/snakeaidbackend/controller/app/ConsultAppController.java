package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.AiChatHistory;
import cn.snake.snakeaidbackend.entity.ConsultationMessage;
import cn.snake.snakeaidbackend.service.AiAssistantService;
import cn.snake.snakeaidbackend.service.TicketService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** App 端 - 咨询中心（AI + 人工） */
@RestController
@RequestMapping("/app")
@Tag(name = "App端-咨询中心")
public class ConsultAppController {

    @Resource
    private AiAssistantService aiService;
    @Resource
    private TicketService ticketService;

    private Long currentUserId() {
        try {
            Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return p instanceof Long ? (Long) p : null;
        } catch (Exception e) { return null; }
    }

    // ============ 🤖 AI 助手 ============

    @Operation(summary = "AI 对话（蛇伤急救智能助手）")
    @PostMapping("/ai/chat")
    public Result<Map<String, Object>> aiChat(@RequestBody Map<String, String> body) {
        String msg = body.get("message");
        if (msg == null || msg.isBlank()) return Result.fail("消息不能为空");
        return Result.success(aiService.chat(currentUserId(), msg));
    }

    @Operation(summary = "AI 对话历史")
    @GetMapping("/ai/history")
    public Result<List<AiChatHistory>> aiHistory() {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.success(aiService.getHistory(uid));
    }

    @Operation(summary = "清空 AI 对话历史")
    @DeleteMapping("/ai/history")
    public Result<String> aiClear() {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        aiService.clearHistory(uid);
        return Result.success("已清空");
    }

    // ============ 👩‍⚕️ 人工咨询 ============

    @Operation(summary = "创建人工咨询工单（发第一条消息）")
    @PostMapping("/consult/ticket")
    public Result<Map<String, Object>> createTicket(@RequestBody Map<String, String> body) {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "请先登录");
        String title = body.get("title");
        String category = body.get("category");
        String message = body.get("message");
        if (title == null || title.isBlank()) return Result.fail("请填写咨询标题");
        if (message == null || message.isBlank()) return Result.fail("请输入咨询内容");
        return Result.success(ticketService.createTicket(uid, title, category, message));
    }

    @Operation(summary = "我的工单列表")
    @GetMapping("/consult/tickets")
    public Result<List<Map<String, Object>>> myTickets() {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.success(ticketService.listByUser(uid));
    }

    @Operation(summary = "工单详情（带全部消息）")
    @GetMapping("/consult/ticket/{id}")
    public Result<Map<String, Object>> ticketDetail(@PathVariable Long id) {
        return Result.success(ticketService.getDetail(id));
    }

    @Operation(summary = "给工单发消息（用户发）")
    @PostMapping("/consult/ticket/{id}/reply")
    public Result<ConsultationMessage> ticketReply(@PathVariable Long id,
                                                    @RequestBody Map<String, String> body) {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        String content = body.get("content");
        if (content == null || content.isBlank()) return Result.fail("消息内容不能为空");
        return Result.success(ticketService.userSend(id, uid, content));
    }

    @Operation(summary = "删除自己工单里的某条消息")
    @DeleteMapping("/consult/message/{msgId}")
    public Result<String> deleteMessage(@PathVariable Long msgId) {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        ticketService.deleteMessage(msgId, uid, false);
        return Result.success("已删除");
    }

    @Operation(summary = "删除自己的整个工单（含所有消息）")
    @DeleteMapping("/consult/ticket/{id}")
    public Result<String> deleteTicket(@PathVariable Long id) {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        ticketService.deleteTicket(id, uid, false);
        return Result.success("已删除");
    }
}
