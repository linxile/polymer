package com.polymer.gen.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.polymer.framework.common.exception.ServiceException;
import com.polymer.framework.common.pojo.PageResult;
import com.polymer.framework.common.utils.StringUtils;
import com.polymer.gen.common.query.Query;
import com.polymer.gen.common.utils.GenUtils;
import com.polymer.gen.config.GenDataSource;
import com.polymer.gen.config.template.GeneratorConfig;
import com.polymer.gen.config.template.GeneratorInfo;
import com.polymer.gen.entity.TableEntity;
import com.polymer.gen.entity.TableFieldEntity;
import com.polymer.gen.enums.FormLayoutEnum;
import com.polymer.gen.enums.GeneratorTypeEnum;
import com.polymer.gen.mapper.TableMapper;
import com.polymer.gen.service.DataSourceService;
import com.polymer.gen.service.TableFieldService;
import com.polymer.gen.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 数据表
 * polymer@126.com
 *
 */
@Service
public class TableServiceImpl implements TableService {
    private static final Logger log = LoggerFactory.getLogger(TableServiceImpl.class);
    @Resource
    private TableMapper tableMapper;
    @Resource
    private TableFieldService tableFieldService;
    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private GeneratorConfig generatorConfig;

    @Override
    public PageResult<TableEntity> page(Query query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<TableEntity> entityList = tableMapper.selectTableList(query);
        PageInfo<TableEntity> pageInfo = new PageInfo<>(entityList);
        return new PageResult<>(entityList, pageInfo.getTotal());
    }

    @Override
    public TableEntity getByTableName(String tableName) {
        List<TableEntity> tableEntities = tableMapper.selectTableListByTableName(tableName);
        if(tableEntities != null && !tableEntities.isEmpty()){
            return tableEntities.get(0);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Long[] ids) {
        // 删除表
        for(Long id: ids){
            tableMapper.deleteTableById(id);
        }
        // 删除列
        tableFieldService.deleteBatchTableIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void tableImport(Long datasourceId, String tableName) {
        // 初始化配置信息
        GenDataSource dataSource = dataSourceService.get(datasourceId);
        // 查询表是否存在
        TableEntity table = this.getByTableName(tableName);
        // 表存在
        if (table != null) {
            throw new ServiceException(tableName + "已存在");
        }

        // 从数据库获取表信息
        table = GenUtils.getTable(dataSource, tableName);

        // 代码生成器信息
        GeneratorInfo generator = generatorConfig.getGeneratorConfig();

        // 保存表信息
        table.setPackageName(generator.getProject().getPackageName());
        table.setVersion(generator.getProject().getVersion());
        table.setBackendPath(generator.getProject().getBackendPath());
        table.setFrontendPath(generator.getProject().getFrontendPath());
        table.setAuthor(generator.getDeveloper().getAuthor());
        table.setEmail(generator.getDeveloper().getEmail());
        table.setFormLayout(FormLayoutEnum.ONE.getValue());
        table.setGeneratorType(GeneratorTypeEnum.ZIP_DOWNLOAD.ordinal());
        table.setClassName(StringUtils.toPascalCase(tableName));
        table.setModuleName(GenUtils.getModuleName(table.getPackageName()));
        table.setFunctionName(GenUtils.getFunctionName(tableName));
        table.setCreateTime(LocalDateTime.now());
        tableMapper.insertTable(table);

        // 获取原生字段数据
        List<TableFieldEntity> tableFieldList = GenUtils.getTableFieldList(dataSource, table.getId(), table.getTableName());
        // 初始化字段数据
        tableFieldService.initFieldList(tableFieldList);

        // 保存列数据
        tableFieldList.forEach(e-> tableFieldService.insertTableField(e));

        try {
            //释放数据源
            dataSource.getConnection().close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync(Long id) {
        TableEntity table = tableMapper.selectTableById(id);

        // 初始化配置信息
        GenDataSource datasource = dataSourceService.get(table.getDatasourceId());

        // 从数据库获取表字段列表
        List<TableFieldEntity> dbTableFieldList = GenUtils.getTableFieldList(datasource, table.getId(), table.getTableName());
        if (dbTableFieldList.isEmpty()) {
            throw new ServiceException("同步失败，请检查数据库表：" + table.getTableName());
        }

        List<String> dbTableFieldNameList = dbTableFieldList.stream().map(TableFieldEntity::getFieldName).collect(Collectors.toList());

        // 表字段列表
        List<TableFieldEntity> tableFieldList = tableFieldService.getByTableId(id);

        Map<String, TableFieldEntity> tableFieldMap = tableFieldList.stream().collect(Collectors.toMap(TableFieldEntity::getFieldName, Function.identity()));

        // 初始化字段数据
        tableFieldService.initFieldList(dbTableFieldList);

        // 同步表结构字段
        dbTableFieldList.forEach(field -> {
            // 新增字段
            if (!tableFieldMap.containsKey(field.getFieldName())) {
                tableFieldService.insertTableField(field);
                return;
            }

            // 修改字段
            TableFieldEntity updateField = tableFieldMap.get(field.getFieldName());
            updateField.setPrimaryPk(field.getPrimaryPk());
            updateField.setFieldComment(field.getFieldComment());
            updateField.setFieldType(field.getFieldType());
            updateField.setAttrType(field.getAttrType());
            updateField.setPackageName(field.getPackageName());
            updateField.setTsType(field.getTsType());

            tableFieldService.updateTableField(updateField);
        });

        // 删除数据库表中没有的字段
        List<TableFieldEntity> delFieldList = tableFieldList.stream().filter(field -> !dbTableFieldNameList.contains(field.getFieldName())).collect(Collectors.toList());
        if (!delFieldList.isEmpty()) {
            List<Long> fieldIds = delFieldList.stream().map(TableFieldEntity::getId).collect(Collectors.toList());
            tableFieldService.removeBatchByIds(fieldIds);
        }
    }

    @Override
    public TableEntity getById(Long id) {
        return tableMapper.selectTableById(id);
    }

    @Override
    public TableEntity updateById(TableEntity table) {
        tableMapper.updateTable(table);
        return table;
    }
}