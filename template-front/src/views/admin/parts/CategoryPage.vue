<template>
    <div class="parts-category-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <div class="header-left">
                        <span>配件分类列表</span>
                        <el-input
                            v-model="searchName"
                            placeholder="搜索分类名称"
                            style="width: 200px; margin-left: 20px"
                            clearable
                            @clear="loadData"
                            @keyup.enter="loadData"
                        >
                            <template #append>
                                <el-button @click="loadData"><el-icon><Search /></el-icon></el-button>
                            </template>
                        </el-input>
                    </div>
                    <el-button type="primary" @click="openCreateDialog">添加分类</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="name" label="分类名称" width="200" />
                <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip />
                <el-table-column prop="createTime" label="创建时间" width="180">
                    <template #default="scope">
                        {{ new Date(scope.row.createTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
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
                <el-form-item label="分类名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入分类名称" />
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input v-model="form.description" type="textarea" placeholder="请输入分类描述" :rows="3" />
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
import { Search } from '@element-plus/icons-vue'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchName = ref('')

const showDialog = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑配件分类' : '添加配件分类')
const formRef = ref(null)
const form = reactive({
    id: null,
    name: '',
    description: ''
})

const rules = {
    name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadData = () => {
    loading.value = true
    get(`/api/admin/parts/category/list?page=${currentPage.value}&size=${pageSize.value}&name=${searchName.value}`, (data) => {
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
    form.name = ''
    form.description = ''
    showDialog.value = true
}

const openEditDialog = (row) => {
    isEdit.value = true
    form.id = row.id
    form.name = row.name
    form.description = row.description
    showDialog.value = true
}

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            const url = isEdit.value ? '/api/admin/parts/category/update' : '/api/admin/parts/category/create'
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
    post('/api/admin/parts/category/delete', { id }, () => {
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
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.header-left {
    display: flex;
    align-items: center;
}
.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>
