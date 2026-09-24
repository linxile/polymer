import type { PageParam<#if baseClass??>, BaseEntity</#if> } from "../common";

/**
* ${tableComment}分页查询参数
*/
export interface ${ModuleName}${FunctionName}Query extends PageParam {
<#list queryList as field>
    <#if field.queryType != 'between'>
        /** ${field.fieldComment!} */
        ${field.attrName}?: ${field.tsType};
    <#else>
        /** 开始${field.fieldComment!} */
        begin${field.attrName?cap_first}?: string;
        /** 结束${field.fieldComment!} */
        end${field.attrName?cap_first}?: string;
    </#if>
</#list>
}

/**
* ${tableComment}信息
*/
export interface ${ModuleName}${FunctionName}<#if baseClass??> extends BaseEntity</#if> {
<#list fieldList as field>
    <#if !field.baseField>
        /** ${field.fieldComment!} */
        ${field.attrName}?: ${field.tsType};
    </#if>
</#list>
}