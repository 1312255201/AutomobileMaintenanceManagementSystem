<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-aside width="200px">
        <div class="logo">
            <el-image style="width: 40px; height: 40px" src="/favicon.ico" fit="cover" />
            <span class="title">后台管理</span>
        </div>
        <el-menu
          router
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
          style="height: calc(100% - 60px)"
        >
          <el-menu-item index="/admin/repairman">
            <el-icon><User /></el-icon>
            <span>维修工管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="text-align: right; font-size: 12px; display: flex; align-items: center; justify-content: flex-end;">
          <div class="toolbar">
            <el-button text @click="switchMode">切换到用户端</el-button>
            <el-dropdown>
              <span class="el-dropdown-link" style="cursor: pointer; display: flex; align-items: center; margin-left: 10px">
                <el-icon style="margin-right: 8px"><Setting /></el-icon>
                <span>操作</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="userLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
            <el-tabs v-model="activeTab" type="card" class="demo-tabs" @tab-click="handleTabClick" closable @tab-remove="removeTab">
                <el-tab-pane
                    v-for="item in tabs"
                    :key="item.path"
                    :label="item.title"
                    :name="item.path"
                >
                </el-tab-pane>
            </el-tabs>
            <div class="main-content">
                <router-view v-slot="{ Component }">
                    <keep-alive>
                        <component :is="Component" />
                    </keep-alive>
                </router-view>
            </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { logout } from '@/net'
import router from "@/router";
import { computed, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { User, Setting } from '@element-plus/icons-vue'

const route = useRoute();
const activeMenu = computed(() => route.path);
const activeTab = ref(route.path);
const tabs = ref([]);

watch(route, (to) => {
    activeTab.value = to.path;
    const exist = tabs.value.find(t => t.path === to.path);
    if (!exist) {
        let title = '未命名';
        if (to.path.includes('repairman')) title = '维修工管理';
        // Add more mappings as needed
        tabs.value.push({
            title: title,
            path: to.path
        });
    }
}, { immediate: true });

function handleTabClick(tab) {
    router.push(tab.props.name);
}

function removeTab(targetName) {
    const tabsList = tabs.value;
    let activeName = activeTab.value;
    if (activeName === targetName) {
        tabsList.forEach((tab, index) => {
            if (tab.path === targetName) {
                const nextTab = tabsList[index + 1] || tabsList[index - 1];
                if (nextTab) {
                    activeName = nextTab.path;
                }
            }
        });
    }
    activeTab.value = activeName;
    tabs.value = tabsList.filter(tab => tab.path !== targetName);
    router.push(activeName);
}

function userLogout() {
  logout(() => router.push("/"))
}

function switchMode() {
    router.push('/index')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.el-header {
  background-color: #fff;
  color: var(--el-text-color-primary);
  border-bottom: 1px solid #dcdfe6;
}
.el-aside {
  color: var(--el-text-color-primary);
  background: #545c64;
}
.el-main {
    background-color: #f0f2f5;
    padding: 0;
    display: flex;
    flex-direction: column;
}
.logo {
    height: 60px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #434a50;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
}
.title {
    margin-left: 10px;
}
.demo-tabs {
    background-color: #fff;
    padding-top: 5px;
    padding-left: 10px;
    border-bottom: 1px solid #dcdfe6;
}
.main-content {
    flex: 1;
    padding: 20px;
    overflow: auto;
}
</style>
