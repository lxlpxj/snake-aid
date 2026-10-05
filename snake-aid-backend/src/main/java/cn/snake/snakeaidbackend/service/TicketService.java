package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.entity.AppUser;
import cn.snake.snakeaidbackend.entity.ConsultationMessage;
import cn.snake.snakeaidbackend.entity.ConsultationTicket;
import cn.snake.snakeaidbackend.entity.SysUser;
import cn.snake.snakeaidbackend.mapper.AppUserMapper;
import cn.snake.snakeaidbackend.mapper.ConsultationMessageMapper;
import cn.snake.snakeaidbackend.mapper.ConsultationTicketMapper;
import cn.snake.snakeaidbackend.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 人工咨询工单服务 */
@Service
public class TicketService {

    @Resource
    private ConsultationTicketMapper ticketMapper;
    @Resource
    private ConsultationMessageMapper messageMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private AppUserMapper appUserMapper;

    // ===== 用户端 =====

    @Transactional
    public Map<String, Object> createTicket(Long userId, String title, String category, String firstMessage) {
        ConsultationTicket t = new ConsultationTicket();
        t.setUserId(userId);
        t.setTitle(title);
        t.setCategory(category != null ? category : "蛇伤急救");
        t.setStatus("PENDING");
        t.setLastMessage(firstMessage);
        t.setLastTime(LocalDateTime.now());
        ticketMapper.insert(t);

        // 自动指派第一个在线的医疗顾问（保持 PENDING，等顾问点"开始处理"才变 IN_PROGRESS）
        SysUser advisor = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getRoleType, "MEDICAL_ADVISOR")
                        .eq(SysUser::getStatus, 1)
                        .last("LIMIT 1")
        );
        if (advisor != null) {
            t.setAssigneeId(advisor.getId());
            // 不自动改状态，保持 PENDING
            ticketMapper.updateById(t);
        }

        addMessage(t.getId(), "USER", userId, firstMessage);
        return buildTicket(t);
    }

    public List<Map<String, Object>> listByUser(Long userId) {
        List<ConsultationTicket> tickets = ticketMapper.selectList(
                new LambdaQueryWrapper<ConsultationTicket>()
                        .eq(ConsultationTicket::getUserId, userId)
                        .orderByDesc(ConsultationTicket::getLastTime)
        );
        return tickets.stream().map(this::buildTicket).toList();
    }

    @Transactional
    public ConsultationMessage userSend(Long ticketId, Long userId, String content) {
        ConsultationMessage msg = addMessage(ticketId, "USER", userId, content);
        updateTicketLast(ticketId, content);
        return msg;
    }

    // ===== 顾问/管理端 =====

    public IPage<Map<String, Object>> listForAdvisor(Long advisorId, String status, long current, long size) {
        LambdaQueryWrapper<ConsultationTicket> w = new LambdaQueryWrapper<>();
        if (advisorId != null) w.eq(ConsultationTicket::getAssigneeId, advisorId);
        if (status != null && !status.isBlank()) w.eq(ConsultationTicket::getStatus, status);
        w.orderByDesc(ConsultationTicket::getLastTime);

        IPage<ConsultationTicket> page = ticketMapper.selectPage(new Page<>(current, size), w);
        return page.convert(this::buildTicket);
    }

    public IPage<Map<String, Object>> listAll(String status, long current, long size) {
        LambdaQueryWrapper<ConsultationTicket> w = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) w.eq(ConsultationTicket::getStatus, status);
        w.orderByDesc(ConsultationTicket::getCreateTime);
        IPage<ConsultationTicket> page = ticketMapper.selectPage(new Page<>(current, size), w);
        return page.convert(this::buildTicket);
    }

    @Transactional
    public ConsultationMessage advisorReply(Long ticketId, Long advisorId, String content) {
        ConsultationMessage msg = addMessage(ticketId, "ADMIN", advisorId, content);
        ConsultationTicket t = ticketMapper.selectById(ticketId);
        if (t != null) {
            t.setStatus("IN_PROGRESS");
            t.setLastMessage(content);
            t.setLastTime(LocalDateTime.now());
            ticketMapper.updateById(t);
        }
        return msg;
    }

    public void updateStatus(Long ticketId, String status) {
        ConsultationTicket t = ticketMapper.selectById(ticketId);
        if (t == null) throw new RuntimeException("工单不存在");
        t.setStatus(status);
        ticketMapper.updateById(t);
    }

    public void closeTicket(Long ticketId, Long advisorId) {
        ConsultationTicket t = ticketMapper.selectById(ticketId);
        if (t != null) {
            t.setStatus("RESOLVED");
            ticketMapper.updateById(t);
        }
    }

    /** 删除单条消息：isAdmin=true 随便删；否则只能删自己发的 */
    @Transactional
    public void deleteMessage(Long msgId, Long userId, boolean isAdmin) {
        ConsultationMessage m = messageMapper.selectById(msgId);
        if (m == null) throw new RuntimeException("消息不存在");
        if (!isAdmin && m.getSenderId() != null && !m.getSenderId().equals(userId)) {
            throw new RuntimeException("无权删除此消息");
        }
        messageMapper.deleteById(msgId);
    }

    /** 删除整个工单（先删所有消息，再删工单）：isAdmin=true 随便删；否则只能删自己的工单 */
    @Transactional
    public void deleteTicket(Long ticketId, Long userId, boolean isAdmin) {
        ConsultationTicket t = ticketMapper.selectById(ticketId);
        if (t == null) throw new RuntimeException("工单不存在");
        if (!isAdmin && !t.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此工单");
        }
        // 先删消息
        messageMapper.delete(new LambdaQueryWrapper<ConsultationMessage>()
                .eq(ConsultationMessage::getTicketId, ticketId));
        // 再删工单
        ticketMapper.deleteById(ticketId);
    }

    public Map<String, Object> getDetail(Long ticketId) {
        ConsultationTicket t = ticketMapper.selectById(ticketId);
        if (t == null) return null;
        Map<String, Object> result = buildTicket(t);

        List<ConsultationMessage> msgs = messageMapper.selectList(
                new LambdaQueryWrapper<ConsultationMessage>()
                        .eq(ConsultationMessage::getTicketId, ticketId)
                        .orderByAsc(ConsultationMessage::getCreateTime)
        );
        result.put("messages", msgs);
        return result;
    }

    // ===== 内部 =====

    private ConsultationMessage addMessage(Long ticketId, String senderType, Long senderId, String content) {
        ConsultationMessage m = new ConsultationMessage();
        m.setTicketId(ticketId);
        m.setSenderType(senderType);
        m.setSenderId(senderId);
        m.setContent(content);
        m.setIsRead(0);
        m.setCreateTime(LocalDateTime.now());
        messageMapper.insert(m);
        return m;
    }

    private void updateTicketLast(Long ticketId, String lastMsg) {
        ConsultationTicket t = ticketMapper.selectById(ticketId);
        if (t != null) {
            t.setLastMessage(lastMsg);
            t.setLastTime(LocalDateTime.now());
            ticketMapper.updateById(t);
        }
    }

    private Map<String, Object> buildTicket(ConsultationTicket t) {
        Map<String, Object> r = new HashMap<>();
        r.put("id", t.getId());
        r.put("title", t.getTitle());
        r.put("category", t.getCategory());
        r.put("status", t.getStatus());
        r.put("lastMessage", t.getLastMessage());
        r.put("lastTime", t.getLastTime());
        r.put("createTime", t.getCreateTime());
        r.put("userId", t.getUserId());

        // 顾问信息
        if (t.getAssigneeId() != null) {
            SysUser adv = sysUserMapper.selectById(t.getAssigneeId());
            if (adv != null) r.put("advisorName", adv.getRealName());
        }
        // 用户信息
        try {
            AppUser u = appUserMapper.selectById(t.getUserId());
            if (u != null) {
                r.put("userNickname", u.getNickname());
                r.put("userPhone", u.getPhone());
            }
        } catch (Exception ignored) {
        }
        return r;
    }
}
