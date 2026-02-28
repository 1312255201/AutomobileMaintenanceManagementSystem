<template>
    <div class="appointment-container">
        <el-card class="appointment-card">
            <template #header>
                <div class="card-header">
                    <span>线上预约</span>
                </div>
            </template>
            <el-form :model="form" ref="formRef" :rules="rules" label-width="120px">
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
                    />
                </el-form-item>
                <el-form-item label="服务类型" prop="serviceType">
                    <el-select v-model="form.serviceType" placeholder="请选择服务类型">
                        <el-option label="定期保养" value="定期保养" />
                        <el-option label="故障维修" value="故障维修" />
                        <el-option label="车辆检查" value="车辆检查" />
                        <el-option label="其他" value="其他" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="description">
                    <el-input v-model="form.description" type="textarea" placeholder="请输入备注信息" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm">提交预约</el-button>
                    <el-button @click="resetForm">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { post } from '@/net'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const form = reactive({
    carModel: '',
    licensePlate: '',
    appointmentTime: '',
    serviceType: '',
    description: ''
})

const rules = {
    carModel: [{ required: true, message: '请输入车型', trigger: 'blur' }],
    licensePlate: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
    appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
    serviceType: [{ required: true, message: '请选择服务类型', trigger: 'change' }]
}

const disabledDate = (time) => {
    return time.getTime() < Date.now()
}

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            post('/api/appointment/create', form, () => {
                ElMessage.success('预约成功！')
                resetForm()
            })
        } else {
            return false
        }
    })
}

const resetForm = () => {
    formRef.value.resetFields()
}
</script>

<style scoped>
.appointment-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}
.appointment-card {
    width: 600px;
}
</style>
