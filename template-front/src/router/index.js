import { createRouter, createWebHistory } from 'vue-router'
import { unauthorized, takeRole } from "@/net";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/views/welcome/LoginPage.vue')
                }, {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/views/welcome/RegisterPage.vue')
                }, {
                    path: 'forget',
                    name: 'welcome-forget',
                    component: () => import('@/views/welcome/ForgetPage.vue')
                }
            ]
        }, {
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue'),
            redirect: '/index/appointment',
            children: [
                {
                    path: 'appointment',
                    name: 'index-appointment',
                    component: () => import('@/views/index/AppointmentPage.vue')
                }
            ]
        }, {
            path: '/admin',
            name: 'admin',
            component: () => import('@/views/AdminView.vue'),
            redirect: '/admin/repairman',
            children: [
                {
                    path: 'repairman',
                    name: 'admin-repairman',
                    component: () => import('@/views/admin/RepairmanPage.vue')
                }, {
                    path: 'supplier',
                    name: 'admin-supplier',
                    component: () => import('@/views/admin/SupplierPage.vue')
                }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const isUnauthorized = unauthorized()
    const role = takeRole()
    
    if(to.name.startsWith('welcome') && !isUnauthorized) {
        if(role === 'admin') {
            next('/admin')
        } else {
            next('/index')
        }
    } else if((to.fullPath.startsWith('/index') || to.fullPath.startsWith('/admin')) && isUnauthorized) {
        next('/')
    } else {
        next()
    }
})

export default router
