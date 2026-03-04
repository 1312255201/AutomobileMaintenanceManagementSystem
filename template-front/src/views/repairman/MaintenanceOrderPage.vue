<template>
    <div class="repairman-maintenance-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <span>我的维修任务</span>
                    <el-button type="primary" @click="loadData">刷新</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="carModel" label="车型" min-width="120" />
                <el-table-column prop="licensePlate" label="车牌" width="100" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="分配时间" min-width="160">
                    <template #default="scope">
                        {{ new Date(scope.row.createTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                    <template #default="scope">
                        <el-button size="small" type="primary" @click="viewDetail(scope.row)">查看/处理</el-button>
                        <el-button size="small" type="info" @click="openChatDialog(scope.row)">聊天</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- Chat Dialog -->
        <el-dialog v-model="showChatDialog" title="在线咨询" width="600px" destroy-on-close>
            <ChatComponent :orderId="chatOrderId" :readOnly="chatReadOnly" v-if="chatOrderId" />
        </el-dialog>

        <!-- Detail/Repair Dialog -->
        <el-dialog v-model="showDetailDialog" title="维修任务详情" width="800px" destroy-on-close>
            <div v-if="currentOrder">
                <el-descriptions title="基本信息" :column="2" border>
                    <el-descriptions-item label="维修单ID">{{ currentOrder.id }}</el-descriptions-item>
                    <el-descriptions-item label="车型">{{ currentOrder.carModel }}</el-descriptions-item>
                    <el-descriptions-item label="车牌">{{ currentOrder.licensePlate }}</el-descriptions-item>
                    <el-descriptions-item label="当前状态">
                        <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="故障描述" :span="2">{{ currentOrder.description }}</el-descriptions-item>
                </el-descriptions>

                <div style="margin-top: 20px; display: flex; justify-content: space-between; align-items: center;">
                    <h3>维修项目</h3>
                    <div v-if="currentOrder.status < 2">
                        <el-button type="primary" size="small" @click="openAddItemDialog(1)">添加工时</el-button>
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
                    <el-popconfirm title="确定完成维修任务吗？" @confirm="completeOrder">
                        <template #reference>
                            <el-button type="success" size="large">完成任务</el-button>
                        </template>
                    </el-popconfirm>
                </div>
            </div>
        </el-dialog>

        <!-- Add Item Dialog (Simplified for Repairman - Only Labor) -->
        <el-dialog v-model="showAddItemDialog" title="添加工时项目" width="500px">
            <el-form :model="itemForm" ref="itemFormRef" :rules="itemRules" label-width="100px">
                <el-form-item label="项目名称" prop="itemName">
                    <el-input v-model="itemForm.itemName" placeholder="请输入工时项目名称" />
                </el-form-item>
                <el-form-item label="工时费" prop="cost">
                    <el-input-number v-model="itemForm.cost" :min="0" :precision="2" style="width: 100%" />
                </el-form-item>
                <el-form-item label="数量" prop="quantity">
                    <el-input-number v-model="itemForm.quantity" :min="1" :max="999" style="width: 100%" />
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
import { reactive, ref, onMounted, onActivated } from 'vue'
import { get, post } from '@/net'
import { ElMessage } from 'element-plus'
import ChatComponent from '@/components/ChatComponent.vue'

const tableData = ref([])
const loading = ref(false)
const showDetailDialog = ref(false)
const currentOrder = ref(null)
const showAddItemDialog = ref(false)
const itemFormRef = ref(null)

const showChatDialog = ref(false)
const chatOrderId = ref(null)
const chatReadOnly = ref(false)

const itemForm = reactive({
    orderId: null,
    itemName: '',
    itemType: 1, // Repairman only adds Labor (Type 1) usually, or needs parts permission?
                 // Requirement: "Repairman can see maintenance order management... check own info"
                 // Assuming they can add labor. Parts usually by admin/inventory manager?
                 // Let's allow Labor for now.
    cost: 0,
    quantity: 1,
    remark: '',
    partId: null
})

const itemRules = {
    itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
    cost: [{ required: true, message: '请输入费用', trigger: 'blur' }],
    quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

const loadData = () => {
    loading.value = true
    get(`/api/maintenance/list?_t=${Date.now()}`, (data) => {
        tableData.value = data
        loading.value = false
    })
}

const getStatusText = (status) => {
    switch(status) {
        case 0: return '待维修'
        case 1: return '维修中'
        case 2: return '待支付' // Completed by repairman
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

const viewDetail = (row) => {
    get(`/api/maintenance/detail?id=${row.id}&_t=${Date.now()}`, (data) => {
        currentOrder.value = data
        showDetailDialog.value = true
    })
}

const openChatDialog = (row) => {
    chatOrderId.value = row.id
    get(`/api/review/order/${row.id}`, (review) => {
        chatReadOnly.value = !!review
        showChatDialog.value = true
    }, () => {
        chatReadOnly.value = false
        showChatDialog.value = true
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
    showAddItemDialog.value = true
}

const submitAddItem = () => {
    itemFormRef.value.validate((valid) => {
        if (valid) {
            post('/api/maintenance/item/add', itemForm, () => {
                ElMessage.success('添加成功')
                showAddItemDialog.value = false
                viewDetail(currentOrder.value)
                loadData()
            })
        }
    })
}

const deleteItem = (itemId) => {
    post('/api/maintenance/item/delete', { id: itemId }, () => {
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
    post('/api/maintenance/complete', { id: currentOrder.value.id }, () => {
        ElMessage.success('维修任务已完成')
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
</style>
