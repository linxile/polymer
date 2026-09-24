<template>
	<el-card>
		<el-form :model="queryParams" ref="queryRef" :inline="true">
			<#list queryList as field>
				<el-form-item>
					<#if field.queryFormType == 'text' || field.queryFormType == 'textarea' || field.queryFormType == 'editor'>
						<el-input v-model="queryParams.${field.attrName}" placeholder="${field.fieldComment!}" clearable></el-input>
					<#elseif field.queryFormType == 'select'>
						<#if field.formDict??>
							<fast-select v-model="queryParams.${field.attrName}" dict-type="${field.formDict}" placeholder="${field.fieldComment!}" clearable></fast-select>
						<#else>
							<el-select v-model="queryParams.${field.attrName}" placeholder="${field.fieldComment!}" clearable>
								<el-option label="请选择" value="0"></el-option>
							</el-select>
						</#if>
					<#elseif field.queryFormType == 'radio'>
						<#if field.formDict??>
							<fast-radio-group v-model="queryParams.${field.attrName}" dict-type="${field.formDict}"></fast-radio-group>
						<#else>
							<el-radio-group v-model="queryParams.${field.attrName}">
								<el-radio :value="0" label="单选" />
							</el-radio-group>
						</#if>
					<#elseif field.queryFormType == 'date'>
						<el-date-picker
								v-model="${field.attrName}Ref"
								type="daterange"
								start-placeholder="开始${field.fieldComment!}"
								end-placeholder="结束${field.fieldComment!}"
								value-format="YYYY-MM-DD"
								@change="onChange${field.attrName?cap_first}"
						></el-date-picker>
					<#elseif field.queryFormType == 'datetime'>
						<el-date-picker
								v-model="${field.attrName}Ref"
								type="datetimerange"
								start-placeholder="开始${field.fieldComment!}"
								end-placeholder="结束${field.fieldComment!}"
								value-format="YYYY-MM-DD HH:mm:ss"
								@change="onChange${field.attrName?cap_first}"
						></el-date-picker>
					<#else>
						<el-input v-model="queryParams.${field.attrName}" placeholder="${field.fieldComment!}" clearable></el-input>
					</#if>
				</el-form-item>
			</#list>
			<el-form-item>
				<el-button type="primary" @click="handleQuery">搜索</el-button>
				<el-button @click="resetQuery">重置</el-button>
			</el-form-item>
			<el-form-item>
				<el-button v-auth="'${moduleName}:${functionName}:save'" type="primary" @click="handleAdd">新增</el-button>
			</el-form-item>
			<el-form-item>
				<el-button v-auth="'${moduleName}:${functionName}:delete'" type="danger" :disabled="multiple" @click="handleDelete">删除</el-button>
			</el-form-item>
		</el-form>

		<el-table
				v-loading="loading"
				:data="dataList"
				border
				style="width: 100%"
				ref="tableRef"
				@selection-change="handleSelectionChange"
		>
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<#list gridList as field>
				<#if field.formDict??>
					<fast-table-column prop="${field.attrName}" label="${field.fieldComment!}" dict-type="${field.formDict}"></fast-table-column>
				<#else>
					<el-table-column prop="${field.attrName}" label="${field.fieldComment!}" header-align="center" align="center"></el-table-column>
				</#if>
			</#list>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
				<template #default="scope">
					<el-button v-auth="'${moduleName}:${functionName}:update'" type="primary" link icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
					<el-button v-auth="'${moduleName}:${functionName}:delete'" type="primary" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
				</template>
			</el-table-column>
		</el-table>

		<!-- 分页 -->
		<pagination
				v-show="total > 0"
				:total="total"
				v-model:page="queryParams.pageNo"
				v-model:limit="queryParams.pageSize"
				@pagination="getDataList"
		/>

		<!-- 新增/修改弹窗 -->
		<AddOrUpdate ref="addOrUpdateRef" @success="getDataList"></AddOrUpdate>
	</el-card>
</template>

<script setup lang="ts" name="${ModuleName}${FunctionName}Index">
	import { onMounted, ref } from 'vue'
	import AddOrUpdate from './add-or-update.vue'
	import { ${ModuleName}${FunctionName}, ${ModuleName}${FunctionName}Query } from '@/types/api/${moduleName}/${functionNameKebab}'
	import { delete${FunctionName}s, get${FunctionName}Page } from '@/api/${moduleName}/${functionNameKebab}'
	import { ElMessage, ElMessageBox } from 'element-plus'

	const queryRef = ref()
	const tableRef = ref()

	const dataList = ref<${ModuleName}${FunctionName}[]>([])
	const loading = ref<boolean>(true)
	const ids = ref<number[]>([])
	const multiple = ref<boolean>(true)
	const total = ref<number>(0)

	const queryParams = ref<${ModuleName}${FunctionName}Query>({
		pageNo: 1,
		pageSize: 10<#list queryList as field><#if field.queryType != 'between'>,
		${field.attrName}: undefined<#else>,
		begin${field.attrName?cap_first}: undefined,
		end${field.attrName?cap_first}: undefined</#if></#list>
	})

	<#list queryList as field>
	<#if field.queryType == 'between'>
	/** ${field.fieldComment!}范围选择 */
	const ${field.attrName}Ref = ref<string[]>([])

	</#if>
	</#list>
	// 新增/修改弹窗引用
	const addOrUpdateRef = ref<InstanceType<typeof AddOrUpdate>>()

	/** 查询${tableComment}列表 */
	function getDataList() {
		loading.value = true
		get${FunctionName}Page(queryParams.value).then(response => {
			dataList.value = response.data?.list || []
			total.value = response.data?.total || 0
			loading.value = false
		})
	}

	/** 搜索按钮操作 */
	function handleQuery() {
		queryParams.value.pageNo = 1
		getDataList()
	}

	/** 重置按钮操作 */
	function resetQuery() {
		queryRef.value.resetFields()
		<#list queryList as field>
		<#if field.queryType == 'between'>
		${field.attrName}Ref.value = []
		queryParams.value.begin${field.attrName?cap_first} = undefined
		queryParams.value.end${field.attrName?cap_first} = undefined
		</#if>
		</#list>
		handleQuery()
	}

	/** 多选框选中数据 */
	function handleSelectionChange(selection: ${ModuleName}${FunctionName}[]) {
		ids.value = selection.map(item => item.id!)
		multiple.value = !selection.length
	}

	/** 新增按钮操作 */
	function handleAdd() {
		addOrUpdateRef.value?.open()
	}

	/** 修改按钮操作 */
	function handleUpdate(row: ${ModuleName}${FunctionName}) {
		addOrUpdateRef.value?.openWithData(row.id!)
	}

	/** 删除按钮操作 */
	function handleDelete(row?: ${ModuleName}${FunctionName}) {
		const deleteIds = row?.id !== undefined ? [row.id] : (ids.value || [])
		if (deleteIds.length === 0) {
			ElMessage.warning('请选择删除记录')
			return
		}
		ElMessageBox.confirm('是否确认删除编号为"' + deleteIds + '"的数据项？').then(function() {
			return delete${FunctionName}s(deleteIds)
		}).then(() => {
			getDataList()
			ElMessage.success("删除成功")
		}).catch(() => {})
	}

	<#list queryList as field>
	<#if field.queryType == 'between'>
	/** ${field.fieldComment!}范围选择变更处理 */
	function onChange${field.attrName?cap_first}(value: string[] | null) {
		if (value && value.length === 2) {
			queryParams.value.begin${field.attrName?cap_first} = value[0]
			queryParams.value.end${field.attrName?cap_first} = value[1]
		} else {
			queryParams.value.begin${field.attrName?cap_first} = undefined
			queryParams.value.end${field.attrName?cap_first} = undefined
		}
	}

	</#if>
	</#list>
	// 页面初始化
	onMounted(() => {
		getDataList()
	})
</script>