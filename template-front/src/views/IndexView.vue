<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-aside width="200px">
        <div class="logo">
            <el-image style="width: 40px; height: 40px" src="/favicon.ico" fit="cover" />
            <span class="title">汽车维修</span>
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
          <el-menu-item index="/index/appointment">
            <el-icon><Calendar /></el-icon>
            <span>线上预约</span>
          </el-menu-item>
          <el-sub-menu index="user">
            <template #title>
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </template>
            <el-menu-item index="/index/user/info">个人信息</el-menu-item>
            <el-menu-item index="/index/user/vehicle">车辆管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="text-align: right; font-size: 12px; display: flex; align-items: center; justify-content: flex-end;">
          <div class="toolbar">
            <el-button text @click="switchMode" v-if="role === 'admin'">切换到管理端</el-button>
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
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { logout, takeRole } from '@/net'
import router from "@/router";
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { Calendar, Setting, User } from '@element-plus/icons-vue'

const route = useRoute();
const activeMenu = computed(() => route.path);
const role = ref(takeRole())

function userLogout() {
  logout(() => router.push("/"))
}

function switchMode() {
    router.push('/admin')
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
</style>
