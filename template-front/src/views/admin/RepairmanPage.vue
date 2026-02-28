<template>
    <div class="repairman-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <span>维修工列表</span>
                    <el-button type="primary" @click="openCreateDialog">添加维修工</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="username" label="用户名" width="120" />
                <el-table-column prop="name" label="姓名" width="120" />
                <el-table-column prop="phone" label="手机号" width="150" />
                <el-table-column prop="email" label="邮箱" width="180" />
                <el-table-column prop="specialization" label="擅长领域" />
                <el-table-column label="操作" width="180">
                    <template #default="scope">
                        <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
                        <el-popconfirm title="确定删除吗？" @confirm="handleDelete(scope.row.id)">
                            <template #reference>
                                <el-button size="small" type="danger">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </el-card>

        <el-dialog v-model="showDialog" :title="dialogTitle" width="500px">
            <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
                <el-form-item label="用户名" prop="username" v-if="!isEdit">
                    <el-input v-model="form.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="密码" prop="password" v-if="!isEdit">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
                </el-form-item>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="form.name" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="form.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="form.email" placeholder="请输入邮箱" :disabled="isEdit" />
                </el-form-item>
                <el-form-item label="擅长领域" prop="specialization">
                    <el-input v-model="form.specialization" placeholder="请输入擅长领域" />
                </el-form-item>
                <el-form-item label="个人介绍" prop="introduction">
                    <el-input v-model="form.introduction" type="textarea" placeholder="请输入个人介绍" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">提交</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed, onActivated } from 'vue'
import { post, get } from '@/net'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showDialog = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑维修工' : '添加维修工')
const formRef = ref(null)
const form = reactive({
    id: null,
    username: '',
    password: '',
    name: '',
    phone: '',
    email: '',
    specialization: '',
    introduction: ''
})

const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
    ]
}

const loadData = () => {
    loading.value = true
    get(`/api/admin/repairman/list?page=${currentPage.value}&size=${pageSize.value}&_t=${Date.now()}`, (data) => {
        tableData.value = data.records
        total.value = data.total
        loading.value = false
    })
}

const handleSizeChange = (val) => {
    pageSize.value = val
    loadData()
}

const handleCurrentChange = (val) => {
    currentPage.value = val
    loadData()
}

const openCreateDialog = () => {
    isEdit.value = false
    form.id = null
    form.username = ''
    form.password = ''
    form.name = ''
    form.phone = ''
    form.email = ''
    form.specialization = ''
    form.introduction = ''
    showDialog.value = true
}

const openEditDialog = (row) => {
    isEdit.value = true
    form.id = row.id
    form.username = row.username
    form.name = row.name
    form.phone = row.phone
    form.email = row.email
    form.specialization = row.specialization
    form.introduction = row.introduction
    showDialog.value = true
}

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            const url = isEdit.value ? '/api/admin/repairman/update' : '/api/admin/repairman/create'
            post(url, form, () => {
                ElMessage.success(isEdit.value ? '更新成功！' : '创建成功！')
                showDialog.value = false
                loadData()
            })
        } else {
            return false
        }
    })
}

const handleDelete = (id) => {
    post('/api/admin/repairman/delete', { id }, () => {
        ElMessage.success('删除成功！')
        loadData()
    }, (message) => {
        ElMessage.warning(message)
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    })
}

onMounted(() => {
    loadData()
})

onActivated(() => {
    loadData()
})
</script>

<style scoped>
.repairman-container {
    
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>
