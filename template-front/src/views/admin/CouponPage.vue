<template>
    <div class="coupon-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <div class="header-left">
                        <span>优惠券发放管理</span>
                        <el-select 
                            v-model="searchUserId" 
                            placeholder="筛选用户" 
                            style="width: 200px; margin-left: 20px" 
                            clearable 
                            filterable
                            @change="loadData"
                        >
                            <el-option
                                v-for="item in userList"
                                :key="item.id"
                                :label="`${item.username} (${item.email})`"
                                :value="item.id"
                            />
                        </el-select>
                        <el-button type="primary" @click="loadData" style="margin-left: 10px"><el-icon><Search /></el-icon></el-button>
                    </div>
                    <el-button type="primary" @click="openCreateDialog">发放优惠券</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="name" label="优惠券名称" min-width="150" show-overflow-tooltip />
                <el-table-column prop="username" label="所属用户" width="120" />
                <el-table-column label="面值/门槛" width="150">
                    <template #default="scope">
                        <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.discountAmount }}</span>
                        <span style="font-size: 12px; color: #909399; margin-left: 5px">
                            (满{{ scope.row.conditionAmount }}可用)
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="有效期" width="280">
                    <template #default="scope">
                        {{ new Date(scope.row.validStart).toLocaleDateString() }} 至 {{ new Date(scope.row.validEnd).toLocaleDateString() }}
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发放时间" width="180">
                    <template #default="scope">
                        {{ new Date(scope.row.createTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="100" fixed="right">
                    <template #default="scope">
                        <el-popconfirm title="确定撤销此优惠券吗？" @confirm="handleDelete(scope.row.id)">
                            <template #reference>
                                <el-button size="small" type="danger" :disabled="scope.row.status === 1">撤销</el-button>
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

        <!-- Create Dialog -->
        <el-dialog v-model="showDialog" title="发放优惠券" width="600px">
            <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
                <el-form-item label="优惠券名称" prop="name">
                    <el-input v-model="form.name" placeholder="如：新用户立减券" />
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="优惠金额" prop="discountAmount">
                            <el-input-number v-model="form.discountAmount" :min="0.01" :precision="2" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="使用门槛" prop="conditionAmount">
                            <el-input-number v-model="form.conditionAmount" :min="0" :precision="2" style="width: 100%" placeholder="0为无门槛" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="有效期(天)" prop="validDays">
                    <el-input-number v-model="form.validDays" :min="1" :max="365" style="width: 100%" />
                </el-form-item>
                <el-form-item label="选择用户" prop="userIds">
                    <el-select 
                        v-model="form.userIds" 
                        multiple 
                        placeholder="请选择要发放的用户(可多选)" 
                        style="width: 100%"
                        filterable
                        collapse-tags
                    >
                        <el-option
                            v-for="item in userList"
                            :key="item.id"
                            :label="`${item.username} (${item.email})`"
                            :value="item.id"
                        />
                    </el-select>
                    <div style="margin-top: 5px">
                        <el-button size="small" @click="selectAllUsers">全选用户</el-button>
                        <el-button size="small" @click="form.userIds = []">清空选择</el-button>
                    </div>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">确认发放</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted, onActivated } from 'vue'
import { post, get } from '@/net'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchUserId = ref(null)
const userList = ref([])

const showDialog = ref(false)
const formRef = ref(null)
const form = reactive({
    name: '',
    discountAmount: 10,
    conditionAmount: 0,
    validDays: 30,
    userIds: []
})

const rules = {
    name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
    discountAmount: [{ required: true, message: '请输入优惠金额', trigger: 'blur' }],
    validDays: [{ required: true, message: '请输入有效期天数', trigger: 'blur' }],
    userIds: [{ required: true, message: '请选择至少一个用户', trigger: 'change' }]
}

const loadData = () => {
    loading.value = true
    let url = `/api/coupon/list?page=${currentPage.value}&size=${pageSize.value}&_t=${Date.now()}`
    if (searchUserId.value) url += `&userId=${searchUserId.value}`
    
    get(url, (data) => {
        tableData.value = data.records
        total.value = data.total
        loading.value = false
    })
}

const loadUsers = () => {
    get(`/api/coupon/users?_t=${Date.now()}`, (data) => {
        userList.value = data
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

const getStatusText = (status) => {
    switch(status) {
        case 0: return '未使用'
        case 1: return '已使用'
        case 2: return '已过期'
        default: return '未知'
    }
}

const getStatusType = (status) => {
    switch(status) {
        case 0: return 'success'
        case 1: return 'info'
        case 2: return 'danger'
        default: return 'info'
    }
}

const openCreateDialog = () => {
    form.name = ''
    form.discountAmount = 10
    form.conditionAmount = 0
    form.validDays = 30
    form.userIds = []
    showDialog.value = true
    loadUsers()
}

const selectAllUsers = () => {
    form.userIds = userList.value.map(user => user.id)
}

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            post('/api/coupon/create', form, () => {
                ElMessage.success(`成功发放给 ${form.userIds.length} 位用户！`)
                showDialog.value = false
                loadData()
            })
        }
    })
}

const handleDelete = (id) => {
    post('/api/coupon/delete', { id }, () => {
        ElMessage.success('撤销成功！')
        loadData()
    }, (message) => {
        ElMessage.warning(message)
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    })
}

onMounted(() => {
    loadData()
    loadUsers()
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
