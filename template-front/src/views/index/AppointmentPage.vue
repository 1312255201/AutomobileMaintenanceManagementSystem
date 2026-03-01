<template>
    <div class="appointment-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <span>我的预约列表</span>
                    <el-button type="primary" @click="showCreateDialog = true">发起预约</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="carModel" label="车型" width="120" />
                <el-table-column prop="licensePlate" label="车牌号" width="120" />
                <el-table-column prop="appointmentTime" label="预约时间" width="180">
                    <template #default="scope">
                        {{ new Date(scope.row.appointmentTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column prop="serviceType" label="服务类型" width="120" />
                <el-table-column prop="description" label="备注" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                    <template #default="scope">
                        <el-button 
                            v-if="scope.row.status === 2" 
                            size="small" 
                            type="primary" 
                            @click="openPayDialog(scope.row)"
                        >
                            去支付
                        </el-button>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180">
                    <template #default="scope">
                        {{ new Date(scope.row.createTime).toLocaleString() }}
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

        <el-dialog v-model="showCreateDialog" title="发起预约" width="500px">
            <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
                <el-form-item label="车型" prop="carModel">
                    <el-input v-model="form.carModel" placeholder="请输入车型" />
                </el-form-item>
                <el-form-item label="车牌号" prop="licensePlate">
                    <el-input v-model="form.licensePlate" placeholder="请输入车牌号" />
                </el-form-item>
                <el-form-item label="预约时间" prop="appointmentTime">
                    <el-date-picker
                        v-model="form.appointmentTime"
                        type="datetime"
                        placeholder="选择预约时间"
                        :disabled-date="disabledDate"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item label="服务类型" prop="serviceType">
                    <el-select v-model="form.serviceType" placeholder="请选择服务类型" style="width: 100%">
                        <el-option label="定期保养" value="定期保养" />
                        <el-option label="故障维修" value="故障维修" />
                        <el-option label="车辆检查" value="车辆检查" />
                        <el-option label="其他" value="其他" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="description">
                    <el-input v-model="form.description" type="textarea" placeholder="请输入备注信息" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showCreateDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">提交</el-button>
                </span>
            </template>
        </el-dialog>
        <el-dialog v-model="showPayDialog" title="支付订单" width="500px">
            <el-descriptions title="维修单详情" :column="1" border>
                <el-descriptions-item label="车型">{{ currentOrder.carModel }}</el-descriptions-item>
                <el-descriptions-item label="车牌">{{ currentOrder.licensePlate }}</el-descriptions-item>
                <el-descriptions-item label="维修工">{{ currentOrder.repairmanName }}</el-descriptions-item>
                <el-descriptions-item label="原始金额">
                    <span style="font-weight: bold">¥{{ currentOrder.totalCost }}</span>
                </el-descriptions-item>
            </el-descriptions>
            
            <div style="margin-top: 20px">
                <el-form label-width="100px">
                    <el-form-item label="选择优惠券">
                        <el-select v-model="selectedCouponId" placeholder="请选择优惠券" style="width: 100%" clearable>
                            <el-option
                                v-for="item in availableCoupons"
                                :key="item.id"
                                :label="`${item.name} (省¥${item.discountAmount})`"
                                :value="item.id"
                            >
                                <span style="float: left">{{ item.name }}</span>
                                <span style="float: right; color: #8492a6; font-size: 13px">
                                    减 ¥{{ item.discountAmount }}
                                </span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="应付金额">
                        <span style="font-size: 20px; color: #f56c6c; font-weight: bold">
                            ¥{{ calculateFinalAmount() }}
                        </span>
                    </el-form-item>
                </el-form>
            </div>
            
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showPayDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitPay">确认支付</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { post, get } from '@/net'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showCreateDialog = ref(false)
const formRef = ref(null)
const form = reactive({
    carModel: '',
    licensePlate: '',
    appointmentTime: '',
    serviceType: '',
    description: ''
})

const showPayDialog = ref(false)
const currentOrder = ref({})
const availableCoupons = ref([])
const selectedCouponId = ref(null)

const rules = {
    carModel: [{ required: true, message: '请输入车型', trigger: 'blur' }],
    licensePlate: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
    appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
    serviceType: [{ required: true, message: '请选择服务类型', trigger: 'change' }]
}

const disabledDate = (time) => {
    return time.getTime() < Date.now()
}

const loadData = () => {
    loading.value = true
    get(`/api/appointment/list?page=${currentPage.value}&size=${pageSize.value}`, (data) => {
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

const getStatusText = (status) => {
    switch(status) {
        case 0: return '待确认'
        case 1: return '已确认'
        case 2: return '已完成(待支付)'
        case 3: return '已取消'
        case 4: return '已支付'
        default: return '未知'
    }
}

const getStatusType = (status) => {
    switch(status) {
        case 0: return 'warning'
        case 1: return 'primary'
        case 2: return 'danger'
        case 3: return 'info'
        case 4: return 'success'
        default: return 'info'
    }
}

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            post('/api/appointment/create', form, () => {
                ElMessage.success('预约成功！')
                showCreateDialog.value = false
                formRef.value.resetFields()
                loadData()
            })
        } else {
            return false
        }
    })
}

const openPayDialog = (row) => {
    // Assuming appointment ID maps to Maintenance Order?
    // We need to fetch the maintenance order details for this appointment.
    // However, the backend doesn't have a direct "getOrderByAppointmentId" endpoint.
    // But wait, the user clicks "Pay" on an Appointment row.
    // The requirement says: "When maintenance order is completed, show bill info to user, allow user to pay with coupon".
    // So we need to find the maintenance order associated with this appointment.
    
    // Let's first get the order detail. We might need a new endpoint or search.
    // Or we can assume the appointment list includes orderId? No.
    // Let's add a helper to fetch active order for this appointment or just list orders.
    // Actually, maybe we should list Maintenance Orders for the user instead of Appointments?
    // But the user asked to "show bill info... allow user to pay".
    
    // Let's try to get the order detail by appointment ID.
    // I'll create a new endpoint in Backend: /api/appointment/order/{appointmentId}
    // OR, I can fetch the user's maintenance orders.
    
    // For now, let's assume we can get the order details.
    // I'll add a new endpoint in AppointmentController/Service.
    get(`/api/appointment/order/${row.id}`, (data) => {
        currentOrder.value = data
        selectedCouponId.value = null
        showPayDialog.value = true
        loadCoupons()
    }, (msg) => {
        ElMessage.warning(msg)
    })
}

const loadCoupons = () => {
    get(`/api/coupon/my?_t=${Date.now()}`, (data) => {
        // Filter valid coupons
        availableCoupons.value = data.filter(c => c.status === 0 && new Date(c.validEnd) > new Date())
    })
}

const calculateFinalAmount = () => {
    if (!currentOrder.value.totalCost) return 0
    let cost = currentOrder.value.totalCost
    if (selectedCouponId.value) {
        const coupon = availableCoupons.value.find(c => c.id === selectedCouponId.value)
        if (coupon) {
            if (cost >= coupon.conditionAmount) {
                cost = cost - coupon.discountAmount
            }
        }
    }
    return cost < 0 ? 0 : cost
}

const submitPay = () => {
    post('/api/appointment/pay', {
        orderId: currentOrder.value.id,
        couponId: selectedCouponId.value
    }, () => {
        ElMessage.success('支付成功！')
        showPayDialog.value = false
        loadData()
    })
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.appointment-container {
    padding: 20px;
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
