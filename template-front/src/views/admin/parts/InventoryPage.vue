<template>
    <div class="parts-inventory-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <div class="header-left">
                        <span>配件库存列表</span>
                        <el-input
                            v-model="searchName"
                            placeholder="配件名称"
                            style="width: 150px; margin-left: 20px"
                            clearable
                            @clear="loadData"
                            @keyup.enter="loadData"
                        />
                        <el-select v-model="searchCategoryId" placeholder="配件类型" style="width: 150px; margin-left: 10px" clearable @change="loadData">
                            <el-option
                                v-for="item in categoryList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id"
                            />
                        </el-select>
                        <el-input
                            v-model="searchBrand"
                            placeholder="品牌"
                            style="width: 150px; margin-left: 10px"
                            clearable
                            @clear="loadData"
                            @keyup.enter="loadData"
                        />
                        <el-button type="primary" @click="loadData" style="margin-left: 10px"><el-icon><Search /></el-icon></el-button>
                    </div>
                    <el-button type="primary" @click="openCreateDialog">添加配件</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="name" label="配件名称" width="150" show-overflow-tooltip />
                <el-table-column prop="categoryName" label="类型" width="120" />
                <el-table-column prop="brand" label="品牌" width="120" />
                <el-table-column prop="price" label="价格" width="100">
                    <template #default="scope">
                        ¥{{ scope.row.price }}
                    </template>
                </el-table-column>
                <el-table-column prop="quantity" label="库存数量" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.quantity > 10 ? 'success' : 'danger'">{{ scope.row.quantity }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="详情" width="80">
                    <template #default="scope">
                        <el-popover placement="left" :width="300" trigger="click">
                            <template #reference>
                                <el-button size="small" link>查看</el-button>
                            </template>
                            <el-descriptions title="详细信息" :column="1" border>
                                <el-descriptions-item label="简介">{{ scope.row.description }}</el-descriptions-item>
                                <el-descriptions-item label="注意事项">{{ scope.row.precautions }}</el-descriptions-item>
                                <el-descriptions-item label="创建时间">{{ new Date(scope.row.createTime).toLocaleString() }}</el-descriptions-item>
                            </el-descriptions>
                        </el-popover>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="250" fixed="right">
                    <template #default="scope">
                        <el-button size="small" type="success" @click="openInboundDialog(scope.row)">入库</el-button>
                        <el-button size="small" type="warning" @click="openOutboundDialog(scope.row)">销售</el-button>
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

        <!-- Create/Edit Dialog -->
        <el-dialog v-model="showDialog" :title="dialogTitle" width="500px">
            <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
                <el-form-item label="配件名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入配件名称" />
                </el-form-item>
                <el-form-item label="配件类型" prop="categoryId">
                    <el-select v-model="form.categoryId" placeholder="请选择类型" style="width: 100%">
                        <el-option
                            v-for="item in categoryList"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="品牌" prop="brand">
                    <el-input v-model="form.brand" placeholder="请输入品牌" />
                </el-form-item>
                <el-form-item label="价格" prop="price">
                    <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
                </el-form-item>
                <el-form-item label="简介" prop="description">
                    <el-input v-model="form.description" type="textarea" placeholder="请输入简介" />
                </el-form-item>
                <el-form-item label="注意事项" prop="precautions">
                    <el-input v-model="form.precautions" type="textarea" placeholder="请输入注意事项" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">提交</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- Inbound Dialog -->
        <el-dialog v-model="showInboundDialog" title="配件入库" width="400px">
            <el-form :model="inboundForm" ref="inboundFormRef" :rules="inboundRules" label-width="80px">
                <el-form-item label="配件名称">
                    <el-input v-model="currentRow.name" disabled />
                </el-form-item>
                <el-form-item label="入库数量" prop="quantity">
                    <el-input-number v-model="inboundForm.quantity" :min="1" style="width: 100%" />
                </el-form-item>
                <el-form-item label="入库单价" prop="price">
                    <el-input-number v-model="inboundForm.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
                </el-form-item>
                <el-form-item label="供应商" prop="supplierId">
                    <el-select v-model="inboundForm.supplierId" placeholder="请选择供应商" style="width: 100%">
                        <el-option
                            v-for="item in supplierList"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="inboundForm.remark" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showInboundDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitInbound">确认入库</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- Outbound Dialog -->
        <el-dialog v-model="showOutboundDialog" title="配件销售(出库)" width="500px">
            <el-form :model="outboundForm" ref="outboundFormRef" :rules="outboundRules" label-width="100px">
                <el-form-item label="配件名称">
                    <el-input v-model="currentRow.name" disabled />
                </el-form-item>
                <el-form-item label="当前库存">
                    <el-tag>{{ currentRow.quantity }}</el-tag>
                </el-form-item>
                <el-form-item label="销售数量" prop="quantity">
                    <el-input-number v-model="outboundForm.quantity" :min="1" :max="currentRow.quantity" style="width: 100%" />
                </el-form-item>
                <el-form-item label="销售单价" prop="price">
                    <el-input-number v-model="outboundForm.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
                </el-form-item>
                <el-form-item label="关联工单" prop="appointmentId">
                    <el-select v-model="outboundForm.appointmentId" placeholder="请选择维修工单/用户" style="width: 100%" filterable>
                        <el-option
                            v-for="item in activeAppointments"
                            :key="item.id"
                            :label="`${item.carModel} (${item.licensePlate}) - ${item.description}`"
                            :value="item.id"
                        >
                            <span style="float: left">{{ item.carModel }} - {{ item.licensePlate }}</span>
                            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.description }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="outboundForm.remark" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showOutboundDialog = false">取消</el-button>
                    <el-button type="primary" @click="submitOutbound">确认销售</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { post, get } from '@/net'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchName = ref('')
const searchCategoryId = ref(null)
const searchBrand = ref('')
const categoryList = ref([])
const supplierList = ref([])
const activeAppointments = ref([])

const showDialog = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑配件' : '添加配件')
const formRef = ref(null)
const form = reactive({
    id: null,
    name: '',
    categoryId: null,
    brand: '',
    price: 0,
    description: '',
    precautions: ''
})

const rules = {
    name: [{ required: true, message: '请输入配件名称', trigger: 'blur' }],
    categoryId: [{ required: true, message: '请选择配件类型', trigger: 'change' }],
    price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

// Inbound/Outbound
const showInboundDialog = ref(false)
const showOutboundDialog = ref(false)
const currentRow = ref({})
const inboundFormRef = ref(null)
const outboundFormRef = ref(null)

const inboundForm = reactive({
    partId: null,
    quantity: 1,
    price: 0,
    supplierId: null,
    remark: ''
})

const outboundForm = reactive({
    partId: null,
    quantity: 1,
    price: 0,
    appointmentId: null,
    remark: ''
})

const inboundRules = {
    quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
    price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const outboundRules = {
    quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
    price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
    appointmentId: [{ required: true, message: '请选择关联工单', trigger: 'change' }]
}

const loadData = () => {
    loading.value = true
    let url = `/api/admin/parts/inventory/list?page=${currentPage.value}&size=${pageSize.value}`
    if (searchName.value) url += `&name=${searchName.value}`
    if (searchCategoryId.value) url += `&categoryId=${searchCategoryId.value}`
    if (searchBrand.value) url += `&brand=${searchBrand.value}`
    
    get(url, (data) => {
        tableData.value = data.records
        total.value = data.total
        loading.value = false
    })
}

const loadCategories = () => {
    get('/api/admin/parts/category/list?page=1&size=100', (data) => {
        categoryList.value = data.records
    })
}

const loadSuppliers = () => {
    get('/api/admin/supplier/list?page=1&size=100', (data) => {
        supplierList.value = data.records
    })
}

const loadActiveAppointments = () => {
    get('/api/admin/appointment/active', (data) => {
        activeAppointments.value = data
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
    form.categoryId = null
    form.brand = ''
    form.price = 0
    form.description = ''
    form.precautions = ''
    showDialog.value = true
}

const openEditDialog = (row) => {
    isEdit.value = true
    form.id = row.id
    form.name = row.name
    form.categoryId = row.categoryId
    form.brand = row.brand
    form.price = row.price
    form.description = row.description
    form.precautions = row.precautions
    showDialog.value = true
}

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            const url = isEdit.value ? '/api/admin/parts/inventory/update' : '/api/admin/parts/inventory/create'
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
    post('/api/admin/parts/inventory/delete', { id }, () => {
        ElMessage.success('删除成功！')
        loadData()
    }, (message) => {
        ElMessage.warning(message)
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    })
}

// Inbound Operations
const openInboundDialog = (row) => {
    currentRow.value = row
    inboundForm.partId = row.id
    inboundForm.quantity = 1
    inboundForm.price = row.price // Default to current price
    inboundForm.supplierId = null
    inboundForm.remark = ''
    showInboundDialog.value = true
    loadSuppliers()
}

const submitInbound = () => {
    inboundFormRef.value.validate((valid) => {
        if (valid) {
            post('/api/admin/parts/inventory/inbound', inboundForm, () => {
                ElMessage.success('入库成功！')
                showInboundDialog.value = false
                loadData()
            })
        }
    })
}

// Outbound Operations
const openOutboundDialog = (row) => {
    currentRow.value = row
    outboundForm.partId = row.id
    outboundForm.quantity = 1
    outboundForm.price = row.price
    outboundForm.appointmentId = null
    outboundForm.remark = ''
    showOutboundDialog.value = true
    loadActiveAppointments()
}

const submitOutbound = () => {
    outboundFormRef.value.validate((valid) => {
        if (valid) {
            post('/api/admin/parts/inventory/outbound', outboundForm, () => {
                ElMessage.success('销售成功！')
                showOutboundDialog.value = false
                loadData()
            })
        }
    })
}

onMounted(() => {
    loadData()
    loadCategories()
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
