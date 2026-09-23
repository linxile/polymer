package com.polymer.system.controller;

import com.polymer.api.system.SysCheckImportApi;
import com.polymer.api.system.vo.ImportResultVO;
import com.polymer.framework.common.pojo.PageResult;
import com.polymer.framework.common.pojo.Result;
import com.polymer.framework.common.utils.ExcelUtil;
import com.polymer.framework.common.utils.FileUtils;
import com.polymer.framework.common.utils.StringUtils;
import com.polymer.framework.encrypt.core.annotation.ApiEncrypt;
import com.polymer.framework.idempotent.core.annotation.RepeatSubmit;
import com.polymer.framework.logger.annotations.OperateLog;
import com.polymer.framework.logger.enums.OperateTypeEnum;
import com.polymer.framework.security.core.user.SecurityUser;
import com.polymer.framework.security.core.user.UserDetail;
import com.polymer.system.query.SysUserQuery;
import com.polymer.system.service.SysUserService;
import com.polymer.system.vo.SysUserBaseVO;
import com.polymer.system.vo.SysUserExcelVO;
import com.polymer.system.vo.SysUserPasswordVO;
import com.polymer.system.vo.SysUserSelectVO;
import com.polymer.system.vo.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
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
 * 用户管理
 *
 * @author polymer
 */
@RestController
@RequestMapping("sys/user")
@Tag(name = "用户管理")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private SysCheckImportApi sysCheckImportApi;

    @GetMapping("page")
    @Operation(summary = "分页")
    @PreAuthorize("hasAuthority('sys:user:page')")
    public Result<PageResult<SysUserVO>> page(@ParameterObject @Valid SysUserQuery query) {
        PageResult<SysUserVO> page = sysUserService.page(query);

        return Result.ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('sys:user:info')")
    public Result<SysUserVO> get(@PathVariable("id") Long id) {
        SysUserVO vo = sysUserService.getById(id);
        return Result.ok(vo);
    }

    @GetMapping("info")
    @Operation(summary = "登录用户")
    public Result<SysUserVO> info() {
        SysUserVO user = sysUserService.info();
        return Result.ok(user);
    }

    @PutMapping("info")
    @Operation(summary = "修改登录用户信息")
    @OperateLog(type = OperateTypeEnum.UPDATE)
    public Result<String> loginInfo(@RequestBody @Valid SysUserBaseVO vo) {
        sysUserService.updateLoginInfo(vo);

        return Result.ok();
    }

    @ApiEncrypt(response = true)
    @RepeatSubmit
    @PutMapping("/password")
    @Operation(summary = "修改密码")
    @OperateLog(type = OperateTypeEnum.UPDATE)
    public Result<String> password(@RequestBody @Valid SysUserPasswordVO vo) {
        // 原密码不正确
        UserDetail user = SecurityUser.getUser();
        Assert.notNull(user, "用户信息为空");
        if (!passwordEncoder.matches(vo.getPassword(), user.getPassword())) {
            return Result.error("原密码不正确");
        }

        // 修改密码
        sysUserService.updatePassword(user.getId(), passwordEncoder.encode(vo.getNewPassword()));

        return Result.ok("修改密码成功，请重新登录！");
    }

    @PostMapping
    @Operation(summary = "保存")
    @OperateLog(type = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result<SysUserVO> save(@RequestBody @Valid SysUserVO vo) {
        // 新增密码不能为空
        if (StringUtils.isBlank(vo.getPassword())) {
            return Result.error("密码不能为空");
        }

        // 密码加密
        vo.setPassword(passwordEncoder.encode(vo.getPassword()));

        // 保存
        SysUserVO resVo = sysUserService.save(vo);

        return Result.ok(resVo);
    }

    @PutMapping
    @Operation(summary = "修改")
    @OperateLog(type = OperateTypeEnum.UPDATE)
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result<SysUserVO> update(@RequestBody @Valid SysUserVO vo) {
        // 如果密码不为空，则进行加密处理
        if (StringUtils.isBlank(vo.getPassword())) {
            vo.setPassword(null);
        } else {
            vo.setPassword(passwordEncoder.encode(vo.getPassword()));
        }

        SysUserVO resVo = sysUserService.update(vo);

        return Result.ok(resVo);
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @OperateLog(type = OperateTypeEnum.DELETE)
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result<String> delete(@RequestBody List<Long> idList) {
        Long userId = SecurityUser.getUserId();
        if (idList.contains(userId)) {
            return Result.error("不能删除当前登录用户");
        }

        sysUserService.delete(idList);

        return Result.ok();
    }

    @PostMapping("/updateUserAvatar")
    @Operation(summary = "根据id修改用户头像")
    public Result<String> updateUserAvatar(@RequestBody SysUserVO vo) {
        sysUserService.updateUserAvatar(vo.getId(), vo.getAvatar());
        return Result.ok();
    }

    @GetMapping("/getUserListByDeptId/{deptId}/{sub}")
    @Operation(summary = "根据部门id查询用户信息集合")
    public Result<List<SysUserSelectVO>> getUserListByDeptId(@PathVariable("deptId") Long deptId, @PathVariable("sub") Integer sub) {
        List<SysUserSelectVO> userSelectVOList = sysUserService.getUserListByDeptId(deptId, sub);
        return Result.ok(userSelectVOList);
    }

    @GetMapping("/list")
    @Operation(summary = "集合")
    public Result<List<SysUserVO>> list() {
        List<SysUserVO> list = sysUserService.list();

        return Result.ok(list);
    }

    /**
     * 导出模板
     */
    @GetMapping("/exportTemplate")
    @Operation(summary = "导出模板")
    @PreAuthorize("hasAuthority('sys:user:import')")
    public void exportTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<SysUserExcelVO> excelUtil = new ExcelUtil<>(SysUserExcelVO.class);
        byte[] b = excelUtil.createTemplateExcel("用户导入模板", "用户数据导入（注：红色字体为必填项）");

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, "用户导入模板.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(b);
    }

    /**
     * 获取Excel导入唯一字段列表
     * 返回所有@Excel注解中unique = true的字段名称
     */
    @GetMapping("/uniqueFields")
    @Operation(summary = "获取Excel导入唯一字段列表")
    @PreAuthorize("hasAuthority('sys:user:import')")
    public Result<List<String>> getUniqueFields() {
        List<String> uniqueFields = sysCheckImportApi.getUniqueFields(SysUserExcelVO.class);
        return Result.ok(uniqueFields);
    }

    @PostMapping("/import")
    @Operation(summary = "导入用户")
    @PreAuthorize("hasAuthority('sys:user:import')")
    public Result<ImportResultVO> importExcel(@RequestParam("file") MultipartFile file, @RequestParam(value = "strategy", defaultValue = "skip") String strategy) throws Exception {
        if (file.isEmpty()) {
            return Result.error("请选择需要上传的文件");
        }
        ImportResultVO res = sysUserService.importByExcel(file, passwordEncoder.encode("123456"), strategy);
        return Result.ok(res);
    }

    @PostMapping("/export")
    @Operation(summary = "导出用户")
    @PreAuthorize("hasAuthority('sys:user:export')")
    public void export(@RequestBody SysUserQuery query, HttpServletResponse response) throws IOException {
        byte[] b = sysUserService.export(query);

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, "用户数据.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(b);
    }
}
