<template>
    <div class="detail-page">
        <!-- 导航（与我的订单页完全一致） -->
        <div class="nav-header">
            <div class="nav-container">
                <div class="logo">
                    <img src="/logo.jpeg" alt="logo" class="logo-img" />
                    <span class="logo-text">旅游信息系统</span>
                </div>
                <el-menu mode="horizontal" :router="true" class="nav-menu" :default-active="$route.path">
                    <el-menu-item index="/home">
                        <el-icon><House /></el-icon>
                        <span>首页</span>
                    </el-menu-item>
                    <el-menu-item index="/spot">
                        <el-icon><MapLocation /></el-icon>
                        <span>景点</span>
                    </el-menu-item>
                    <el-menu-item index="/guide">
                        <el-icon><Document /></el-icon>
                        <span>攻略</span>
                    </el-menu-item>
                    <el-menu-item index="/hotel">
                        <el-icon><OfficeBuilding /></el-icon>
                        <span>周边住宿</span>
                    </el-menu-item>
                    <el-menu-item index="/ticket">
                        <el-icon><Ticket /></el-icon>
                        <span>门票预订</span>
                    </el-menu-item>
                    <el-menu-item index="/order">
                        <el-icon><List /></el-icon>
                        <span>我的订单</span>
                    </el-menu-item>
                    <el-menu-item index="/profile">
                        <el-icon><User /></el-icon>
                        <span>个人中心</span>
                    </el-menu-item>
                    <el-menu-item v-if="userInfo?.role === '管理员'" index="/admin/home">
                        <el-icon><Setting /></el-icon>
                        <span>后台管理</span>
                    </el-menu-item>
                </el-menu>
                <div class="user-box">
                    <el-image
                        :src="getAvatarSrc(userInfo)"
                        style="width:34px;height:34px;border-radius:50%;object-fit:cover"
                        fit="cover"
                    />
                    <div class="user-name">{{ userInfo?.username }}</div>
                    <el-button text @click="logout">退出登录</el-button>
                </div>
            </div>
        </div>

        <div class="container">
            <div class="detail-wrap">
                <!-- 顶部返回 -->
                <div class="top-bar">
                    <button type="button" class="back-btn" @click="goBack">
                        <el-icon><ArrowLeft /></el-icon>
                        <span>返回我的订单</span>
                    </button>
                    <el-tag v-if="order" :type="statusTagType(statusText)" effect="light" round>
                        {{ statusText }}
                    </el-tag>
                </div>

                <div v-if="order" class="detail-card">
                    <!-- 概览 -->
                    <div class="hero">
                        <div class="hero-icon" :class="isHotel ? 'hotel' : 'ticket'">
                            <el-icon size="26">
                                <component :is="isHotel ? OfficeBuilding : Ticket" />
                            </el-icon>
                        </div>
                        <div class="hero-main">
                            <div class="hero-type">{{ isHotel ? '住宿订单' : '门票订单' }}</div>
                            <div class="hero-name">{{ order.ticketName || order.hotelName || '订单商品' }}</div>
                            <div class="hero-no">订单号 {{ order.orderNo }}</div>
                        </div>
                        <div class="hero-amount">
                            <span class="symbol">¥</span>{{ amountText }}
                        </div>
                    </div>

                    <!-- 状态时间线 -->
                    <div class="timeline">
                        <div v-for="(node, idx) in timeline" :key="idx" class="tl-node" :class="node.state">
                            <div class="tl-dot">
                                <el-icon v-if="node.state === 'done'" size="12"><Select /></el-icon>
                            </div>
                            <div class="tl-text">
                                <b>{{ node.title }}</b>
                                <span>{{ node.time || '—' }}</span>
                            </div>
                            <div v-if="idx < timeline.length - 1" class="tl-line"></div>
                        </div>
                    </div>

                    <!-- 信息区 -->
                    <div class="info-block">
                        <div class="block-title">订单信息</div>
                        <div class="info-grid">
                            <div class="info-item"><span>订单编号</span><b>{{ order.orderNo }}</b></div>
                            <div class="info-item"><span>订单类型</span><b>{{ isHotel ? '酒店住宿' : '景点门票' }}</b></div>
                            <div class="info-item"><span>下单时间</span><b>{{ formatDateTime(order.createTime) || '—' }}</b></div>
                            <div class="info-item"><span>订单状态</span><b>{{ statusText }}</b></div>
                        </div>
                    </div>

                    <div class="info-block">
                        <div class="block-title">{{ isHotel ? '入住信息' : '出行信息' }}</div>
                        <div class="info-grid">
                            <div class="info-item"><span>{{ isHotel ? '住宿名称' : '门票名称' }}</span><b>{{ order.ticketName || order.hotelName || '—' }}</b></div>
                            <div class="info-item"><span>{{ isHotel ? '入住日期' : '出行日期' }}</span><b>{{ formatDate(order.travelDate) || '—' }}</b></div>
                            <div class="info-item"><span>{{ isHotel ? '住宿天数' : '购票数量' }}</span><b>{{ order.count }}{{ isHotel ? ' 晚' : ' 张' }}</b></div>
                            <div class="info-item"><span>{{ isHotel ? '入住人' : '出行人' }}</span><b>{{ order.personName || '—' }}</b></div>
                            <div class="info-item"><span>联系手机</span><b>{{ order.phone || '—' }}</b></div>
                        </div>
                    </div>

                    <div class="info-block">
                        <div class="block-title">金额明细</div>
                        <div class="amount-row"><span>单价</span><b>¥{{ unitPriceText }} × {{ order.count }}{{ isHotel ? ' 晚' : ' 张' }}</b></div>
                        <div class="amount-row total"><span>订单合计</span><b>¥{{ amountText }}</b></div>
                    </div>

                    <!-- 操作区 -->
                    <div class="action-bar">
                        <el-button v-if="canCancel" type="danger" plain @click="handleCancel">取消订单</el-button>
                        <el-button type="primary" @click="rebook">{{ isHotel ? '再订一间住宿' : '再买一次门票' }}</el-button>
                        <el-button @click="goBack">返回列表</el-button>
                    </div>

                    <div class="tips">
                        <el-icon><InfoFilled /></el-icon>
                        <span>{{ isHotel ? '入住当天请携带本人身份证到前台办理登记，如需取消请提前联系酒店。' : '出行当天请携带本人身份证与订单号到景区核销入园，取消后库存会自动返还。' }}</span>
                    </div>
                </div>

                <el-empty v-else-if="!loading" description="没有找到这条订单，可能已经被删除">
                    <el-button type="primary" @click="goBack">返回我的订单</el-button>
                </el-empty>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
    ArrowLeft, House, MapLocation, Document, OfficeBuilding, Ticket, List, User, Setting,
    Select, InfoFilled
} from '@element-plus/icons-vue'
import { getOrderByIdApi, cancelOrder as cancelOrderApi } from '@/api/orderData.js'
import { getOrderDisplayStatus } from '@/utils/orderStatus.js'

const route = useRoute()
const router = useRouter()

const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))
const order = ref(null)
const loading = ref(true)

function getAvatarSrc(user) {
    if (user?.role === '管理员') {
        return '/admin-avatar.jpeg'
    }
    if (user?.avatar) {
        if (user.avatar.startsWith('http') || user.avatar.startsWith('/')) {
            return user.avatar
        }
        return new URL(`../assets/images/${user.avatar}`, import.meta.url).href
    }
    return new URL(`../assets/images/default-avatar.png`, import.meta.url).href
}

const isHotel = computed(() => Number(order.value?.orderType) === 2 || (!order.value?.orderType && !!order.value?.hotelId))
const statusText = computed(() => getOrderDisplayStatus(order.value))
const amountText = computed(() => Number(order.value?.totalPrice || 0).toFixed(2))
const unitPriceText = computed(() => {
    const total = Number(order.value?.totalPrice || 0)
    const count = Number(order.value?.count || 0)
    if (!count) return total.toFixed(2)
    return (total / count).toFixed(2)
})
const canCancel = computed(() => {
    if (!order.value || String(order.value.status) !== '1') return false
    if (!order.value.travelDate) return true
    const d = new Date(order.value.travelDate)
    if (isNaN(d.getTime())) return true
    return d > new Date()
})

const formatDate = (val) => {
    if (!val) return ''
    const d = new Date(val)
    if (isNaN(d.getTime())) return String(val).split('T')[0]
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return `${y}-${m}-${day}`
}
const formatDateTime = (val) => {
    if (!val) return ''
    const d = new Date(val)
    if (isNaN(d.getTime())) return String(val)
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const h = String(d.getHours()).padStart(2, '0')
    const min = String(d.getMinutes()).padStart(2, '0')
    return `${y}-${m}-${day} ${h}:${min}`
}

const timeline = computed(() => {
    if (!order.value) return []
    const created = formatDateTime(order.value.createTime)
    const travel = formatDate(order.value.travelDate)
    const s = String(order.value.status)
    if (statusText.value === '已过期') {
        return [
            { title: '提交订单', time: created, state: 'done' },
            { title: '订单已过期', time: travel ? `${travel} 后未使用` : '未在有效期内使用', state: 'current' }
        ]
    }
    if (s === '3') {
        return [
            { title: '提交订单', time: created, state: 'done' },
            { title: '订单已取消', time: '库存已返还', state: 'current' }
        ]
    }
    if (s === '2') {
        return [
            { title: '提交订单', time: created, state: 'done' },
            { title: '已核销 / 已完成', time: travel || '', state: 'current' }
        ]
    }
    return [
        { title: '提交订单', time: created, state: 'done' },
        { title: '支付成功', time: created, state: 'done' },
        { title: isHotel.value ? '等待入住' : '等待出行', time: travel || '', state: 'current' }
    ]
})

const statusTagType = (text) => {
    if (text === '待出行') return 'primary'
    if (text === '已完成') return 'success'
    if (text === '已取消') return 'info'
    if (text === '已过期') return 'warning'
    return 'danger'
}

async function loadDetail() {
    loading.value = true
    try {
        const res = await getOrderByIdApi(route.params.id)
        order.value = res || null
    } catch (e) {
        console.error('加载订单详情失败', e)
        order.value = null
    } finally {
        loading.value = false
    }
}

function goBack() {
    router.push('/order')
}

async function handleCancel() {
    try {
        await ElMessageBox.confirm('取消后库存会自动返还，确定取消这条订单吗？', '取消订单', {
            confirmButtonText: '确定取消',
            cancelButtonText: '再想想',
            type: 'warning'
        })
    } catch (e) {
        return
    }
    try {
        await cancelOrderApi(order.value.id)
        ElMessage.success('订单已取消，库存已归还')
        await loadDetail()
    } catch (e) {
        ElMessage.error('取消失败，请稍后重试')
    }
}

function rebook() {
    router.push(isHotel.value ? '/hotel' : '/ticket')
}

const logout = () => {
    sessionStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
    router.push('/login')
}

onMounted(loadDetail)
</script>

<style scoped>
.detail-page {
    min-height: 100vh;
    padding-bottom: 40px;
    background:
        radial-gradient(circle at 12% 12%, rgba(14, 124, 134, 0.12), transparent 28%),
        radial-gradient(circle at 88% 18%, rgba(242, 140, 91, 0.1), transparent 32%),
        linear-gradient(180deg, #faf8f5 0%, #f7f4ee 45%, #f3f0e9 100%);
}

.nav-header {
    position: sticky;
    top: 0;
    z-index: 999;
    background: rgba(255, 255, 255, 0.88);
    backdrop-filter: saturate(160%) blur(14px);
    -webkit-backdrop-filter: saturate(160%) blur(14px);
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    box-shadow: 0 6px 24px rgba(15, 60, 70, 0.08);
}
.nav-container {
    width: 92%;
    max-width: 1400px;
    margin: 0 auto;
    height: 72px;
    display: flex;
    align-items: center;
    gap: 24px;
}
.logo {
    display: flex;
    align-items: center;
    gap: 10px;
}
.logo-img {
    width: 38px;
    height: 38px;
    object-fit: cover;
    border-radius: 10px;
    box-shadow: 0 4px 14px rgba(14, 124, 134, 0.35);
}
.logo-text {
    font-size: 20px;
    font-weight: 700;
    color: #1f2937;
    letter-spacing: 0.5px;
}
.nav-menu {
    flex: 1;
}
:deep(.el-menu--horizontal) {
    border-bottom: none !important;
    background: transparent !important;
}
:deep(.el-menu-item) {
    color: #4b5563 !important;
    font-size: 15px;
    font-weight: 500;
    margin: 0 4px;
}
:deep(.el-menu-item:hover) {
    color: #0e7c86 !important;
    background: rgba(14, 124, 134, 0.06) !important;
    border-radius: 8px;
}
:deep(.el-menu-item.is-active) {
    color: #0e7c86 !important;
    background: rgba(14, 124, 134, 0.08) !important;
    border-radius: 8px;
}
:deep(.el-menu-item.is-active .el-icon) {
    color: #0e7c86 !important;
}
:deep(.el-menu--horizontal > .el-menu-item.is-active)::after {
    display: none;
}
.user-box {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 6px 10px 6px 6px;
    border-radius: 999px;
    background: #f7f4ee;
    border: 1px solid rgba(0, 0, 0, 0.04);
}
.user-name {
    color: #1f2937;
    font-size: 14px;
    font-weight: 500;
}

.container {
    width: 92%;
    max-width: 1400px;
    margin: 0 auto;
    padding: 30px 0;
}

.detail-wrap {
    max-width: 900px;
    margin: 0 auto;
}

.top-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
}
.back-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 14px;
    border: 1px solid rgba(14, 124, 134, 0.25);
    background: rgba(255, 255, 255, 0.9);
    border-radius: 999px;
    color: #3c5a80;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s ease;
}
.back-btn:hover {
    color: #0e7c86;
    border-color: rgba(14, 124, 134, 0.5);
}

.detail-card {
    background: #fff;
    border-radius: 20px;
    padding: 28px;
    box-shadow: 0 12px 32px rgba(15, 60, 70, 0.1);
    border: 1px solid rgba(226, 236, 248, 0.9);
}

.hero {
    display: flex;
    align-items: center;
    gap: 18px;
    padding-bottom: 22px;
    border-bottom: 1px dashed #e8eff8;
}
.hero-icon {
    width: 56px;
    height: 56px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    flex-shrink: 0;
}
.hero-icon.ticket {
    background: linear-gradient(135deg, #149aa6, #0e7c86);
}
.hero-icon.hotel {
    background: linear-gradient(135deg, #f6b25e, #ef8a3d);
}
.hero-main {
    flex: 1;
    min-width: 0;
}
.hero-type {
    font-size: 12px;
    color: #8fa2bb;
    letter-spacing: 0.5px;
}
.hero-name {
    font-size: 20px;
    font-weight: 700;
    color: #1f2937;
    margin: 4px 0 6px;
}
.hero-no {
    font-size: 12px;
    color: #93a5bb;
}
.hero-amount {
    font-size: 30px;
    font-weight: 800;
    color: #ff3b30;
    white-space: nowrap;
}
.hero-amount .symbol {
    font-size: 16px;
    margin-right: 2px;
}

.timeline {
    display: flex;
    align-items: flex-start;
    gap: 0;
    padding: 22px 0 8px;
}
.tl-node {
    position: relative;
    display: flex;
    align-items: flex-start;
    gap: 8px;
    flex: 1;
    min-width: 0;
}
.tl-dot {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    background: #dbe7f7;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    z-index: 1;
}
.tl-node.done .tl-dot {
    background: #0e7c86;
}
.tl-node.current .tl-dot {
    background: #ff9f43;
    box-shadow: 0 0 0 4px rgba(255, 159, 67, 0.16);
}
.tl-text {
    display: flex;
    flex-direction: column;
    gap: 2px;
    padding-right: 12px;
}
.tl-text b {
    font-size: 13px;
    color: #22364f;
    font-weight: 600;
}
.tl-text span {
    font-size: 11px;
    color: #9aabc0;
}
.tl-line {
    position: absolute;
    left: 18px;
    right: 0;
    top: 9px;
    height: 1px;
    background: #e3ecf8;
}
.tl-node.done .tl-line {
    background: #e6f4f5;
}

.info-block {
    margin-top: 18px;
    padding: 16px 18px;
    border-radius: 14px;
    background: #f8fbff;
    border: 1px solid #eaf1fa;
}
.block-title {
    font-size: 13px;
    font-weight: 600;
    color: #0e7c86;
    margin-bottom: 10px;
}
.info-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px 24px;
}
.info-item {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    font-size: 13px;
    padding: 3px 0;
}
.info-item span {
    color: #8296ad;
    flex-shrink: 0;
}
.info-item b {
    color: #22364f;
    font-weight: 500;
    text-align: right;
    word-break: break-all;
}

.amount-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 13px;
    color: #6b7280;
    padding: 4px 0;
}
.amount-row b {
    color: #22364f;
}
.amount-row.total {
    margin-top: 8px;
    padding-top: 10px;
    border-top: 1px dashed #e3ecf8;
    font-size: 15px;
}
.amount-row.total b {
    color: #ff3b30;
    font-size: 20px;
    font-weight: 800;
}

.action-bar {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 20px;
}

.tips {
    margin-top: 16px;
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #93a5bb;
    line-height: 1.7;
}
</style>
