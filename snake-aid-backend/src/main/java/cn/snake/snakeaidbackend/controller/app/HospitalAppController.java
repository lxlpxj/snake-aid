package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.Hospital;
import cn.snake.snakeaidbackend.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/app/hospital")
@Tag(name = "APP-就近医院")
public class HospitalAppController {

    @Resource
    private HospitalService hospitalService;

    @Operation(summary = "就近医院（支持坐标距离排序 或 按省市区筛选）")
    @GetMapping("/nearby")
    public Result<List<Hospital>> nearby(
            @RequestParam(required = false) BigDecimal lng,
            @RequestParam(required = false) BigDecimal lat,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword) {
        return Result.success(hospitalService.findNearbyHospitals(lng, lat, province, city, keyword));
    }

    @Operation(summary = "医院详情")
    @GetMapping("/{id}")
    public Result<Hospital> detail(@PathVariable Long id) {
        return Result.success(hospitalService.detail(id));
    }

    @Operation(summary = "省份列表（去重）")
    @GetMapping("/provinces")
    public Result<List<String>> provinces() {
        return Result.success(hospitalService.listProvinces());
    }

    @Operation(summary = "某省下属城市列表")
    @GetMapping("/cities")
    public Result<List<String>> cities(@RequestParam String province) {
        return Result.success(hospitalService.listCities(province));
    }
}
