package com.polymer.gen.entity;

import java.io.Serializable;

/**
 * 代码生成表字段对象 gen_table_field
 *
 * @author polymer polymer@126.com
 * @since 1.0.0 2025-10-21
 */
public class TableFieldEntity implements Serializable  {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 表ID
     */
    private Long tableId;
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 字段说明
     */
    private String fieldComment;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 属性类型
     */
    private String attrType;
    /**
     * 属性包名
     */
    private String packageName;
    /**
     * ts类型
     */
    private String tsType;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 自动填充  DEFAULT、INSERT、UPDATE、INSERT_UPDATE
     */
    private String autoFill;
    /**
     * 主键 0：否  1：是
     */
    private boolean primaryPk;
    /**
     * 基类字段 0：否  1：是
     */
    private boolean baseField;
    /**
     * 表单项 0：否  1：是
     */
    private boolean formItem;
    /**
     * 表单必填 0：否  1：是
     */
    private boolean formRequired;
    /**
     * 表单类型
     */
    private String formType;
    /**
     * 表单字典类型
     */
    private String formDict;
    /**
     * 表单效验
     */
    private String formValidator;
    /**
     * 列表项 0：否  1：是
     */
    private boolean gridItem;
    /**
     * 列表排序 0：否  1：是
     */
    private boolean gridSort;
    /**
     * 查询项 0：否  1：是
     */
    private boolean queryItem;
    /**
     * 查询方式
     */
    private String queryType;
    /**
     * 查询表单类型
     */
    private String queryFormType;
    /**
     * 主键策略
     */
    private String extra;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getTableId() {
        return tableId;
    }
    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public String getFieldType() {
        return fieldType;
    }
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
    public String getFieldComment() {
        return fieldComment;
    }
    public void setFieldComment(String fieldComment) {
        this.fieldComment = fieldComment;
    }
    public String getAttrName() {
        return attrName;
    }
    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }
    public String getAttrType() {
        return attrType;
    }
    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTsType() {
        return tsType;
    }

    public void setTsType(String tsType) {
        this.tsType = tsType;
    }

    public Integer getSort() {
        return sort;
    }
    public void setSort(Integer sort) {
        this.sort = sort;
    }
    public String getAutoFill() {
        return autoFill;
    }
    public void setAutoFill(String autoFill) {
        this.autoFill = autoFill;
    }
    public boolean getPrimaryPk() {
        return primaryPk;
    }
    public void setPrimaryPk(boolean primaryPk) {
        this.primaryPk = primaryPk;
    }
    public boolean getBaseField() {
        return baseField;
    }
    public void setBaseField(boolean baseField) {
        this.baseField = baseField;
    }
    public boolean getFormItem() {
        return formItem;
    }
    public void setFormItem(boolean formItem) {
        this.formItem = formItem;
    }
    public boolean getFormRequired() {
        return formRequired;
    }
    public void setFormRequired(boolean formRequired) {
        this.formRequired = formRequired;
    }
    public String getFormType() {
        return formType;
    }
    public void setFormType(String formType) {
        this.formType = formType;
    }
    public String getFormDict() {
        return formDict;
    }
    public void setFormDict(String formDict) {
        this.formDict = formDict;
    }
    public String getFormValidator() {
        return formValidator;
    }
    public void setFormValidator(String formValidator) {
        this.formValidator = formValidator;
    }
    public boolean getGridItem() {
        return gridItem;
    }
    public void setGridItem(boolean gridItem) {
        this.gridItem = gridItem;
    }
    public boolean getGridSort() {
        return gridSort;
    }
    public void setGridSort(boolean gridSort) {
        this.gridSort = gridSort;
    }
    public boolean getQueryItem() {
        return queryItem;
    }
    public void setQueryItem(boolean queryItem) {
        this.queryItem = queryItem;
    }
    public String getQueryType() {
        return queryType;
    }
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }
    public String getQueryFormType() {
        return queryFormType;
    }
    public void setQueryFormType(String queryFormType) {
        this.queryFormType = queryFormType;
    }
    public String getExtra() {
        return extra;
    }
    public void setExtra(String extra) {
        this.extra = extra;
    }
}
