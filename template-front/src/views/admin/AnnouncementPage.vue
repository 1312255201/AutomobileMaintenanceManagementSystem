<template>
    <div class="announcement-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <span>公告活动管理</span>
                    <el-button type="primary" @click="openCreateDialog">发布公告/活动</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="title" label="标题" />
                <el-table-column prop="type" label="类型" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.type === 'notice' ? 'info' : 'success'">
                            {{ scope.row.type === 'notice' ? '公告' : '活动' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
                            {{ scope.row.status === 1 ? '已发布' : '草稿' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="180">
                    <template #default="scope">
                        {{ new Date(scope.row.createTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                    <template #default="scope">
                        <el-button size="small" type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
                        <el-popconfirm title="确定删除吗？" @confirm="handleDelete(scope.row)">
                            <template #reference>
                                <el-button size="small" type="danger">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="showDialog" :title="isEdit ? '编辑公告' : '发布公告'" width="800px" destroy-on-close>
            <el-form :model="form" ref="formRef" :rules="rules" label-width="80px">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="form.title" placeholder="请输入标题" />
                </el-form-item>
                <el-form-item label="类型" prop="type">
                    <el-radio-group v-model="form.type">
                        <el-radio label="notice">公告通知</el-radio>
                        <el-radio label="activity">优惠活动</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="封面图" prop="cover" v-if="form.type === 'activity'">
                     <el-upload
                        class="avatar-uploader"
                        action="#"
                        :show-file-list="false"
                        :http-request="uploadCover"
                        :before-upload="beforeUpload"
                    >
                        <img v-if="form.cover" :src="getImageUrl(form.cover)" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <div style="height: 400px; width: 100%">
                        <QuillEditor 
                            ref="editor" 
                            v-model:content="form.content" 
                            contentType="html" 
                            theme="snow" 
                            toolbar="full"
                        />
                    </div>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-switch
                        v-model="form.status"
                        :active-value="1"
                        :inactive-value="0"
                        active-text="立即发布"
                        inactive-text="存为草稿"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/net'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'

const tableData = ref([])
const loading = ref(false)
const showDialog = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
    id: null,
    title: '',
    content: '',
    cover: '',
    type: 'notice',
    status: 1
})

const rules = {
    title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
    content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
    type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const loadData = () => {
    loading.value = true
    get('/api/announcement/list/all', (data) => {
        tableData.value = data
        loading.value = false
    }, () => {
        loading.value = false
    })
}

const openCreateDialog = () => {
    isEdit.value = false
    form.id = null
    form.title = ''
    form.content = ''
    form.cover = ''
    form.type = 'notice'
    form.status = 1
    showDialog.value = true
}

const openEditDialog = (row) => {
    isEdit.value = true
    form.id = row.id
    form.title = row.title
    form.content = row.content
    form.cover = row.cover
    form.type = row.type
    form.status = row.status
    showDialog.value = true
}

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            const url = isEdit.value ? '/api/announcement/update' : '/api/announcement/create'
            post(url, form, () => {
                ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
                showDialog.value = false
                loadData()
            })
        }
    })
}

const handleDelete = (row) => {
    get(`/api/announcement/delete/${row.id}`, () => {
        ElMessage.success('删除成功')
        loadData()
    })
}

const getImageUrl = (path) => {
    if (!path) return ''
    if (path.startsWith('http')) return path
    if (path.startsWith('/images')) return `http://localhost:8080${path}`
    return `http://localhost:8080/images${path}`
}

const beforeUpload = (file) => {
    if (file.type.indexOf('image/') === -1) {
        ElMessage.error('只能上传图片文件')
        return false
    }
    if (file.size / 1024 / 1024 > 5) {
        ElMessage.error('图片大小不能超过5MB')
        return false
    }
    return true
}

const uploadCover = (param) => {
    const formData = new FormData()
    formData.append('file', param.file)
    const authData = localStorage.getItem('authorize') || sessionStorage.getItem('authorize')
    const token = authData ? JSON.parse(authData).token : null
    const config = {
        headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${token}`
        }
    }
    axios.post('/api/image/cache', formData, config).then(res => {
        if(res.data.code === 200) {
            form.cover = res.data.data
        } else {
            ElMessage.warning(res.data.message)
        }
    })
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
