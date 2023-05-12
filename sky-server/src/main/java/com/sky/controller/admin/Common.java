package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class Common {
    @Autowired
    AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        //获取原始文件名
        try {
            String fileName = file.getOriginalFilename();
            //获取图片后缀
            String[] s = fileName.split("\\.");
            //构造新文件名称
            String name = UUID.randomUUID() + "." + s[1];
            log.info("name:{}", name);
            String filePath = aliOssUtil.upload(file.getBytes(), name);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败:{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
