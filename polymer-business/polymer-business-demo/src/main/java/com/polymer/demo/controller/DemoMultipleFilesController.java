package com.polymer.demo.controller;

import com.polymer.api.system.SysCheckImportApi;
import com.polymer.api.system.vo.ImportResultVO;
import com.polymer.demo.query.DemoMultipleFilesQuery;
import com.polymer.demo.service.DemoMultipleFilesService;
import com.polymer.demo.vo.DemoMultipleFilesExcelVO;
import com.polymer.demo.vo.DemoMultipleFilesVO;
import com.polymer.framework.common.pojo.PageResult;
import com.polymer.framework.common.pojo.Result;
import com.polymer.framework.common.utils.ExcelUtil;
import com.polymer.framework.common.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
* 多文件上传样例Controller
*
* @author polymer polymer@126.com
* @since 1.0.0 2026-06-23
*/
@RestController
@RequestMapping("demo/multipleFiles")
@Tag(name="多文件上传样例")
public class DemoMultipleFilesController {
    @Resource
    private DemoMultipleFilesService demoMultipleFilesService;
    @Resource
    private SysCheckImportApi sysCheckImportApi;

    @GetMapping("/page")
    @Operation(summary = "查询多文件上传样例分页")
    @PreAuthorize("hasAuthority('demo:multipleFiles:page')")
    public Result<PageResult<DemoMultipleFilesVO>> page(@ParameterObject @Valid DemoMultipleFilesQuery query){
        PageResult<DemoMultipleFilesVO> page = demoMultipleFilesService.page(query);

        return Result.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取多文件上传样例详细信息")
    @PreAuthorize("hasAuthority('demo:multipleFiles:info')")
    public Result<DemoMultipleFilesVO> getInfo(@PathVariable("id") Long id){
        DemoMultipleFilesVO vo = demoMultipleFilesService.selectDemoMultipleFilesById(id);

        return Result.ok(vo);
    }

    @PostMapping
    @Operation(summary = "新增多文件上传样例")
    @PreAuthorize("hasAuthority('demo:multipleFiles:save')")
    public Result<DemoMultipleFilesVO> save(@RequestBody DemoMultipleFilesVO vo){
        DemoMultipleFilesVO resVo = demoMultipleFilesService.insertDemoMultipleFiles(vo);

        return Result.ok(resVo);
    }

    @PutMapping
    @Operation(summary = "修改多文件上传样例")
    @PreAuthorize("hasAuthority('demo:multipleFiles:update')")
    public Result<DemoMultipleFilesVO> update(@RequestBody @Valid DemoMultipleFilesVO vo){
        DemoMultipleFilesVO resVo = demoMultipleFilesService.updateDemoMultipleFiles(vo);

        return Result.ok(resVo);
    }

    @DeleteMapping
    @Operation(summary = "删除多文件上传样例")
    @PreAuthorize("hasAuthority('demo:multipleFiles:delete')")
    public Result<Integer> delete(@RequestBody List<Long> idList){
        int num = demoMultipleFilesService.deleteDemoMultipleFilesByIdList(idList);

        return Result.ok(num);
    }

    /**
     * 导出模板
     */
    @GetMapping("/exportTemplate")
    @Operation(summary = "导出模板")
    @PreAuthorize("hasAuthority('demo:multipleFiles:import')")
    public void exportTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<DemoMultipleFilesExcelVO> excelUtil = new ExcelUtil<>(DemoMultipleFilesExcelVO.class);
        byte[] b = excelUtil.createTemplateExcel("多文件上传样例导入模板", "多文件上传样例数据导入（注：红色字体为必填项）");

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, "多文件上传样例导入模板.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(b);
    }

    /**
     * 获取Excel导入唯一字段列表
     * 返回所有@Excel注解中unique = true的字段名称
     */
    @GetMapping("/uniqueFields")
    @Operation(summary = "获取Excel导入唯一字段列表")
    @PreAuthorize("hasAuthority('demo:multipleFiles:import')")
    public Result<List<String>> getUniqueFields() {
        List<String> uniqueFields = sysCheckImportApi.getUniqueFields(DemoMultipleFilesExcelVO.class);
        return Result.ok(uniqueFields);
    }

    @PostMapping("/import")
    @Operation(summary = "导入多文件上传样例")
    @PreAuthorize("hasAuthority('demo:multipleFiles:import')")
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file, @RequestParam(value = "strategy", defaultValue = "skip") String strategy) throws Exception {
        if (file.isEmpty()) {
            return Result.error("请选择需要上传的文件");
        }
        ImportResultVO res = demoMultipleFilesService.importByExcel(file, strategy);
        return Result.ok(res);
    }

    @PostMapping("/export")
    @Operation(summary = "导出多文件上传样例")
    @PreAuthorize("hasAuthority('demo:multipleFiles:export')")
    public void export(@RequestBody DemoMultipleFilesQuery query, HttpServletResponse response) throws IOException {
        byte[] b = demoMultipleFilesService.export(query);

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, "多文件上传样例数据.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(b);
    }
}
