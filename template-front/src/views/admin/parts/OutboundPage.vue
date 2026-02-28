<template>
    <div class="outbound-records-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <span>销售(出库)记录列表</span>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="60" />
                <el-table-column prop="partId" label="配件ID" width="80" />
                <el-table-column prop="quantity" label="销售数量" width="100" />
                <el-table-column prop="price" label="销售单价" width="100">
                    <template #default="scope">
                        ¥{{ scope.row.price }}
                    </template>
                </el-table-column>
                <el-table-column prop="customerName" label="客户姓名" width="150" />
                <el-table-column prop="appointmentId" label="关联工单ID" width="100" />
                <el-table-column prop="createTime" label="销售时间" width="180">
                    <template #default="scope">
                        {{ new Date(scope.row.createTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" show-overflow-tooltip />
                <el-table-column label="操作" width="100" fixed="right">
                    <template #default="scope">
                        <el-popconfirm title="确定删除吗？删除后库存将自动恢复" @confirm="handleDelete(scope.row.id)">
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
    </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { post, get } from '@/net'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = () => {
    loading.value = true
    get(`/api/admin/parts/inventory/outbound/list?page=${currentPage.value}&size=${pageSize.value}&_t=${Date.now()}`, (data) => {
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

const handleDelete = (id) => {
    post('/api/admin/parts/inventory/outbound/delete', { id }, () => {
        ElMessage.success('删除成功，库存已恢复！')
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
.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>
