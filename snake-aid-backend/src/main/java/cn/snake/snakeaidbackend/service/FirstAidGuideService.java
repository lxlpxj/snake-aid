package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.FirstAidGuide;

public interface FirstAidGuideService {

    PageResult<FirstAidGuide> pageForAdmin(long current, long size, Integer auditStatus);

    FirstAidGuide createSimple(FirstAidGuide guide);

    FirstAidGuide updateSimple(FirstAidGuide guide);

    void delete(Long id);

    void submitAudit(Long id);

    void audit(Long id, boolean pass, Long auditBy);
}
