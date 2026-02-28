<template>
    <div class="maintenance-order-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <span>维修单列表</span>
                    <el-button type="primary" @click="showCreateDialog = true">处理预约(创建维修单)</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="appointmentId" label="预约ID" width="80" />
                <el-table-column prop="repairmanName" label="维修工" width="120" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="totalCost" label="总费用" width="120">
                    <template #default="scope">
                        ¥{{ scope.row.totalCost }}
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180">
                    <template #default="scope">
                        {{ new Date(scope.row.createTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="scope">
                        <el-button size="small" type="primary" @click="viewDetail(scope.row)">详情/维修</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- Create Order Dialog -->
        <el-dialog v-model="showCreateDialog" title="处理预约 - 创建维修单" width="500px">
            <el-form :model="createForm" ref="createFormRef" :rules="createRules" label-width="100px">
                <el-form-item label="选择预约" prop="appointmentId">
                    <el-select v-model="createForm.appointmentId" placeholder="请选择待处理的预约" style="width: 100%" filterable>
                        <el-option
                            v-for="item in pendingAppointments"
                            :key="item.id"
                            :label="`${item.carModel} (${item.licensePlate}) - ${new Date(item.appointmentTime).toLocaleDateString()}`"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="分配维修工" prop="repairmanId">
                    <el-select v-model="createForm.repairmanId" placeholder="请选择维修工" style="width: 100%" filterable>
                        <el-option
                            v-for="item in repairmanList"
                            :key="item.id"
                            :label="`${item.name} (${item.specialization})`"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showCreateDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitCreate">创建维修单</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted, onActivated } from 'vue'
import { get, post } from '@/net'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const showCreateDialog = ref(false)
const pendingAppointments = ref([])
const repairmanList = ref([])

const createFormRef = ref(null)
const createForm = reactive({
    appointmentId: null,
    repairmanId: null
})

const createRules = {
    appointmentId: [{ required: true, message: '请选择预约', trigger: 'change' }],
    repairmanId: [{ required: true, message: '请选择维修工', trigger: 'change' }]
}

const loadData = () => {
    loading.value = true
    get(`/api/admin/maintenance/list?_t=${Date.now()}`, (data) => {
        tableData.value = data
        loading.value = false
    })
}

const loadPendingAppointments = () => {
    // Assuming we have an endpoint to get pending appointments (status = 0)
    // If not, we might need to filter from all appointments or create a new endpoint
    // For now, let's reuse the active appointments endpoint but we might need to filter client-side or update backend
    // Actually, user asked to "process appointment", so we need pending ones.
    // Let's assume we can fetch all and filter, or better, add a query param to appointment list
    get(`/api/admin/appointment/active?_t=${Date.now()}`, (data) => {
        // Filter only status 0 (Pending) for creation
        pendingAppointments.value = data.filter(item => item.status === 0)
    })
}

const loadRepairmen = () => {
    get(`/api/admin/repairman/list?page=1&size=100&_t=${Date.now()}`, (data) => {
        repairmanList.value = data.records
    })
}

const getStatusText = (status) => {
    switch(status) {
        case 0: return '待维修'
        case 1: return '维修中'
        case 2: return '已完成'
        case 3: return '已支付'
        default: return '未知'
    }
}

const getStatusType = (status) => {
    switch(status) {
        case 0: return 'warning'
        case 1: return 'primary'
        case 2: return 'success'
        case 3: return 'success'
        default: return 'info'
    }
}

const submitCreate = () => {
    createFormRef.value.validate((valid) => {
        if (valid) {
            post('/api/admin/maintenance/create', createForm, () => {
                ElMessage.success('维修单创建成功！')
                showCreateDialog.value = false
                loadData()
                loadPendingAppointments() // Refresh list
            })
        }
    })
}

const viewDetail = (row) => {
    ElMessage.info('维修详情功能开发中...')
}

onMounted(() => {
    loadData()
    loadPendingAppointments()
    loadRepairmen()
})

onActivated(() => {
    loadData()
    loadPendingAppointments()
    loadRepairmen()
})
</script>

<style scoped>
.maintenance-order-container {
    
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
