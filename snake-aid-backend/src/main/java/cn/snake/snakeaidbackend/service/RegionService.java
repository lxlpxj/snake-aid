package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.entity.Region;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 地区字典服务
 */
public interface RegionService extends IService<Region> {

    /**
     * 获取所有地区列表（省市区结构）
     *
     * @return 平铺的 Region 列表，调用方自行组装树
     */
    List<Region> listAll();
}
