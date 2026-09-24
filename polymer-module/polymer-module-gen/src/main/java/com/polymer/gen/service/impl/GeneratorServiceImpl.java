package com.polymer.gen.service.impl;

import com.polymer.framework.common.exception.ServiceException;
import com.polymer.framework.common.utils.ArrayUtil;
import com.polymer.framework.common.utils.DateUtils;
import com.polymer.framework.common.utils.FileUtils;
import com.polymer.framework.common.utils.IoUtils;
import com.polymer.framework.common.utils.StringUtils;
import com.polymer.gen.common.utils.TemplateUtils;
import com.polymer.gen.config.template.GeneratorConfig;
import com.polymer.gen.config.template.GeneratorInfo;
import com.polymer.gen.config.template.TemplateInfo;
import com.polymer.gen.entity.BaseClassEntity;
import com.polymer.gen.entity.TableEntity;
import com.polymer.gen.entity.TableFieldEntity;
import com.polymer.gen.service.BaseClassService;
import com.polymer.gen.service.DataSourceService;
import com.polymer.gen.service.FieldTypeService;
import com.polymer.gen.service.GeneratorService;
import com.polymer.gen.service.TableFieldService;
import com.polymer.gen.service.TableService;
import com.polymer.gen.vo.PreviewVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 代码生成
 * polymer@126.com
 *
 */
@Service
public class GeneratorServiceImpl implements GeneratorService {
    @Resource
    private DataSourceService datasourceService;
    @Resource
    private FieldTypeService fieldTypeService;
    @Resource
    private BaseClassService baseClassService;
    @Resource
    private GeneratorConfig generatorConfig;
    @Resource
    private TableService tableService;
    @Resource
    private TableFieldService tableFieldService;

    @Override
    public void downloadCode(Long tableId, ZipOutputStream zip) {
        // 数据模型
        Map<String, Object> dataModel = getDataModel(tableId);

        // 代码生成器信息
        GeneratorInfo generator = generatorConfig.getGeneratorConfig();

        // 渲染模板并输出
        for (TemplateInfo template : generator.getTemplates()) {
            dataModel.put("templateName", template.getTemplateName());
            String content = TemplateUtils.getContent(template.getTemplateContent(), dataModel);
            String path = TemplateUtils.getContent(template.getGeneratorPath(), dataModel);

            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(path));
                IoUtils.writeUtf8(zip, false, content);
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                throw new ServiceException("模板写入失败：" + path, e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generatorCode(Long tableId) {
        // 数据模型
        Map<String, Object> dataModel = getDataModel(tableId);

        // 代码生成器信息
        GeneratorInfo generator = generatorConfig.getGeneratorConfig();

        // 渲染模板并输出
        for (TemplateInfo template : generator.getTemplates()) {
            dataModel.put("templateName", template.getTemplateName());
            String content = TemplateUtils.getContent(template.getTemplateContent(), dataModel);
            String path = TemplateUtils.getContent(template.getGeneratorPath(), dataModel);

            try {
                FileUtils.writeUtf8String(content, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获取渲染的数据模型
     *
     * @param tableId 表ID
     */
    private Map<String, Object> getDataModel(Long tableId) {
        // 表信息
        TableEntity table = tableService.getById(tableId);
        List<TableFieldEntity> fieldList = tableFieldService.getByTableId(tableId);
        table.setFieldList(fieldList);

        // 数据模型
        Map<String, Object> dataModel = new HashMap<>();

        // 获取数据库类型
        String dbType = datasourceService.getDatabaseProductName(table.getDatasourceId());
        dataModel.put("dbType", dbType);

        // 项目信息
        dataModel.put("package", table.getPackageName());
        dataModel.put("packagePath", table.getPackageName().replace(".", File.separator));
        dataModel.put("version", table.getVersion());
        dataModel.put("moduleName", table.getModuleName());
        dataModel.put("ModuleName", StringUtils.upperFirst(table.getModuleName()));
        dataModel.put("functionName", table.getFunctionName());
        dataModel.put("FunctionName", StringUtils.upperFirst(table.getFunctionName()));
        dataModel.put("functionNameKebab", StringUtils.camelToKebab(table.getFunctionName()));
        dataModel.put("formLayout", table.getFormLayout());

        // 开发者信息
        dataModel.put("author", table.getAuthor());
        dataModel.put("email", table.getEmail());
        dataModel.put("datetime", DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS));
        dataModel.put("date", DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD));

        // 设置字段分类
        setFieldTypeList(dataModel, table);

        // 设置基类信息
        setBaseClass(dataModel, table);

        // 导入的包列表
        Set<String> importList = fieldTypeService.getPackageByTableId(table.getId());
        dataModel.put("importList", importList);



        // 表信息
        dataModel.put("tableName", table.getTableName());
        dataModel.put("tableComment", table.getTableComment());
        dataModel.put("className", StringUtils.lowerFirst(table.getClassName()));
        dataModel.put("ClassName", table.getClassName());
        dataModel.put("fieldList", table.getFieldList());

        // 生成路径
        dataModel.put("backendPath", table.getBackendPath());
        dataModel.put("frontendPath", table.getFrontendPath());

        return dataModel;
    }

    /**
     * 设置基类信息
     *
     * @param dataModel 数据模型
     * @param table     表
     */
    private void setBaseClass(Map<String, Object> dataModel, TableEntity table) {
        if (table.getBaseclassId() == null) {
            return;
        }

        // 基类
        BaseClassEntity baseClass = baseClassService.selectBaseClassById(table.getBaseclassId());
        baseClass.setPackageName(baseClass.getPackageName());
        dataModel.put("baseClass", baseClass);

        // 基类字段
        String[] fields = baseClass.getFields().split(",");

        // 标注为基类字段
        for (TableFieldEntity field : table.getFieldList()) {
            if (ArrayUtil.contains(fields, field.getFieldName())) {
                field.setBaseField(true);
            }
        }
    }

    /**
     * 设置字段分类信息
     *
     * @param dataModel 数据模型
     * @param table     表
     */
    private void setFieldTypeList(Map<String, Object> dataModel, TableEntity table) {
        // 主键 (不支持多主键)
        TableFieldEntity primary = null;
        // 表单列表
        List<TableFieldEntity> formList = new ArrayList<>();
        // 网格列表
        List<TableFieldEntity> gridList = new ArrayList<>();
        // 查询列表
        List<TableFieldEntity> queryList = new ArrayList<>();

        Set<String> importQueryList = new HashSet<>();

        for (TableFieldEntity field : table.getFieldList()) {
            if (field.getPrimaryPk() && primary == null) {
                primary = field;
            }
            if (field.getFormItem()) {
                formList.add(field);
            }
            if (field.getGridItem()) {
                gridList.add(field);
            }
            if (field.getQueryItem()) {
                queryList.add(field);
                if(StringUtils.isNotBlank(field.getPackageName())){
                    importQueryList.add(field.getPackageName());
                }
            }
        }
        dataModel.put("primary", primary);
        dataModel.put("formList", formList);
        dataModel.put("gridList", gridList);
        dataModel.put("queryList", queryList);
        dataModel.put("importQueryList", importQueryList);
    }

    /**
     * 代码预览
     *
     * @param tableId 表ID
     * @return 预览内容
     */
    @Override
    public List<PreviewVO> preview(Long tableId) {
        Map<String, Object> dataModel = getDataModel(tableId);
        // 代码生成器信息
        GeneratorInfo generator = generatorConfig.getGeneratorConfig();
        return generator.getTemplates().stream().map(t -> {
            dataModel.put("templateName", t.getTemplateName());
            String content = TemplateUtils.getContent(t.getTemplateContent(), dataModel);
            String fileName = t.getGeneratorPath().substring(t.getGeneratorPath().lastIndexOf("/") + 1);
            fileName = TemplateUtils.getContent(fileName, dataModel);
            PreviewVO previewVO = new PreviewVO();
            previewVO.setFileName(fileName);
            previewVO.setContent(content);
            return previewVO;
        }).collect(Collectors.toList());
    }

}