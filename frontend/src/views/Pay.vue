<template>
    <div class="pay-page">
        <div class="pay-wrap">
            <div class="pay-top">
                <button type="button" class="back-btn" @click="goBack">
                    <el-icon><ArrowLeft /></el-icon>
                </button>
                <div class="pay-top-title">收银台</div>
                <div class="pay-top-right" v-if="draft">订单号 {{ draft.payload.orderNo }}</div>
            </div>

            <div v-if="draft" class="pay-card edge-beam edge-beam--cool">
                <!-- 订单合计 -->
                <div class="amount-block">
                    <div class="amount-label">订单合计</div>
                    <div class="amount">¥ {{ amountText }}</div>
                    <div class="amount-sub">{{ draft.productName }}<template v-if="draft.subtitle"> · {{ draft.subtitle }}</template></div>
                </div>

                <!-- 订单信息 -->
                <div class="pay-info">
                    <div class="info-row">
                        <span class="info-label">项目名称</span>
                        <span class="info-value">{{ draft.productName }}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">{{ draft.orderType === 2 ? '入住日期' : '出行日期' }}</span>
                        <span class="info-value">{{ draft.dateLabel }}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">{{ draft.orderType === 2 ? '住宿数量' : '购票数量' }}</span>
                        <span class="info-value">{{ draft.qtyLabel }}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">{{ draft.orderType === 2 ? '入住人' : '出行人' }}</span>
                        <span class="info-value">{{ draft.personName || '未填写' }}<template v-if="draft.phone"> · {{ draft.phone }}</template></span>
                    </div>
                </div>

                <!-- 支付方式 -->
                <div class="method-title">选择支付方式</div>
                <div class="methods">
                    <button type="button" class="method" :class="{ active: payMethod === 'wechat' }"
                        @click="payMethod = 'wechat'">
                        <span class="method-icon wechat"><el-icon><ChatDotRound /></el-icon></span>
                        <span class="method-name">微信支付</span>
                        <span class="method-check"><el-icon><Select /></el-icon></span>
                    </button>
                    <button type="button" class="method" :class="{ active: payMethod === 'alipay' }"
                        @click="payMethod = 'alipay'">
                        <span class="method-icon alipay"><el-icon><Wallet /></el-icon></span>
                        <span class="method-name">支付宝</span>
                        <span class="method-check"><el-icon><Select /></el-icon></span>
                    </button>
                    <button type="button" class="method" :class="{ active: payMethod === 'card' }"
                        @click="payMethod = 'card'">
                        <span class="method-icon card"><el-icon><CreditCard /></el-icon></span>
                        <span class="method-name">银行卡</span>
                        <span class="method-check"><el-icon><Select /></el-icon></span>
                    </button>
                </div>

                <!-- 滑动确认支付 -->
                <SlideToPay ref="slideRef" :loading="paying" @success="handlePaid" />

                <div class="pay-tip">
                    <el-icon><Lock /></el-icon>
                    <span>演示环境：模拟支付，不会真实扣款</span>
                </div>
            </div>

            <div v-else class="pay-empty">
                <el-empty description="没有待支付的订单，可能已经支付完成">
                    <el-button type="primary" @click="router.push('/ticket')">去逛逛门票</el-button>
                </el-empty>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ChatDotRound, Wallet, CreditCard, Select, Lock } from '@element-plus/icons-vue'
import SlideToPay from '@/components/SlideToPay.vue'
import { addOrder } from '@/api/orderData.js'
import { loadAllTicketFromBackend } from '@/api/ticketData.js'

const DRAFT_KEY = 'travel-pay-draft'

const router = useRouter()
const draft = ref(null)
const payMethod = ref('wechat')
const paying = ref(false)
const slideRef = ref(null)

const amountText = computed(() => Number(draft.value?.amount || 0).toFixed(2))

const loadDraft = () => {
    try {
        const raw = sessionStorage.getItem(DRAFT_KEY)
        draft.value = raw ? JSON.parse(raw) : null
    } catch (e) {
        draft.value = null
    }
}

const goBack = () => {
    if (window.history.length > 1) router.back()
    else router.push('/ticket')
}

const handlePaid = async () => {
    if (!draft.value || paying.value) return
    paying.value = true
    try {
        const result = await addOrder({ ...draft.value.payload, status: 1 })
        if (result === false) throw new Error('下单失败')
        // 门票支付成功后同步一下前台库存
        if (draft.value.orderType === 1) {
            try {
                await loadAllTicketFromBackend()
            } catch (e) {
                /* 库存刷新失败不影响支付结果 */
            }
        }
        sessionStorage.removeItem(DRAFT_KEY)
        ElMessage.success(`支付成功，已生成订单：${draft.value.payload.orderNo}`)
        router.push('/order')
    } catch (err) {
        console.error('支付失败', err)
        ElMessage.error('支付失败，请稍后重试')
        slideRef.value?.reset()
    } finally {
        paying.value = false
    }
}

onMounted(loadDraft)
</script>

<style scoped>
.pay-page {
    min-height: 100vh;
    padding: 28px 16px 48px;
    background:
        radial-gradient(circle at 12% 8%, rgba(14, 124, 134, 0.14), transparent 30%),
        radial-gradient(circle at 88% 12%, rgba(242, 140, 91, 0.12), transparent 34%),
        linear-gradient(180deg, #faf8f5 0%, #f3f7fc 45%, #f3f0e9 100%);
    box-sizing: border-box;
}

.pay-wrap {
    width: 100%;
    max-width: 560px;
    margin: 0 auto;
}

.pay-top {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 18px;
}

.back-btn {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    border: 1px solid rgba(14, 124, 134, 0.22);
    background: rgba(255, 255, 255, 0.86);
    color: #3c5a80;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
}
.back-btn:hover {
    background: #fff;
    border-color: rgba(14, 124, 134, 0.5);
    color: #0e7c86;
}

.pay-top-title {
    font-size: 18px;
    font-weight: 700;
    color: #1f2937;
}

.pay-top-right {
    margin-left: auto;
    font-size: 12px;
    color: #6b7280;
}

.pay-card {
    padding: 24px 22px 22px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(18px) saturate(150%);
    -webkit-backdrop-filter: blur(18px) saturate(150%);
    border: 1px solid rgba(255, 255, 255, 0.6);
    border-radius: 22px;
    box-shadow: 0 18px 45px rgba(20, 30, 50, 0.14), 0 2px 8px rgba(20, 30, 50, 0.06);
}

/* 合计金额 */
.amount-block {
    text-align: center;
    padding: 6px 0 18px;
}

.amount-label {
    font-size: 13px;
    color: #6b7280;
    letter-spacing: 1px;
}

.amount {
    margin-top: 8px;
    font-size: 40px;
    font-weight: 800;
    line-height: 1.1;
    letter-spacing: 0.5px;
    background: linear-gradient(135deg, #ff8a3d, #ff4d4f);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
}

.amount-sub {
    margin-top: 8px;
    font-size: 13px;
    color: #6b7280;
}

/* 订单信息 */
.pay-info {
    padding: 14px 16px;
    border-radius: 14px;
    background: rgba(241, 246, 253, 0.8);
    border: 1px solid rgba(196, 214, 240, 0.5);
}

.info-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 6px 0;
    font-size: 13px;
}

.info-label {
    color: #6b7280;
    flex-shrink: 0;
}

.info-value {
    color: #22364f;
    font-weight: 500;
    text-align: right;
    word-break: break-all;
}

/* 支付方式 */
.method-title {
    margin: 22px 0 10px;
    font-size: 14px;
    font-weight: 600;
    color: #1f2937;
}

.methods {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.method {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;
    padding: 11px 14px;
    background: #fff;
    border: 1px solid rgba(196, 214, 240, 0.6);
    border-radius: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
    text-align: left;
}

.method:hover {
    border-color: rgba(14, 124, 134, 0.45);
}

.method.active {
    border-color: #0e7c86;
    background: rgba(240, 247, 255, 0.95);
    box-shadow: 0 6px 16px rgba(14, 124, 134, 0.14);
}

.method-icon {
    width: 32px;
    height: 32px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 17px;
    color: #fff;
    flex-shrink: 0;
}

.method-icon.wechat {
    background: linear-gradient(135deg, #2fbf6f, #1aa657);
}
.method-icon.alipay {
    background: linear-gradient(135deg, #149aa6, #0e7c86);
}
.method-icon.card {
    background: linear-gradient(135deg, #8a98ab, #5d6b7f);
}

.method-name {
    flex: 1;
    font-size: 14px;
    color: #22364f;
    font-weight: 500;
}

.method-check {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    border: 1px solid rgba(150, 170, 195, 0.6);
    color: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    transition: all 0.2s ease;
}

.method.active .method-check {
    background: #0e7c86;
    border-color: #0e7c86;
    color: #fff;
}

/* 滑动支付与提示 */
.slide-wrap {
    margin-top: 22px;
}

.pay-tip {
    margin-top: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    font-size: 12px;
    color: #93a5bb;
}

.pay-empty {
    padding: 40px 0;
}

/* 给滑块留出上边距 */
.pay-card :deep(.stp) {
    margin-top: 22px;
}
</style>
