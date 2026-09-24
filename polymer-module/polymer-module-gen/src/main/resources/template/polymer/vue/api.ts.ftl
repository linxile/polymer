import service from '@/utils/request'
import type { ${ModuleName}${FunctionName}, ${ModuleName}${FunctionName}Query } from '@/types/api/${moduleName}/${functionNameKebab}'
import type { Result, PageResult } from '@/types/api/common'

/**
* API 基础路径
*/
const BASE_URL = '/${moduleName}/${functionName}';

/**
* 获取${tableComment}详情
* @param id ${tableComment}ID
*/
export const get${FunctionName}ById = (id: number): Promise<Result<${ModuleName}${FunctionName}>> => {
return service.get<Result<${ModuleName}${FunctionName}>>(`${'$'}{BASE_URL}/${'$'}{id}`);
};

/**
* 新增/修改${tableComment}
*/
export const submit${FunctionName} = (data: ${ModuleName}${FunctionName}): Promise<Result<${ModuleName}${FunctionName}>> => {
if (data.id) {
return service.put<Result<${ModuleName}${FunctionName}>>(BASE_URL, data)
}
return service.post<Result<${ModuleName}${FunctionName}>>(BASE_URL, data)
}

/**
* 分页查询${tableComment}列表
* @param params 查询参数
*/
export const get${FunctionName}Page = (params: ${ModuleName}${FunctionName}Query): Promise<Result<PageResult<${ModuleName}${FunctionName}>>> => {
return service.get<Result<PageResult<${ModuleName}${FunctionName}>>>(`${'$'}{BASE_URL}/page`, { params })
}

/**
* 批量删除${tableComment}
* @param ids ${tableComment}ID数组
*/
export const delete${FunctionName}s = (ids: number[]): Promise<Result<string>> => {
	return service.delete<Result<string>>(BASE_URL, { data: ids })
		}