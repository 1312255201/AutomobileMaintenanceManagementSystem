<template>
    <div class="client-info-container">
        <el-card class="info-card">
            <template #header>
                <div class="card-header">
                    <span>个人信息</span>
                    <el-button type="primary" @click="saveInfo">保存修改</el-button>
                </div>
            </template>
            <el-form :model="form" ref="formRef" :rules="rules" label-width="100px" v-loading="loading">
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="form.name" placeholder="请输入您的姓名" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="form.gender">
                        <el-radio :label="1">男</el-radio>
                        <el-radio :label="2">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="form.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="微信号" prop="wechat">
                    <el-input v-model="form.wechat" placeholder="请输入微信号" />
                </el-form-item>
                <el-form-item label="联系地址" prop="address">
                    <el-input v-model="form.address" placeholder="请输入联系地址" type="textarea" />
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { get, post } from '@/net'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const formRef = ref(null)
const form = reactive({
    name: '',
    gender: 1,
    phone: '',
    wechat: '',
    address: ''
})

const rules = {
    name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const loadData = () => {
    loading.value = true
    get(`/api/user/client/info?_t=${Date.now()}`, (data) => {
        if (data) {
            form.name = data.name || ''
            form.gender = data.gender || 1
            form.phone = data.phone || ''
            form.wechat = data.wechat || ''
            form.address = data.address || ''
        }
        loading.value = false
    }, (message) => {
        // If 404 or other error, might mean no info yet, just stop loading
        loading.value = false
    })
}

const saveInfo = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            post('/api/user/client/update', form, () => {
                ElMessage.success('保存成功！')
                loadData()
            })
        }
    })
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.client-info-container {
    padding: 20px;
    max-width: 800px;
    margin: 0 auto;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
