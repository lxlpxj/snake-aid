package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.ConsultationMessage;
import cn.snake.snakeaidbackend.service.TicketService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 管理后台 - 医疗顾问咨询工作台 */
@RestController
@RequestMapping("/admin/consult")
@Tag(name = "Admin端-咨询工作台")
public class ConsultAdminController {

    @Resource
    private TicketService ticketService;
    @Resource
    private JdbcTemplate jdbc;

    @Operation(summary = "全部工单列表（所有顾问都能看）")
    @GetMapping("/tickets")
    public Result<IPage<Map<String, Object>>> tickets(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        return Result.success(ticketService.listAll(status, current, size));
    }

    @Operation(summary = "顾问自己的工单（按 assignee 过滤）")
    @GetMapping("/my-tickets")
    public Result<IPage<Map<String, Object>>> myTickets(
            @RequestParam Long advisorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        return Result.success(ticketService.listForAdvisor(advisorId, status, current, size));
    }

    @Operation(summary = "工单详情")
    @GetMapping("/ticket/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(ticketService.getDetail(id));
    }

    @Operation(summary = "顾问回复工单")
    @PostMapping("/ticket/{id}/reply")
    public Result<ConsultationMessage> reply(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        // 从 SecurityContext 拿当前登录 admin 的 userId（JwtFilter 已放入 principal）
        Long advisorId = null;
        try {
            Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (p instanceof Long) advisorId = (Long) p;
        } catch (Exception ignored) {}
        String content = (String) body.get("content");
        if (content == null || content.isBlank()) return Result.fail("回复内容不能为空");
        return Result.success(ticketService.advisorReply(id, advisorId, content));
    }

    @Operation(summary = "关闭工单（标记已解决）")
    @PostMapping("/ticket/{id}/close")
    public Result<String> close(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        Long advisorId = null;
        try {
            Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (p instanceof Long) advisorId = (Long) p;
        } catch (Exception ignored) {}
        ticketService.closeTicket(id, advisorId);
        return Result.success("已关闭");
    }

    @Operation(summary = "更新工单状态")
    @PutMapping("/ticket/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        ticketService.updateStatus(id, status);
        return Result.success("ok");
    }

    @Operation(summary = "待处理工单数量（Dashboard 用）")
    @GetMapping("/pending-count")
    public Result<Map<String, Object>> pendingCount() {
        Map<String, Object> r = new java.util.HashMap<>();
        try {
            r.put("pending", jdbc.queryForObject(
                    "SELECT COUNT(*) FROM consultation_ticket WHERE status='PENDING'", Integer.class));
            r.put("inProgress", jdbc.queryForObject(
                    "SELECT COUNT(*) FROM consultation_ticket WHERE status='IN_PROGRESS'", Integer.class));
            r.put("resolved", jdbc.queryForObject(
                    "SELECT COUNT(*) FROM consultation_ticket WHERE status='RESOLVED'", Integer.class));
            r.put("total", jdbc.queryForObject(
                    "SELECT COUNT(*) FROM consultation_ticket", Integer.class));
        } catch (Exception e) {
            r.put("error", e.getMessage());
        }
        return Result.success(r);
    }

    @Operation(summary = "在线医疗顾问列表")
    @GetMapping("/advisors")
    public Result<List<Map<String, Object>>> advisors() {
        String sql = "SELECT id, username, real_name, phone FROM sys_user " +
                     "WHERE role_type='MEDICAL_ADVISOR' AND status=1";
        return Result.success(jdbc.queryForList(sql));
    }

    @Operation(summary = "管理员删除任意消息")
    @DeleteMapping("/message/{msgId}")
    public Result<String> deleteMessage(@PathVariable Long msgId) {
        ticketService.deleteMessage(msgId, null, true);
        return Result.success("已删除");
    }

    @Operation(summary = "管理员删除任意工单（含所有消息）")
    @DeleteMapping("/ticket/{id}")
    public Result<String> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id, null, true);
        return Result.success("已删除");
    }
}
