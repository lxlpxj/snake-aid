package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.ResultCode;
import cn.snake.snakeaidbackend.entity.Hospital;
import cn.snake.snakeaidbackend.entity.SerumStock;
import cn.snake.snakeaidbackend.exception.BusinessException;
import cn.snake.snakeaidbackend.mapper.HospitalMapper;
import cn.snake.snakeaidbackend.mapper.SerumStockMapper;
import cn.snake.snakeaidbackend.service.HospitalService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * 医院服务实现
 */
@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final HospitalMapper hospitalMapper;
    private final SerumStockMapper serumStockMapper;

    @Override
    public List<Hospital> findNearbyHospitals(BigDecimal lng, BigDecimal lat, String province, String city, String keyword) {
        LambdaQueryWrapper<Hospital> wrapper = new LambdaQueryWrapper<>();
        // 省市区筛选
        if (StringUtils.hasText(province)) wrapper.eq(Hospital::getProvince, province);
        if (StringUtils.hasText(city)) wrapper.eq(Hospital::getCity, city);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Hospital::getName, keyword)
                    .or().like(Hospital::getAddress, keyword));
        }
        List<Hospital> all = hospitalMapper.selectList(wrapper);

        // 有坐标才算距离，没坐标（省市区筛选）distance=null
        for (Hospital h : all) {
            if (lng != null && lat != null && h.getLongitude() != null && h.getLatitude() != null) {
                h.setDistance(haversineKm(lng.doubleValue(), lat.doubleValue(),
                        h.getLongitude().doubleValue(), h.getLatitude().doubleValue()));
            } else {
                h.setDistance(null);
            }
        }

        // 排序：有血清优先，再按距离升序（没距离的排后面）
        all.sort(Comparator
                .comparing((Hospital h) -> h.getHasSerum() == null ? 0 : h.getHasSerum())
                .reversed()
                .thenComparingDouble(h -> h.getDistance() == null ? Double.MAX_VALUE : h.getDistance()));
        return all;
    }

    @Override
    public List<String> listProvinces() {
        return hospitalMapper.selectList(null).stream()
                .map(Hospital::getProvince)
                .filter(StringUtils::hasText)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<String> listCities(String province) {
        LambdaQueryWrapper<Hospital> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hospital::getProvince, province);
        return hospitalMapper.selectList(wrapper).stream()
                .map(Hospital::getCity)
                .filter(StringUtils::hasText)
                .distinct()
                .sorted()
                .toList();
    }

    /** Haversine 公式：两点经纬度算球面距离（km） */
    private double haversineKm(double lng1, double lat1, double lng2, double lat2) {
        double R = 6371; // 地球半径 km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public PageResult<Hospital> pageForAdmin(long current, long size, String province, String city, Integer hasSerum) {
        Page<Hospital> page = new Page<>(current, size);
        LambdaQueryWrapper<Hospital> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(province)) wrapper.eq(Hospital::getProvince, province);
        if (StringUtils.hasText(city)) wrapper.eq(Hospital::getCity, city);
        if (hasSerum != null) wrapper.eq(Hospital::getHasSerum, hasSerum);
        wrapper.orderByDesc(Hospital::getId);
        IPage<Hospital> result = hospitalMapper.selectPage(page, wrapper);
        return PageResult.of(result);
    }

    @Override
    public Hospital detail(Long id) {
        Hospital entity = hospitalMapper.selectById(id);
        if (entity == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        return entity;
    }

    @Override
    public Long create(Hospital entity) {
        hospitalMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void update(Hospital entity) {
        if (hospitalMapper.selectById(entity.getId()) == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        hospitalMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        Hospital entity = hospitalMapper.selectById(id);
        if (entity == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        hospitalMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSerumStock(Long hospitalId, String serumType, Integer quantity) {
        SerumStock stock = serumStockMapper.selectOne(
                new LambdaQueryWrapper<SerumStock>()
                        .eq(SerumStock::getHospitalId, hospitalId)
                        .eq(SerumStock::getSerumType, serumType)
        );
        if (stock == null) {
            stock = new SerumStock();
            stock.setHospitalId(hospitalId);
            stock.setSerumType(serumType);
            stock.setQuantity(quantity);
            serumStockMapper.insert(stock);
        } else {
            stock.setQuantity(quantity);
            serumStockMapper.updateById(stock);
        }
        if (quantity != null && quantity > 0) {
            Hospital hospital = hospitalMapper.selectById(hospitalId);
            if (hospital != null && (hospital.getHasSerum() == null || hospital.getHasSerum() != 1)) {
                hospital.setHasSerum(1);
                hospitalMapper.updateById(hospital);
            }
        }
    }
}
