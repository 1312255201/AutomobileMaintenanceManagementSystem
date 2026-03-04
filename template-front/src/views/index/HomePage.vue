<template>
    <div class="home-container">
        <el-row :gutter="20">
            <el-col :span="16">
                <el-card class="box-card" v-for="item in announcementList" :key="item.id" style="margin-bottom: 20px">
                    <template #header>
                        <div class="card-header">
                            <span class="announcement-title">
                                <el-tag :type="item.type === 'notice' ? 'info' : 'success'" style="margin-right: 10px">
                                    {{ item.type === 'notice' ? '公告' : '活动' }}
                                </el-tag>
                                {{ item.title }}
                            </span>
                            <span class="time">{{ new Date(item.createTime).toLocaleDateString() }}</span>
                        </div>
                    </template>
                    <div class="announcement-content">
                        <div v-if="item.cover" class="cover-image">
                            <el-image :src="getImageUrl(item.cover)" fit="cover" style="width: 100%; height: 200px; border-radius: 4px" />
                        </div>
                        <div class="ql-editor" v-html="item.content"></div>
                    </div>
                </el-card>
                <el-empty v-if="announcementList.length === 0" description="暂无公告活动" />
            </el-col>
            <el-col :span="8">
                <el-card class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>门店信息</span>
                        </div>
                    </template>
                    <div class="store-info">
                        <p><strong>营业时间：</strong> 09:00 - 18:00</p>
                        <p><strong>联系电话：</strong> 010-12345678</p>
                        <p><strong>门店地址：</strong> 北京市朝阳区某某街道123号</p>
                        <el-divider />
                        <p style="text-align: center; color: #666">欢迎光临，我们将竭诚为您服务！</p>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/net'
import '@vueup/vue-quill/dist/vue-quill.snow.css'; // Import styles for content rendering

const announcementList = ref([])

const loadData = () => {
    get('/api/announcement/list/published', (data) => {
        announcementList.value = data
    })
}

const getImageUrl = (path) => {
    if (!path) return ''
    if (path.startsWith('http')) return path
    if (path.startsWith('/images')) return `http://localhost:8080${path}`
    return `http://localhost:8080/images${path}`
}

onMounted(() => {
    loadData()
})
</script>

<style scoped>
.home-container {
    padding: 20px;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.announcement-title {
    font-size: 16px;
    font-weight: bold;
    display: flex;
    align-items: center;
}
.time {
    font-size: 12px;
    color: #999;
}
.cover-image {
    margin-bottom: 15px;
}
.store-info p {
    margin-bottom: 10px;
    line-height: 1.5;
}
/* Ensure quill content renders correctly */
.ql-editor {
    padding: 0;
    min-height: auto;
}
</style>
