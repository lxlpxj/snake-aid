package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传 —— 图片 / 视频 存本地 uploads/，返回可访问 URL
 * 访问路径：{baseURL}/uploads/{filename}
 */
@RestController
@RequestMapping("/admin/upload")
@Tag(name = "后台-文件上传")
public class FileUploadController {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    // ============ 图片 ============
    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return doUpload(file, "\\.(jpg|jpeg|png|gif|webp|bmp)$", "仅支持 jpg/png/gif/webp/bmp 格式");
    }

    // ============ 视频 ============
    @Operation(summary = "上传视频")
    @PostMapping("/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        return doUpload(file, "\\.(mp4|webm|mov|avi|mkv|m4v)$", "仅支持 mp4/webm/mov/avi/mkv/m4v 格式");
    }

    // ============ 通用 ============
    private Result<Map<String, String>> doUpload(MultipartFile file, String extRegex, String failMsg) throws IOException {
        if (file == null || file.isEmpty()) return Result.fail("文件不能为空");

        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".")
                ? original.substring(original.lastIndexOf('.'))
                : "";
        String extLower = ext.toLowerCase();
        if (!extLower.matches(extRegex)) return Result.fail(failMsg);

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String filename = UUID.randomUUID().toString().replace("-", "") + extLower;
        File target = new File(dir, filename);
        file.transferTo(target);

        Map<String, String> data = new HashMap<>();
        data.put("url", "/uploads/" + filename);
        data.put("filename", filename);
        data.put("size", String.valueOf(file.getSize()));
        return Result.success(data);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{filename}")
    public Result<Void> delete(@PathVariable String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return Result.fail("非法文件名");
        }
        File target = new File(uploadDir, filename);
        if (target.exists()) target.delete();
        return Result.success();
    }
}
