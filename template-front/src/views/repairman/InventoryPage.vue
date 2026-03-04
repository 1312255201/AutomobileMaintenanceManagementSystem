<template>
    <div class="parts-inventory-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <div class="header-left">
                        <span>配件库存销售</span>
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
                <el-table-column label="操作" width="100" fixed="right">
                    <template #default="scope">
                        <el-button size="small" type="warning" @click="openOutboundDialog(scope.row)" :disabled="scope.row.quantity <= 0">销售</el-button>
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
                <el-form-item label="关联维修单" prop="orderId">
                    <el-select v-model="outboundForm.orderId" placeholder="请选择维修工单" style="width: 100%" filterable>
                        <el-option
                            v-for="item in activeOrders"
                            :key="item.id"
                            :label="`#${item.id} - ${item.carModel} (${item.licensePlate}) - ${item.repairmanName}`"
                            :value="item.id"
                        >
                            <span style="float: left">#{{ item.id }} {{ item.carModel }}</span>
                            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.licensePlate }}</span>
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
const searchCategoryId = ref(null)
const searchBrand = ref('')
const categoryList = ref([])
const activeOrders = ref([])

const showOutboundDialog = ref(false)
const currentRow = ref({})
const outboundFormRef = ref(null)

const outboundForm = reactive({
    partId: null,
    quantity: 1,
    price: 0,
    orderId: null,
    remark: ''
})

const outboundRules = {
    quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
    price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
    orderId: [{ required: true, message: '请选择关联维修单', trigger: 'change' }]
}

const loadData = () => {
    loading.value = true
    let url = `/api/parts/inventory/list?page=${currentPage.value}&size=${pageSize.value}&_t=${Date.now()}`
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
    get(`/api/admin/parts/category/list?page=1&size=100&_t=${Date.now()}`, (data) => {
        categoryList.value = data.records
    })
}

const loadActiveOrders = () => {
    // Repairman should only see their own active orders? Or all active orders to add parts?
    // User requirement: "can sell parts". Usually parts can be sold to any active order.
    // Let's use the generic active orders endpoint which lists orders.
    // If repairman, the previous task updated `active` endpoint? No, previous task updated `list`.
    // Let's check `MaintenanceController.getActiveOrders`. It calls `maintenanceOrderService.getActiveOrders()`.
    // `getActiveOrders` in service lists ALL active orders. This is probably fine for parts sales.
    get(`/api/maintenance/active?_t=${Date.now()}`, (data) => {
        activeOrders.value = data
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

// Outbound Operations
const openOutboundDialog = (row) => {
    currentRow.value = row
    outboundForm.partId = row.id
    outboundForm.quantity = 1
    outboundForm.price = row.price
    outboundForm.orderId = null
    outboundForm.remark = ''
    showOutboundDialog.value = true
    loadActiveOrders()
}

const submitOutbound = () => {
    outboundFormRef.value.validate((valid) => {
        if (valid) {
            post('/api/parts/inventory/outbound', outboundForm, () => {
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

onActivated(() => {
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
