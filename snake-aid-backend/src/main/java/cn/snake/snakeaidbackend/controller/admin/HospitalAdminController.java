package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.Hospital;
import cn.snake.snakeaidbackend.entity.SerumStock;
import cn.snake.snakeaidbackend.mapper.SerumStockMapper;
import cn.snake.snakeaidbackend.service.HospitalService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/admin/hospital")
@Tag(name = "后台-医院管理")
public class HospitalAdminController {

    @Resource
    private HospitalService hospitalService;

    @Resource
    private SerumStockMapper serumStockMapper;

    @Value("${app.amap-key:}")
    private String amapKey;

    // ==================== 医院 CRUD ====================

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<Hospital>> page(
            @RequestParam(required = false) Long current,
            @RequestParam(required = false) Long pageNum,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer hasSerum) {
        long c = current != null ? current : (pageNum != null ? pageNum : 1L);
        long s = size != null ? size : (pageSize != null ? pageSize : 10L);
        return Result.success(hospitalService.pageForAdmin(c, s, province, city, hasSerum));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<Hospital> detail(@PathVariable Long id) {
        return Result.success(hospitalService.detail(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Long> create(@RequestBody Hospital entity) {
        return Result.success(hospitalService.create(entity));
    }

    @Operation(summary = "更新")
    @PutMapping
    public Result<Void> update(@RequestBody Hospital entity) {
        hospitalService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hospitalService.delete(id);
        return Result.success();
    }

    // ==================== 血清库存 CRUD ====================

    @Operation(summary = "查询某医院血清列表")
    @GetMapping("/{hospitalId}/serum")
    public Result<java.util.List<SerumStock>> serumList(@PathVariable Long hospitalId) {
        LambdaQueryWrapper<SerumStock> w = new LambdaQueryWrapper<>();
        w.eq(SerumStock::getHospitalId, hospitalId);
        return Result.success(serumStockMapper.selectList(w));
    }

    @Operation(summary = "新增血清")
    @PostMapping("/{hospitalId}/serum")
    public Result<Long> createSerum(@PathVariable Long hospitalId, @RequestBody SerumStock serum) {
        serum.setHospitalId(hospitalId);
        serum.setUpdateTime(LocalDateTime.now());
        serumStockMapper.insert(serum);
        return Result.success(serum.getId());
    }

    @Operation(summary = "更新血清")
    @PutMapping("/serum")
    public Result<Void> updateSerum(@RequestBody SerumStock serum) {
        serum.setUpdateTime(LocalDateTime.now());
        serumStockMapper.updateById(serum);
        return Result.success();
    }

    @Operation(summary = "删除血清")
    @DeleteMapping("/serum/{id}")
    public Result<Void> deleteSerum(@PathVariable Long id) {
        serumStockMapper.deleteById(id);
        return Result.success();
    }

    // ==================== 高德地图 API 代理 ====================

    private final ObjectMapper om = new ObjectMapper();

    /** 地址搜索 → 调高德地理编码 geo 接口 */
    @Operation(summary = "地址搜索（高德地理编码）")
    @GetMapping("/geo-search")
    public Result<List<Map<String, Object>>> geoSearch(@RequestParam String q) {
        List<Map<String, Object>> empty = new ArrayList<>();
        try {
            if (amapKey == null || amapKey.isEmpty() || amapKey.contains("YOUR_AMAP_KEY")) {
                return Result.success(empty);
            }
            String url = "https://restapi.amap.com/v3/geocode/geo?key=" + amapKey
                    + "&address=" + URLEncoder.encode(q, StandardCharsets.UTF_8) + "&output=json";
            JsonNode root = httpGet(url);
            if (root == null || root.path("status").asText().equals("0")) return Result.success(empty);
            JsonNode geocodes = root.path("geocodes");
            if (!geocodes.isArray()) return Result.success(empty);
            List<Map<String, Object>> result = new ArrayList<>();
            for (JsonNode g : geocodes) {
                String loc = g.path("location").asText("");
                String[] lr = loc.split(",");
                if (lr.length < 2) continue;
                Map<String, Object> item = new HashMap<>();
                item.put("name", g.path("formatted_address").asText(""));
                item.put("display_name", g.path("formatted_address").asText(""));
                item.put("address", g.path("formatted_address").asText(""));
                item.put("province", g.path("province").asText(""));
                item.put("city", g.path("city").asText(""));
                item.put("district", g.path("district").asText(""));
                double lng = Double.parseDouble(lr[0].trim());
                double lat = Double.parseDouble(lr[1].trim());
                item.put("lng", lng); item.put("lon", lng); item.put("longitude", lng);
                item.put("lat", lat); item.put("latitude", lat);
                result.add(item);
            }
            return Result.success(result);
        } catch (Exception e) {
            log.warn("geoSearch error: {}", e.getMessage());
            return Result.success(empty);
        }
    }

    /** 逆地理编码（坐标 → 省/市/区/地址） */
    @Operation(summary = "逆地理编码（坐标转地址）")
    @GetMapping("/geo-reverse")
    public Result<Map<String, Object>> geoReverse(@RequestParam Double lng, @RequestParam Double lat) {
        Map<String, Object> empty = new HashMap<>();
        empty.put("province", ""); empty.put("city", ""); empty.put("district", "");
        empty.put("address", ""); empty.put("formatted_address", "");
        try {
            if (amapKey == null || amapKey.isEmpty() || amapKey.contains("YOUR_AMAP_KEY")) {
                return Result.success(empty);
            }
            String url = "https://restapi.amap.com/v3/geocode/regeo?key=" + amapKey
                    + "&location=" + lng + "," + lat + "&output=json";
            JsonNode root = httpGet(url);
            if (root == null || root.path("status").asText().equals("0")) return Result.success(empty);
            JsonNode regeo = root.path("regeocode");
            if (regeo.isMissingNode() || regeo.isNull()) return Result.success(empty);
            Map<String, Object> result = new HashMap<>();
            result.put("formatted_address", regeo.path("formatted_address").asText(""));
            result.put("address", regeo.path("formatted_address").asText(""));
            JsonNode ac = regeo.path("addressComponent");
            if (!ac.isMissingNode()) {
                result.put("province", ac.path("province").asText(""));
                result.put("city", ac.path("city").asText(""));
                result.put("district", ac.path("district").asText(""));
            }
            return Result.success(result);
        } catch (Exception e) {
            log.warn("geoReverse error: {}", e.getMessage());
            return Result.success(empty);
        }
    }

    /** 通用 HTTP GET 返回 Jackson JsonNode */
    private JsonNode httpGet(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "SnakeAid/1.0");
            if (conn.getResponseCode() != 200) return null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            return om.readTree(sb.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
