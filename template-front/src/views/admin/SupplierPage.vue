<template>
    <div class="supplier-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <div class="header-left">
                        <span>供应商列表</span>
                        <el-input
                            v-model="searchName"
                            placeholder="搜索供应商名称"
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
                    <el-button type="primary" @click="openCreateDialog">添加供应商</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="name" label="供应商名称" min-width="150" />
                <el-table-column prop="contactName" label="联系人" width="100" />
                <el-table-column prop="contactPhone" label="联系电话" width="120" />
                <el-table-column prop="address" label="公司地址" min-width="200" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="80">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
                            {{ scope.row.status === 0 ? '正常' : '停用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="详情" width="80">
                    <template #default="scope">
                        <el-popover placement="left" :width="400" trigger="click">
                            <template #reference>
                                <el-button size="small" link>查看</el-button>
                            </template>
                            <el-descriptions title="详细信息" :column="1" border>
                                <el-descriptions-item label="邮箱">{{ scope.row.email }}</el-descriptions-item>
                                <el-descriptions-item label="营业执照">{{ scope.row.businessLicense }}</el-descriptions-item>
                                <el-descriptions-item label="税务登记">{{ scope.row.taxRegistration }}</el-descriptions-item>
                                <el-descriptions-item label="合作日期">{{ new Date(scope.row.cooperationDate).toLocaleDateString() }}</el-descriptions-item>
                                <el-descriptions-item label="开户行">{{ scope.row.bankName }}</el-descriptions-item>
                                <el-descriptions-item label="银行账号">{{ scope.row.bankAccount }}</el-descriptions-item>
                                <el-descriptions-item label="开户人">{{ scope.row.accountHolder }}</el-descriptions-item>
                                <el-descriptions-item label="备注">{{ scope.row.remark }}</el-descriptions-item>
                            </el-descriptions>
                        </el-popover>
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
            <el-form :model="form" ref="formRef" :rules="rules" label-width="120px">
                <el-form-item label="供应商名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入供应商名称" />
                </el-form-item>
                <el-form-item label="公司地址" prop="address">
                    <el-input v-model="form.address" placeholder="请输入公司地址" />
                </el-form-item>
                <el-form-item label="联系人" prop="contactName">
                    <el-input v-model="form.contactName" placeholder="姓名" />
                </el-form-item>
                <el-form-item label="联系电话" prop="contactPhone">
                    <el-input v-model="form.contactPhone" placeholder="电话" />
                </el-form-item>
                <el-form-item label="电子邮箱" prop="email">
                    <el-input v-model="form.email" placeholder="邮箱" />
                </el-form-item>
                <el-divider content-position="left">财务与资质信息</el-divider>
                <el-form-item label="营业执照号" prop="businessLicense">
                    <el-input v-model="form.businessLicense" placeholder="请输入营业执照号" />
                </el-form-item>
                <el-form-item label="税务登记号" prop="taxRegistration">
                    <el-input v-model="form.taxRegistration" placeholder="请输入税务登记号" />
                </el-form-item>
                <el-form-item label="合作日期" prop="cooperationDate">
                    <el-date-picker
                        v-model="form.cooperationDate"
                        type="date"
                        placeholder="选择合作日期"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="form.status">
                        <el-radio :label="0">正常</el-radio>
                        <el-radio :label="1">停用</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="开户行" prop="bankName">
                    <el-input v-model="form.bankName" placeholder="请输入开户行" />
                </el-form-item>
                <el-form-item label="银行账号" prop="bankAccount">
                    <el-input v-model="form.bankAccount" placeholder="请输入银行账号" />
                </el-form-item>
                <el-form-item label="开户人" prop="accountHolder">
                    <el-input v-model="form.accountHolder" placeholder="请输入开户人" />
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="form.remark" type="textarea" placeholder="备注信息" />
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
const dialogTitle = computed(() => isEdit.value ? '编辑供应商' : '添加供应商')
const formRef = ref(null)
const form = reactive({
    id: null,
    name: '',
    address: '',
    contactName: '',
    contactPhone: '',
    email: '',
    businessLicense: '',
    taxRegistration: '',
    cooperationDate: '',
    bankAccount: '',
    bankName: '',
    accountHolder: '',
    status: 0,
    remark: ''
})

const rules = {
    name: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
    contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const loadData = () => {
    loading.value = true
    get(`/api/admin/supplier/list?page=${currentPage.value}&size=${pageSize.value}&name=${searchName.value}&_t=${Date.now()}`, (data) => {
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
    form.address = ''
    form.contactName = ''
    form.contactPhone = ''
    form.email = ''
    form.businessLicense = ''
    form.taxRegistration = ''
    form.cooperationDate = ''
    form.bankAccount = ''
    form.bankName = ''
    form.accountHolder = ''
    form.status = 0
    form.remark = ''
    showDialog.value = true
}

const openEditDialog = (row) => {
    isEdit.value = true
    form.id = row.id
    form.name = row.name
    form.address = row.address
    form.contactName = row.contactName
    form.contactPhone = row.contactPhone
    form.email = row.email
    form.businessLicense = row.businessLicense
    form.taxRegistration = row.taxRegistration
    form.cooperationDate = row.cooperationDate
    form.bankAccount = row.bankAccount
    form.bankName = row.bankName
    form.accountHolder = row.accountHolder
    form.status = row.status
    form.remark = row.remark
    showDialog.value = true
}

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            const url = isEdit.value ? '/api/admin/supplier/update' : '/api/admin/supplier/create'
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
    post('/api/admin/supplier/delete', { id }, () => {
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
