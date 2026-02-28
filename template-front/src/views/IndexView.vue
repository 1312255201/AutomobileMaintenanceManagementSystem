<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-aside width="200px">
        <el-menu
          router
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
          style="height: 100%"
        >
          <el-menu-item index="/index/appointment">
            <el-icon><Calendar /></el-icon>
            <span>线上预约</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="text-align: right; font-size: 12px; display: flex; align-items: center; justify-content: flex-end;">
          <div class="toolbar">
            <el-dropdown>
              <span class="el-dropdown-link" style="cursor: pointer; display: flex; align-items: center;">
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
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { logout } from '@/net'
import router from "@/router";
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { Calendar, Setting } from '@element-plus/icons-vue'

const route = useRoute();
const activeMenu = computed(() => route.path);

function userLogout() {
  logout(() => router.push("/"))
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
}
</style>
