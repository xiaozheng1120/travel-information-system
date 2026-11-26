import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
// 用户前台页面
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Spot from '../views/Spot.vue'
import Guide from '../views/Guide.vue'
import Hotel from '../views/Hotel.vue'
import Ticket from '../views/Ticket.vue'
import Order from '../views/Order.vue'
// 前台订单详情页
import OrderDetail from '../views/OrderDetail.vue'
// 收银台（支付）页面
import Pay from '../views/Pay.vue'
// 新增注册页面
import Register from '../views/Register.vue'
// 前台个人中心
import Profile from '../views/Profile.vue'
// ========== 后台外层布局 + 所有后台页面导入 ==========
import AdminIndex from '../views/admin/AdminIndex.vue'
import AdminHome from '../views/admin/AdminHome.vue'
import AdminSpot from '../views/admin/AdminSpot.vue'
import AdminTicket from '../views/admin/AdminTicket.vue'
import AdminGuide from '../views/admin/AdminGuide.vue'
import AdminUser from '../views/admin/AdminUser.vue'
import AdminOrder from '../views/admin/AdminOrder.vue'
import AdminHotel from '../views/admin/AdminHotel.vue'
import AdminBanner from '../views/admin/AdminBanner.vue'
import AdminCategory from '../views/admin/AdminCategory.vue'
const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'login', component: Login, meta: { transition: 'auth' } },
  { path: '/register', name: 'register', component: Register, meta: { transition: 'auth' } },
  { path: '/home', name: 'home', component: Home },
  { path: '/spot', name: 'spot', component: Spot },
  { path: '/guide', name: 'guide', component: Guide },
  { path: '/hotel', name: 'hotel', component: Hotel },
  { path: '/ticket', name: 'ticket', component: Ticket },
  // 新增我的订单路由
  { path: '/order', name: 'order', component: Order },
  // 订单详情页（从我的订单 / 个人中心点进来）
  { path: '/orderDetail/:id', name: 'orderDetail', component: OrderDetail },
  // 个人中心：我的订单 + 我的收藏 + 资料设置
  { path: '/profile', name: 'profile', component: Profile },
  // 新增收银台路由（门票 / 酒店预订后跳这里支付）
  { path: '/pay', name: 'pay', component: Pay },
  // ========== 后台路由（嵌套路由，外层AdminIndex布局） ==========
  {
    path: '/admin',
    component: AdminIndex,
    redirect: '/admin/home',
    children: [
      { path: 'home', component: AdminHome, meta: { title: '后台首页' } },
      { path: 'spot', component: AdminSpot, meta: { title: '景点管理' } },
      { path: 'ticket', component: AdminTicket, meta: { title: '门票管理' } },
      { path: 'guide', component: AdminGuide, meta: { title: '攻略管理' } },
      { path: 'user', component: AdminUser, meta: { title: '用户管理' } },
      { path: 'order', component: AdminOrder, meta: { title: '订单管理' } },
      { path: 'hotel', component: AdminHotel, meta: { title: '住宿管理' } },
      { path: 'banner', component: AdminBanner, meta: { title: '轮播图管理', role: '管理员' } },
      { path: 'category', component: AdminCategory, meta: { title: '分类管理' } },
    ]
  },
  // 详情页面（spotDetail动态路由已经写好，直接可用）
  {
    path: '/guideDetail/:id',
    name: 'GuideDetail',
    component: () => import('../views/GuideDetail.vue')
  },
  {
    path: '/spotDetail/:id',
    name: 'SpotDetail',
    component: () => import('../views/SpotDetail.vue')
  },
  // 新增住宿详情路由
   {
    path: '/hotelDetail',
    name: 'HotelDetail',
    component: () => import('../views/HotelDetail.vue')
  }
]
const router = createRouter({
  history: createWebHistory(),
  routes,
  // 新增滚动行为配置
  scrollBehavior(to, from, savedPosition) {
    // 浏览器返回前进时，保留原来滚动位置；新页面跳转回到顶部
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0, left: 0 }
    }
  }
})
// ===================== 全局路由守卫 =====================
router.beforeEach((to, from, next) => {
  // 读取本地存储的用户信息
  const userInfoStr = sessionStorage.getItem('userInfo')
  let userInfo = null
  if (userInfoStr) {
    userInfo = JSON.parse(userInfoStr)
  }
  // 访问登录、注册页面，不需要登录
  if (to.path === '/login' || to.path === '/register') {
    // 如果已经登录，不能再回登录页，直接跳首页
    if (userInfo) {
      next('/home')
    } else {
      next()
    }
  } else if (to.path.startsWith('/admin')) {
    // 访问后台页面：必须登录 并且角色是【管理员】
    if (userInfo && userInfo.role === '管理员') {
      next()
    } else {
      ElMessage.warning('权限不足，您不是管理员，无法访问后台！')
      next('/home')
    }
  } else {
    // 访问前台页面（home/spot/ticket/order等），需要登录
    if (userInfo) {
      next()
    } else {
      ElMessage.warning('请先登录')
      next('/login')
    }
  }
})
export default router
