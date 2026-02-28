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
                <el-table-column prop="repairmanName" label="维修工" min-width="120" />
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
                <el-table-column prop="createTime" label="创建时间" min-width="180">
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
        <!-- Detail/Repair Dialog -->
        <el-dialog v-model="showDetailDialog" title="维修单详情与操作" width="800px" destroy-on-close>
            <div v-if="currentOrder">
                <el-descriptions title="基本信息" :column="2" border>
                    <el-descriptions-item label="维修单ID">{{ currentOrder.id }}</el-descriptions-item>
                    <el-descriptions-item label="预约ID">{{ currentOrder.appointmentId }}</el-descriptions-item>
                    <el-descriptions-item label="维修工">{{ currentOrder.repairmanName }}</el-descriptions-item>
                    <el-descriptions-item label="当前状态">
                        <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ new Date(currentOrder.createTime).toLocaleString() }}</el-descriptions-item>
                    <el-descriptions-item label="总费用">¥{{ currentOrder.totalCost }}</el-descriptions-item>
                </el-descriptions>

                <div style="margin-top: 20px; display: flex; justify-content: space-between; align-items: center;">
                    <h3>维修项目列表</h3>
                    <div v-if="currentOrder.status < 2">
                        <el-button type="primary" size="small" @click="openAddItemDialog(1)">添加工时</el-button>
                        <el-tooltip content="请前往 [配件管理-库存列表-销售] 进行配件出库并关联此维修单" placement="top">
                            <el-button type="info" size="small">添加配件</el-button>
                        </el-tooltip>
                    </div>
                </div>

                <el-table :data="currentOrder.items" style="width: 100%; margin-top: 10px" border>
                    <el-table-column type="index" label="#" width="50" />
                    <el-table-column prop="itemName" label="项目名称" />
                    <el-table-column prop="itemType" label="类型" width="80">
                        <template #default="scope">
                            <el-tag :type="scope.row.itemType === 1 ? 'info' : 'success'">
                                {{ scope.row.itemType === 1 ? '工时' : '配件' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="quantity" label="数量" width="80" />
                    <el-table-column prop="cost" label="单价/费用" width="100">
                        <template #default="scope">¥{{ scope.row.cost }}</template>
                    </el-table-column>
                    <el-table-column label="小计" width="100">
                        <template #default="scope">
                            ¥{{ (scope.row.cost * scope.row.quantity).toFixed(2) }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="remark" label="备注" show-overflow-tooltip />
                    <el-table-column label="操作" width="80" v-if="currentOrder.status < 2">
                        <template #default="scope">
                            <el-popconfirm title="确定删除此项目吗？" @confirm="deleteItem(scope.row.id)">
                                <template #reference>
                                    <el-button size="small" type="danger" link>删除</el-button>
                                </template>
                            </el-popconfirm>
                        </template>
                    </el-table-column>
                </el-table>

                <div style="margin-top: 20px; text-align: right;" v-if="currentOrder.status < 2">
                     <el-popconfirm title="确定完成维修吗？这将锁定维修单。" @confirm="completeOrder">
                        <template #reference>
                            <el-button type="success" size="large">完成维修</el-button>
                        </template>
                    </el-popconfirm>
                </div>
                 <div style="margin-top: 20px; text-align: right;" v-if="currentOrder.status === 2">
                     <el-popconfirm title="确定用户已支付吗？" @confirm="payOrder">
                        <template #reference>
                            <el-button type="warning" size="large">确认支付</el-button>
                        </template>
                    </el-popconfirm>
                </div>
            </div>
        </el-dialog>

        <!-- Add Item Dialog -->
        <el-dialog v-model="showAddItemDialog" :title="addItemTitle" width="500px">
            <el-form :model="itemForm" ref="itemFormRef" :rules="itemRules" label-width="100px">
                <el-form-item label="项目名称" prop="itemName" v-if="itemForm.itemType === 1">
                    <el-input v-model="itemForm.itemName" placeholder="请输入工时项目名称" />
                </el-form-item>
                
                <el-form-item label="选择配件" prop="partId" v-if="itemForm.itemType === 2">
                    <el-select 
                        v-model="itemForm.partId" 
                        placeholder="请选择配件" 
                        style="width: 100%" 
                        filterable 
                        remote
                        :remote-method="searchParts"
                        :loading="partLoading"
                        @change="handlePartSelect"
                    >
                        <el-option
                            v-for="item in partOptions"
                            :key="item.id"
                            :label="`${item.name} (库存:${item.quantity}) - ¥${item.price}`"
                            :value="item.id"
                            :disabled="item.quantity <= 0"
                        />
                    </el-select>
                </el-form-item>

                <el-form-item label="单价/费用" prop="cost">
                    <el-input-number v-model="itemForm.cost" :min="0" :precision="2" style="width: 100%" :disabled="itemForm.itemType === 2" />
                </el-form-item>
                <el-form-item label="数量" prop="quantity">
                    <el-input-number v-model="itemForm.quantity" :min="1" :max="maxQuantity" style="width: 100%" />
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="itemForm.remark" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showAddItemDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitAddItem">添加</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted, onActivated, computed } from 'vue'
import { get, post } from '@/net'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const showCreateDialog = ref(false)
const pendingAppointments = ref([])
const repairmanList = ref([])

// Detail/Repair
const showDetailDialog = ref(false)
const currentOrder = ref(null)
const showAddItemDialog = ref(false)
const addItemTitle = computed(() => itemForm.itemType === 1 ? '添加工时项目' : '添加配件')
const itemFormRef = ref(null)
const itemForm = reactive({
    orderId: null,
    itemName: '',
    itemType: 1, // 1: Labor, 2: Part
    cost: 0,
    quantity: 1,
    remark: '',
    partId: null
})
const itemRules = {
    itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
    partId: [{ required: true, message: '请选择配件', trigger: 'change' }],
    cost: [{ required: true, message: '请输入费用', trigger: 'blur' }],
    quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}
const partOptions = ref([])
const partLoading = ref(false)
const maxQuantity = ref(999)

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
    get(`/api/admin/maintenance/detail?id=${row.id}&_t=${Date.now()}`, (data) => {
        currentOrder.value = data
        showDetailDialog.value = true
    })
}

const openAddItemDialog = (type) => {
    itemForm.orderId = currentOrder.value.id
    itemForm.itemType = type
    itemForm.itemName = ''
    itemForm.cost = 0
    itemForm.quantity = 1
    itemForm.remark = ''
    itemForm.partId = null
    maxQuantity.value = 999
    
    if (type === 2) {
        // Pre-load parts or reset
        partOptions.value = []
        searchParts('')
    }
    
    showAddItemDialog.value = true
}

const searchParts = (query) => {
    partLoading.value = true
    // Need an endpoint to search inventory parts
    // Assuming we can use the list endpoint with search params
    let url = `/api/admin/parts/inventory/list?page=1&size=20`
    if (query) url += `&name=${query}`
    
    get(url, (data) => {
        partOptions.value = data.records
        partLoading.value = false
    })
}

const handlePartSelect = (val) => {
    const part = partOptions.value.find(item => item.id === val)
    if (part) {
        itemForm.itemName = part.name
        itemForm.cost = part.price // Default sale price
        maxQuantity.value = part.quantity
        itemForm.quantity = 1
    }
}

const submitAddItem = () => {
    itemFormRef.value.validate((valid) => {
        if (valid) {
            post('/api/admin/maintenance/item/add', itemForm, () => {
                ElMessage.success('添加成功')
                showAddItemDialog.value = false
                // Refresh detail
                viewDetail(currentOrder.value)
                loadData() // Refresh list for total cost update
            })
        }
    })
}

const deleteItem = (itemId) => {
    post('/api/admin/maintenance/item/delete', { id: itemId }, () => {
        ElMessage.success('删除成功')
        viewDetail(currentOrder.value)
        loadData()
    }, (message) => {
        ElMessage.warning(message)
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    })
}

const completeOrder = () => {
    post('/api/admin/maintenance/complete', { id: currentOrder.value.id }, () => {
        ElMessage.success('维修单已完成')
        viewDetail(currentOrder.value)
        loadData()
    }, (message) => {
        ElMessage.warning(message)
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    })
}

const payOrder = () => {
    post('/api/admin/maintenance/pay', { id: currentOrder.value.id }, () => {
        ElMessage.success('订单已支付')
        viewDetail(currentOrder.value)
        loadData()
    }, (message) => {
        ElMessage.warning(message)
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    })
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
