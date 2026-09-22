package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.Hospital;

import java.math.BigDecimal;
import java.util.List;

/**
 * 医院 & 血清库存服务
 */
public interface HospitalService {

    /**
     * APP 端就近医院（支持坐标距离排序 或 按省市区筛选）
     */
    List<Hospital> findNearbyHospitals(BigDecimal lng, BigDecimal lat, String province, String city, String keyword);

    /** 所有省份（去重） */
    List<String> listProvinces();

    /** 某省下所有城市（去重） */
    List<String> listCities(String province);

    /**
     * 管理端分页（Controller 调用签名：current, size, province, city, hasSerum）
     */
    PageResult<Hospital> pageForAdmin(long current, long size, String province, String city, Integer hasSerum);

    /**
     * 详情
     */
    Hospital detail(Long id);

    Long create(Hospital entity);

    void update(Hospital entity);

    void delete(Long id);

    void updateSerumStock(Long hospitalId, String serumType, Integer quantity);
}
