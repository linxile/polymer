package com.polymer.gen.service.impl;

import com.polymer.framework.common.utils.StringUtils;
import com.polymer.gen.common.query.Query;
import com.polymer.gen.entity.FieldTypeEntity;
import com.polymer.gen.entity.TableFieldEntity;
import com.polymer.gen.enums.AutoFillEnum;
import com.polymer.gen.mapper.TableFieldMapper;
import com.polymer.gen.service.FieldTypeService;
import com.polymer.gen.service.TableFieldService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 表字段
 * polymer@126.com
 *
 */
@Service
public class TableFieldServiceImpl implements TableFieldService {
    @Resource
    private FieldTypeService fieldTypeService;
    @Resource
    private TableFieldMapper tableFieldMapper;

    @Override
    public List<TableFieldEntity> getByTableId(Long tableId) {
        Query query = new Query();
        query.setTableId(tableId);
        return tableFieldMapper.selectTableFieldList(query);
    }

    @Override
    public void deleteBatchTableIds(Long[] tableIds) {
        tableFieldMapper.deleteBatchTableIds(tableIds);
    }

    @Override
    public void updateTableField(Long tableId, List<TableFieldEntity> tableFieldList) {
        // 更新字段数据
        int sort = 0;
        for (TableFieldEntity tableField : tableFieldList) {
            tableField.setSort(sort++);
            tableFieldMapper.updateTableField(tableField);
        }
    }

    public void initFieldList(List<TableFieldEntity> tableFieldList) {
        // 字段类型、属性类型映射
        Map<String, FieldTypeEntity> fieldTypeMap = fieldTypeService.getMap();
        int index = 0;
        for (TableFieldEntity field : tableFieldList) {
            field.setAttrName(StringUtils.underlineToCamel(field.getFieldName()));
            // 获取字段对应的类型
            FieldTypeEntity fieldTypeMapping = fieldTypeMap.get(field.getFieldType().toLowerCase());
            if (fieldTypeMapping == null) {
                // 没找到对应的类型，则为Object类型
                field.setAttrType("Object");
            } else {
                field.setAttrType(fieldTypeMapping.getAttrType());
                field.setPackageName(fieldTypeMapping.getPackageName());
                field.setTsType(fieldTypeMapping.getTsType());
            }

            field.setAutoFill(AutoFillEnum.DEFAULT.name());
            field.setFormItem(true);
            field.setGridItem(true);
            field.setQueryType("=");
            field.setQueryFormType("text");
            field.setFormType("text");
            field.setSort(index++);
        }
    }

    @Override
    public int insertTableField(TableFieldEntity tableField) {
        return tableFieldMapper.insertTableField(tableField);
    }

    @Override
    public int updateTableField(TableFieldEntity tableField) {
        return tableFieldMapper.updateTableField(tableField);
    }

    @Override
    public void removeBatchByIds(List<Long> ids) {
        for(Long id: ids){
            tableFieldMapper.deleteTableFieldById(id);
        }
    }

}