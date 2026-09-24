package com.polymer.gen.controller;

import com.polymer.framework.common.pojo.Result;
import com.polymer.framework.common.utils.FileUtils;
import com.polymer.framework.common.utils.IoUtils;
import com.polymer.gen.service.GeneratorService;
import com.polymer.gen.vo.PreviewVO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成
 * polymer@126.com
 *
 */
@RestController
@RequestMapping("gen/generator")
public class GeneratorController {
    @Resource
    private GeneratorService generatorService;

    /**
     * 生成代码（zip压缩包）
     */
    @PostMapping("download")
    public void download(@RequestBody List<Long> tableIds, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        // 生成代码
        for (Long tableId : tableIds) {
            generatorService.downloadCode(tableId, zip);
        }

        IoUtils.close(zip);

        // zip压缩包数据
        byte[] b = outputStream.toByteArray();

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, "polymer.zip");
        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(b);
    }

    /**
     * 生成代码（自定义目录）
     */
    @PostMapping("code")
    public Result<String> code(@RequestBody Long[] tableIds) {
        // 生成代码
        for (Long tableId : tableIds) {
            generatorService.generatorCode(tableId);
        }

        return Result.ok();
    }

    /**
     * 预览代码
     */
    @GetMapping("/preview")
    public Result<List<PreviewVO>> preview(@RequestParam Long tableId) {
        List<PreviewVO> results = generatorService.preview(tableId);
        return Result.ok(results);
    }

}