<template>
    <div class="user-coupon-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>我的优惠券</span>
                </div>
            </template>
            <el-empty v-if="couponList.length === 0" description="暂无优惠券" />
            <el-row :gutter="20" v-else>
                <el-col :span="8" v-for="item in couponList" :key="item.id" style="margin-bottom: 20px">
                    <div class="coupon-card" :class="{ 'expired': item.status === 2, 'used': item.status === 1 }">
                        <div class="coupon-left">
                            <div class="amount">
                                <span class="symbol">¥</span>
                                <span class="value">{{ item.discountAmount }}</span>
                            </div>
                            <div class="condition">满 {{ item.conditionAmount }} 可用</div>
                        </div>
                        <div class="coupon-right">
                            <div class="name">{{ item.name }}</div>
                            <div class="date">有效期至: {{ new Date(item.validEnd).toLocaleDateString() }}</div>
                            <div class="status-tag">
                                <el-tag v-if="item.status === 0" type="success" size="small">未使用</el-tag>
                                <el-tag v-else-if="item.status === 1" type="info" size="small">已使用</el-tag>
                                <el-tag v-else type="danger" size="small">已过期</el-tag>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/net'

const couponList = ref([])

const loadCoupons = () => {
    get(`/api/coupon/my?_t=${Date.now()}`, (data) => {
        couponList.value = data
    })
}

onMounted(() => {
    loadCoupons()
})
</script>

<style scoped>
.user-coupon-container {
    padding: 20px;
}
.coupon-card {
    display: flex;
    background: linear-gradient(135deg, #fff5f5 0%, #ffffff 100%);
    border: 1px solid #ffcccc;
    border-radius: 8px;
    overflow: hidden;
    height: 100px;
    transition: all 0.3s;
    cursor: pointer;
}
.coupon-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
}
.coupon-left {
    width: 100px;
    background-color: #ff6b6b;
    color: #fff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 10px;
}
.amount {
    font-size: 24px;
    font-weight: bold;
}
.symbol {
    font-size: 14px;
    margin-right: 2px;
}
.condition {
    font-size: 12px;
    margin-top: 5px;
    opacity: 0.9;
}
.coupon-right {
    flex: 1;
    padding: 10px 15px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}
.name {
    font-weight: bold;
    font-size: 16px;
    color: #333;
}
.date {
    font-size: 12px;
    color: #909399;
}
.status-tag {
    text-align: right;
}

/* Styles for used/expired coupons */
.coupon-card.used .coupon-left, .coupon-card.expired .coupon-left {
    background-color: #909399;
}
.coupon-card.used, .coupon-card.expired {
    border-color: #dcdfe6;
    background: #f5f7fa;
}
.coupon-card.used .name, .coupon-card.expired .name {
    color: #909399;
}
</style>
