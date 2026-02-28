<template>
    <div class="vehicle-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <span>我的车辆</span>
                    <el-button type="primary" @click="openCreateDialog">添加车辆</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="licensePlate" label="车牌号" width="120" />
                <el-table-column prop="brand" label="品牌" width="100" />
                <el-table-column prop="model" label="型号" width="120" />
                <el-table-column prop="color" label="颜色" width="80" />
                <el-table-column prop="mileage" label="里程(km)" width="100" />
                <el-table-column label="详细信息" min-width="200">
                    <template #default="scope">
                        <el-descriptions :column="1" size="small" border>
                            <el-descriptions-item label="VIN">{{ scope.row.vin || '-' }}</el-descriptions-item>
                            <el-descriptions-item label="发动机号">{{ scope.row.engineNumber || '-' }}</el-descriptions-item>
                            <el-descriptions-item label="年份">{{ scope.row.year || '-' }}</el-descriptions-item>
                        </el-descriptions>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="scope">
                        <el-button size="small" type="success" @click="openAppointmentDialog(scope.row)">预约</el-button>
                        <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
                        <el-popconfirm title="确定删除这辆车吗？" @confirm="handleDelete(scope.row.id)">
                            <template #reference>
                                <el-button size="small" type="danger">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="showDialog" :title="dialogTitle" width="500px">
            <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
                <el-form-item label="车牌号" prop="licensePlate">
                    <el-input v-model="form.licensePlate" placeholder="请输入车牌号" />
                </el-form-item>
                <el-form-item label="品牌" prop="brand">
                    <el-select
                        v-model="form.brand"
                        placeholder="选择或输入品牌"
                        filterable
                        allow-create
                        default-first-option
                        style="width: 100%"
                    >
                        <el-option v-for="item in commonBrands" :key="item" :label="item" :value="item" />
                    </el-select>
                </el-form-item>
                <el-form-item label="型号" prop="model">
                    <el-input v-model="form.model" placeholder="请输入车型型号" />
                </el-form-item>
                <el-form-item label="颜色" prop="color">
                    <el-input v-model="form.color" placeholder="车辆颜色" />
                </el-form-item>
                <el-form-item label="车辆识别代号" prop="vin">
                    <el-input v-model="form.vin" placeholder="VIN码" />
                </el-form-item>
                <el-form-item label="发动机号" prop="engineNumber">
                    <el-input v-model="form.engineNumber" placeholder="发动机号" />
                </el-form-item>
                <el-form-item label="里程(km)" prop="mileage">
                    <el-input-number v-model="form.mileage" :min="0" style="width: 100%" />
                </el-form-item>
                <el-form-item label="年份" prop="year">
                    <el-date-picker
                        v-model="form.year"
                        type="year"
                        placeholder="选择年份"
                        value-format="YYYY"
                        style="width: 100%"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">保存</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- Appointment Dialog -->
        <el-dialog v-model="showAppointmentDialog" title="快速预约" width="500px">
            <el-form :model="appointmentForm" ref="appointmentFormRef" :rules="appointmentRules" label-width="100px">
                <el-form-item label="车型">
                    <el-input v-model="appointmentForm.carModel" disabled />
                </el-form-item>
                <el-form-item label="车牌号">
                    <el-input v-model="appointmentForm.licensePlate" disabled />
                </el-form-item>
                <el-form-item label="预约时间" prop="appointmentTime">
                    <el-date-picker
                        v-model="appointmentForm.appointmentTime"
                        type="datetime"
                        placeholder="选择预约时间"
                        :disabled-date="disabledDate"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item label="服务类型" prop="serviceType">
                    <el-select v-model="appointmentForm.serviceType" placeholder="请选择服务类型" style="width: 100%">
                        <el-option label="定期保养" value="定期保养" />
                        <el-option label="故障维修" value="故障维修" />
                        <el-option label="车辆检查" value="车辆检查" />
                        <el-option label="其他" value="其他" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="description">
                    <el-input v-model="appointmentForm.description" type="textarea" placeholder="请输入备注信息" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showAppointmentDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitAppointment">提交预约</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { get, post } from '@/net'
import { ElMessage } from 'element-plus'

const commonBrands = [
    "大众", "丰田", "本田", "日产", "福特", "雪佛兰", "别克", "宝马", "奔驰", "奥迪",
    "现代", "起亚", "马自达", "标致", "雪铁龙", "吉利", "比亚迪", "长城", "长安", "奇瑞",
    "特斯拉", "蔚来", "小鹏", "理想", "保时捷", "雷克萨斯", "沃尔沃", "路虎", "凯迪拉克"
]

const tableData = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑车辆信息' : '添加车辆')
const formRef = ref(null)

const showAppointmentDialog = ref(false)
const appointmentFormRef = ref(null)
const appointmentForm = reactive({
    carModel: '',
    licensePlate: '',
    appointmentTime: '',
    serviceType: '',
    description: ''
})

const appointmentRules = {
    appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
    serviceType: [{ required: true, message: '请选择服务类型', trigger: 'change' }]
}

const disabledDate = (time) => {
    return time.getTime() < Date.now()
}

const form = reactive({
    id: null,
    licensePlate: '',
    vin: '',
    engineNumber: '',
    brand: '',
    model: '',
    color: '',
    mileage: 0,
    year: ''
})

const rules = {
    licensePlate: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
    brand: [{ required: true, message: '请输入或选择品牌', trigger: 'change' }],
    model: [{ required: true, message: '请输入型号', trigger: 'blur' }]
}

const loadData = () => {
    loading.value = true
    get(`/api/user/vehicle/list?_t=${Date.now()}`, (data) => {
        tableData.value = data
        loading.value = false
    })
}

const openCreateDialog = () => {
    isEdit.value = false
    form.id = null
    form.licensePlate = ''
    form.vin = ''
    form.engineNumber = ''
    form.brand = ''
    form.model = ''
    form.color = ''
    form.mileage = 0
    form.year = ''
    showDialog.value = true
}

const openEditDialog = (row) => {
    isEdit.value = true
    form.id = row.id
    form.licensePlate = row.licensePlate
    form.vin = row.vin
    form.engineNumber = row.engineNumber
    form.brand = row.brand
    form.model = row.model
    form.color = row.color
    form.mileage = row.mileage
    form.year = row.year
    showDialog.value = true
}

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            const url = isEdit.value ? '/api/user/vehicle/update' : '/api/user/vehicle/create'
            post(url, form, () => {
                ElMessage.success(isEdit.value ? '更新成功！' : '添加成功！')
                showDialog.value = false
                loadData()
            })
        }
    })
}

const handleDelete = (id) => {
    post('/api/user/vehicle/delete', { id }, () => {
        ElMessage.success('删除成功！')
        loadData()
    }, (message) => {
        ElMessage.warning(message)
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    })
}

const openAppointmentDialog = (row) => {
    appointmentForm.carModel = `${row.brand} ${row.model}`
    appointmentForm.licensePlate = row.licensePlate
    appointmentForm.appointmentTime = ''
    appointmentForm.serviceType = ''
    appointmentForm.description = ''
    showAppointmentDialog.value = true
}

const submitAppointment = () => {
    appointmentFormRef.value.validate((valid) => {
        if (valid) {
            post('/api/appointment/create', appointmentForm, () => {
                ElMessage.success('预约成功！')
                showAppointmentDialog.value = false
                appointmentFormRef.value.resetFields()
            })
        }
    })
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.vehicle-container {
    padding: 20px;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
