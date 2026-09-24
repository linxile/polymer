<template>
	<el-dialog v-model="dialogVisible" :title="title" :close-on-click-modal="false" draggable @closed="handleClosed">
		<el-form ref="dataFormRef" :model="form" :rules="rules" label-width="100px" @keyup.enter="submitForm()">
			<#list formList as field>
				<#if field.formType == 'text'>
					<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
						<el-input v-model="form.${field.attrName}" placeholder="${field.fieldComment!}"></el-input>
					</el-form-item>
				<#elseif field.formType == 'textarea'>
					<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
						<el-input type="textarea" v-model="form.${field.attrName}"></el-input>
					</el-form-item>
				<#elseif field.formType == 'editor'>
					<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
						<WangEditor :key="editorKey" v-model="form.${field.attrName}" :style="'height: 300px'" placeholder="请输入..."></WangEditor>
					</el-form-item>
				<#elseif field.formType == 'select'>
					<#if field.formDict??>
						<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
							<fast-select v-model="form.${field.attrName}" dict-type="${field.formDict}" placeholder="${field.fieldComment!}"></fast-select>
						</el-form-item>
					<#else>
						<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
							<el-select v-model="form.${field.attrName}" placeholder="请选择">
								<el-option label="请选择" value="0"></el-option>
							</el-select>
						</el-form-item>
					</#if>
				<#elseif field.formType == 'radio'>
					<#if field.formDict??>
						<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
							<fast-radio-group v-model="form.${field.attrName}" dict-type="${field.formDict}"></fast-radio-group>
						</el-form-item>
					<#else>
						<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
							<el-radio-group v-model="form.${field.attrName}">
								<el-radio :value="0" label="启用" />
								<el-radio :value="1" label="禁用" />
							</el-radio-group>
						</el-form-item>
					</#if>
				<#elseif field.formType == 'checkbox'>
					<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
						<el-checkbox-group v-model="form.${field.attrName}">
							<el-checkbox label="启用" name="type"></el-checkbox>
							<el-checkbox label="禁用" name="type"></el-checkbox>
						</el-checkbox-group>
					</el-form-item>
				<#elseif field.formType == 'date'>
					<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
						<el-date-picker type="date" placeholder="${field.fieldComment!}" v-model="form.${field.attrName}"></el-date-picker>
					</el-form-item>
				<#elseif field.formType == 'datetime'>
					<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
						<el-date-picker type="datetime" placeholder="${field.fieldComment!}" v-model="form.${field.attrName}"></el-date-picker>
					</el-form-item>
				<#elseif field.formType == 'inputNumber'>
					<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
						<el-input-number v-model="form.${field.attrName}" :min="0" :max="1000" aria-label="${field.fieldComment!}" placeholder="${field.fieldComment!}" />
					</el-form-item>
				<#elseif field.formType == 'selectUser'>
					<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
						<div class="user-selection">
							<div v-if="selectedUser" class="selected-user">
								<span>{{ selectedUser.username }}</span>
								<el-icon @click="clearUser" class="delete-icon">
									<Close />
								</el-icon>
							</div>
							<el-button text bg :icon="Plus" @click="openUserDialog">
								选择${field.fieldComment}
							</el-button>
						</div>
					</el-form-item>
				<#else>
					<el-form-item label="${field.fieldComment!}" prop="${field.attrName}">
						<el-input v-model="form.${field.attrName}" placeholder="${field.fieldComment!}"></el-input>
					</el-form-item>
				</#if>
			</#list>
		</el-form>
		<template #footer>
			<el-button @click="cancel">取消</el-button>
			<el-button type="primary" @click="submitForm()">确定</el-button>
		</template>
		<#list formList as field>
			<#if field.formType == 'selectUser'>
				<user-transfer
						v-model="form.${field.attrName}"
						ref="userTransferRef"
						:title="'选择${field.fieldComment}'"
						:multiple="false"
						@confirm="handleUserSelected"
				/>
			</#if>
		</#list>
	</el-dialog>
</template>

<#assign hasEditor = false />
<#assign hasSelectUser = false />
<#assign firstSelectUserField = '' />
<#list formList as field>
	<#if field.formType == 'editor'>
		<#assign hasEditor = true />
	</#if>
	<#if field.formType == 'selectUser'>
		<#assign hasSelectUser = true />
		<#if firstSelectUserField == ''>
			<#assign firstSelectUserField = field.attrName />
		</#if>
	</#if>
</#list>

<script setup lang="ts" name="${ModuleName}${FunctionName}AddOrUpdate">
	import { ref } from 'vue'
	import { ElMessage } from 'element-plus/es'
	import { get${FunctionName}ById, submit${FunctionName} } from '@/api/${moduleName}/${functionNameKebab}'
	import { ${ModuleName}${FunctionName} } from '@/types/api/${moduleName}/${functionNameKebab}'
	<#if hasSelectUser>
	import { Close, Plus } from '@element-plus/icons-vue'
	import UserTransfer from '@/components/user-transfer/index.vue'
	</#if>
	<#if hasEditor>
	import WangEditor from '@/components/wang-editor/index.vue'
	</#if>

	const emit = defineEmits<{ (e: 'success'): void }>()
	const dataFormRef = ref()
	const dialogVisible = ref<boolean>(false)
	const title = ref<string>("")
	const isEdit = ref<boolean>(false)
	<#if hasEditor>
	const editorKey = ref<number>(0)
	</#if>
	<#if hasSelectUser>
	const userTransferRef = ref()
	const selectedUser = ref<{ id: number; username: string } | null>(null)
	</#if>

	const form = ref<${ModuleName}${FunctionName}>({
		<#list fieldList as field>
		${field.attrName}: undefined<#sep>,
		</#list>
	})

	const rules = {
		<#list formList as field>
		<#if field.formRequired>
		${field.attrName}: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]<#sep>,
		</#if>
		</#list>
	}

	/** 打开弹窗（新增） */
	function open() {
		reset()
		isEdit.value = false
		title.value = "添加${tableComment}"
		dialogVisible.value = true
	}

	/** 打开弹窗（修改） */
	function openWithData(id: number) {
		reset()
		isEdit.value = true
		title.value = "修改${tableComment}"
		get${FunctionName}ById(id).then(response => {
			form.value = response.data!
			dialogVisible.value = true
		})
	}

	/** 关闭弹窗 */
	function cancel() {
		dialogVisible.value = false
	}

	/** 弹窗关闭后重置表单 */
	function handleClosed() {
		reset()
	}

	/** 对外暴露方法 */
	defineExpose({
		open,
		openWithData
	})

	/** 表单重置 */
	function reset() {
		<#if hasEditor>
		editorKey.value += 1
		</#if>
		form.value = {
			<#list fieldList as field>
			${field.attrName}: undefined<#sep>,
			</#list>
		}
		<#if hasSelectUser>
		selectedUser.value = null
		</#if>
		if (dataFormRef.value) {
			dataFormRef.value.resetFields()
		}
	}

	<#if hasSelectUser>
	/** 打开选择对话框 */
	function openUserDialog() {
		userTransferRef.value.open(form.value.${firstSelectUserField})
	}

	/** 处理选择结果 */
	function handleUserSelected(user: any) {
		if (user) {
			selectedUser.value = {
				id: user.id,
				username: user.username
			}
			form.value.${firstSelectUserField} = user.id
		} else {
			selectedUser.value = null
			form.value.${firstSelectUserField} = undefined
		}
	}

	/** 清除已选择的 */
	function clearUser() {
		selectedUser.value = null
		form.value.${firstSelectUserField} = undefined
	}
	</#if>

	/** 提交按钮 */
	function submitForm() {
		dataFormRef.value.validate((valid: boolean) => {
			if (valid) {
				const msg = isEdit.value ? "修改成功" : "新增成功"
				submit${FunctionName}(form.value).then(() => {
					ElMessage.success(msg)
					dialogVisible.value = false
					emit('success')
				})
			}
		})
	}
</script>

<#if hasSelectUser>
	<style lang="scss" scoped>
		.user-selection {
			display: flex;
			flex-direction: row;
			gap: 10px;

			.selected-user {
				display: flex;
				align-items: center;
				justify-content: space-between;
				padding: 0 10px;
				background-color: #f5f7fa;
				border-radius: 20px;
				border: 0 solid #dcdfe6;

				.delete-icon {
					cursor: pointer;
					color: #f56c6c;
				}

				.delete-icon:hover {
					color: #e4393c;
				}
			}
		}

		.el-button.is-text:not(.is-disabled).is-has-bg {
			background-color: var(--el-fill-color-light);
			border-radius: 20px;
		}
	</style>
</#if>