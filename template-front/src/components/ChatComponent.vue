<template>
    <div class="chat-container">
        <div class="chat-messages" ref="messageBox">
            <div v-if="messages.length === 0" class="empty-chat">
                暂无消息
            </div>
            <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ 'self': msg.isSelf }">
                <div class="message-avatar" v-if="!msg.isSelf">
                    <el-avatar :src="msg.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
                </div>
                <div class="message-content-wrapper">
                    <div class="message-info" v-if="!msg.isSelf">
                        <span class="sender-name">{{ msg.senderName }}</span>
                        <span class="sender-role-tag" v-if="msg.senderRole === 'admin'">管理员</span>
                        <span class="sender-role-tag" v-else-if="msg.senderRole === 'repairman'">维修工</span>
                        <span class="time">{{ new Date(msg.createTime).toLocaleString() }}</span>
                    </div>
                    <div class="message-info self-info" v-else>
                        <span class="time">{{ new Date(msg.createTime).toLocaleString() }}</span>
                        <span class="sender-name">{{ msg.senderName }}</span>
                    </div>
                    
                    <div class="message-bubble" :class="{ 'image-bubble': msg.type === 'image' }">
                        <div v-if="msg.type === 'text'">{{ msg.content }}</div>
                        <el-image 
                            v-else-if="msg.type === 'image'" 
                            :src="getImageUrl(msg.content)" 
                            :preview-src-list="[getImageUrl(msg.content)]"
                            fit="cover"
                            class="chat-image"
                        />
                    </div>
                </div>
                <div class="message-avatar" v-if="msg.isSelf">
                    <el-avatar :src="msg.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
                </div>
            </div>
        </div>
        
        <div class="chat-input-area" v-if="!readOnly">
            <el-upload
                class="upload-demo"
                action="#"
                :show-file-list="false"
                :http-request="uploadImage"
                :before-upload="beforeUpload"
            >
                <el-button icon="Picture" circle />
            </el-upload>
            <el-input
                v-model="newMessage"
                placeholder="请输入消息..."
                @keyup.enter="sendMessage"
                class="input-box"
            />
            <el-button type="primary" @click="sendMessage" :disabled="!newMessage.trim()">发送</el-button>
        </div>
        <div v-else class="read-only-tip">
            订单已结束，无法发送新消息
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { get, post } from '@/net'
import { ElMessage } from 'element-plus'
import axios from "axios";

const props = defineProps({
    orderId: {
        type: Number,
        required: true
    },
    readOnly: {
        type: Boolean,
        default: false
    }
})

const messages = ref([])
const newMessage = ref('')
const messageBox = ref(null)
let timer = null

const getImageUrl = (content) => {
    if (!content) return ''
    // If content starts with /images, just prepend host.
    if (content.startsWith('/images')) {
        return `http://localhost:8080${content}`
    }
    // If content starts with /cache, prepend /images
    if (content.startsWith('/cache')) {
        return `http://localhost:8080/images${content}`
    }
    // Otherwise assume it's relative to images
    return `http://localhost:8080/images/${content}`
}

const scrollToBottom = () => {
    nextTick(() => {
        if (messageBox.value) {
            messageBox.value.scrollTop = messageBox.value.scrollHeight
        }
    })
}

const fetchMessages = () => {
    get(`/api/chat/messages/${props.orderId}`, (data) => {
        // Check if new messages arrived to scroll down?
        // Or just scroll if at bottom?
        // For simplicity, if length changes, scroll.
        const shouldScroll = messages.value.length !== data.length
        messages.value = data
        if (shouldScroll) {
            scrollToBottom()
        }
    })
}

const sendMessage = () => {
    if (!newMessage.value.trim()) return
    
    post('/api/chat/send', {
        orderId: props.orderId,
        content: newMessage.value,
        type: 'text'
    }, () => {
        newMessage.value = ''
        fetchMessages()
    }, (msg) => {
        ElMessage.warning(msg)
    })
}

const beforeUpload = (file) => {
    if (file.type.indexOf('image/') === -1) {
        ElMessage.error('只能上传图片文件')
        return false
    }
    if (file.size / 1024 / 1024 > 10) {
        ElMessage.error('图片大小不能超过10MB')
        return false
    }
    return true
}

const uploadImage = (param) => {
    const formData = new FormData()
    formData.append('file', param.file)
    
    // Use raw axios with existing token logic
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
            const imageUrl = res.data.data;
            
            // Backend returns full path starting with /images/cache/... (since last fix)
            // But if we use getImageUrl, we are prepending http://localhost:8080/images
            // If backend returns /images/cache/..., then result is http://localhost:8080/images/images/cache/... -> WRONG
            
            // Wait, previous backend fix in ImageController prepended "/images".
            // So imageUrl is "/images/cache/..."
            // My getImageUrl function does: `http://localhost:8080/images${content}`
            // Result: `http://localhost:8080/images/images/cache/...`
            
            // I should fix getImageUrl to NOT add /images if content already has it.
            // OR I should rely on what the user said: "content里为图片的地址... /cache/..."
            // User snippet shows: "content": "/cache/20260304/..."
            // This means my previous backend fix (adding /images) MIGHT NOT HAVE APPLIED or user is looking at OLD data.
            // BUT, the user explicitly said "content里为图片的地址... /cache/..." and asked to splice "http://localhost:8080/images".
            // So if content is "/cache/...", and I add "/images", it becomes "/images/cache/...".
            // Then I add host: "http://localhost:8080/images/cache/...".
            
            // Let's adjust getImageUrl to handle both cases to be safe.
            
            post('/api/chat/send', {
                orderId: props.orderId,
                content: imageUrl, 
                type: 'image'
            }, () => {
                fetchMessages()
            })
        } else {
            ElMessage.warning(res.data.message)
        }
    }).catch(err => {
        ElMessage.error('图片上传失败')
    })
}

onMounted(() => {
    fetchMessages()
    timer = setInterval(fetchMessages, 3000) // Poll every 3 seconds
})

onUnmounted(() => {
    if (timer) clearInterval(timer)
})

watch(() => props.orderId, () => {
    fetchMessages()
})
</script>

<style scoped>
.chat-container {
    display: flex;
    flex-direction: column;
    height: 500px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    background-color: #f5f7fa;
}

.chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 15px;
}

.empty-chat {
    text-align: center;
    color: #909399;
    margin-top: 20px;
}

.message-item {
    display: flex;
    margin-bottom: 15px;
}

.message-item.self {
    flex-direction: row-reverse;
}

.message-avatar {
    margin: 0 10px;
}

.message-content-wrapper {
    max-width: 70%;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.message-item.self .message-content-wrapper {
    align-items: flex-end;
}

.message-info {
    display: flex;
    align-items: center;
    font-size: 12px;
    color: #909399;
    margin-bottom: 4px;
    width: 100%;
}

.self-info {
    justify-content: flex-end;
}

.self-info .sender-name {
    margin-left: 5px;
}

.sender-name {
    margin-right: 5px;
    font-weight: bold;
}

.sender-role-tag {
    background-color: #e6a23c;
    color: white;
    padding: 1px 4px;
    border-radius: 2px;
    font-size: 10px;
    margin-right: 5px;
}

.message-bubble {
    background-color: white;
    padding: 10px;
    border-radius: 4px;
    box-shadow: 0 1px 2px rgba(0,0,0,0.1);
    word-break: break-word;
}

.message-item.self .message-bubble {
    background-color: #95ec69;
}

.image-bubble {
    padding: 5px;
    background-color: transparent !important;
    box-shadow: none !important;
}

.chat-image {
    max-width: 200px;
    max-height: 200px;
    border-radius: 4px;
    cursor: pointer;
}

.chat-input-area {
    padding: 10px;
    background-color: white;
    border-top: 1px solid #dcdfe6;
    display: flex;
    align-items: center;
    gap: 10px;
}

.input-box {
    flex: 1;
}

.read-only-tip {
    text-align: center;
    padding: 10px;
    background-color: #fef0f0;
    color: #f56c6c;
}
</style>
