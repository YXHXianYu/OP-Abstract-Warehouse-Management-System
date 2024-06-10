import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Base from '../views/Base.vue'
import Home from '../components/Home.vue'
import Ware from '../components/Ware.vue'
import Area from '../components/Area.vue'
import Self from '../components/Self.vue'
import Cargo from '../components/Cargo.vue'
import Goods from '../components/Goods.vue'
import Stock from '../components/Stock.vue'
import WorkList from '../components/WorkList.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: '/login',
      component: Login
    },
    {
      path: '/',
      name:'/',
      component: Base,
      children:[
        {
          path: '/home',
          name: '/home',
          component: Home
        },
        {
          path: '/ware',
          name: '/ware',
          component: Ware
        },
        {
          path: '/area',
          name: '/area',
          component: Area
        },
        {
          path: '/self',
          name: '/self',
          component: Self
        },
        {
          path: '/cargo',
          name: '/cargo',
          component: Cargo
        },
        {
          path: '/goods',
          name: '/goods',
          component: Goods
        },
        {
          path: '/stock',
          name: '/stock',
          component: Stock
        },
        {
          path: '/worklist',
          name: '/worklist',
          component: WorkList
        },
      ]
    },
  ]
})

router.beforeEach((to, from, next) => {
  if (to.path === '/login') return next();
  const tokenStr = window.localStorage.getItem('optoken');
  if (!tokenStr) return next('/login');
  next();
})


export default router
