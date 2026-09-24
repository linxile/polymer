package com.polymer.system.controller;

import com.polymer.framework.common.pojo.PageResult;
import com.polymer.framework.common.pojo.Result;
import com.polymer.framework.common.utils.FileUtils;
import com.polymer.framework.logger.annotations.OperateLog;
import com.polymer.framework.logger.enums.OperateTypeEnum;
import com.polymer.system.query.SysLogLoginQuery;
import com.polymer.system.service.SysLogLoginService;
import com.polymer.system.vo.SysLogLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 登录日志
 *
 * @author polymer
 */
@RestController
@RequestMapping("sys/log/login")
@Tag(name = "登录日志")
public class SysLogLoginController {
    @Resource
    private SysLogLoginService sysLogLoginService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @PreAuthorize("hasAuthority('sys:log:login')")
    public Result<PageResult<SysLogLoginVO>> page(@ParameterObject @Valid SysLogLoginQuery query) {
        PageResult<SysLogLoginVO> page = sysLogLoginService.page(query);

        return Result.ok(page);
    }

    @PostMapping("export")
    @Operation(summary = "导出excel")
    @OperateLog(type = OperateTypeEnum.EXPORT)
    @PreAuthorize("hasAuthority('sys:log:login')")
    public void export(@RequestBody SysLogLoginQuery query, HttpServletResponse response) throws IOException {
        byte[] b = sysLogLoginService.export(query);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, "登录日志.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(b);
    }

}