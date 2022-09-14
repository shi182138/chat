import Vue from 'vue'
import VueRouter from 'vue-router'
// import Home from '../views/Home.vue'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Main',
        component: () => import('../views/Main.vue'),
        children: [
            {
                path: '/home',
                name: 'home',
                component: () => import('../views/home')
            },
            {
                path: '/statistics',
                name: 'statistics',
                component: () => import('../views/user/Statistics.vue')
            },
            {
                path: '/userManager',
                name: 'userManager',
                component: () => import('../views/user/UserManager.vue')
            },
            {
                path: '/groupManager',
                name: 'groupManager',
                component: () => import('../views/group/GroupManager.vue')
            },
        ]
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/Login.vue')
    }
]

const router = new VueRouter({
    mode: 'history',
    routes
})



export default router