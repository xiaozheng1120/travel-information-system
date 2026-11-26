<template>
    <div class="login-page">
        <div class="login-card">
            <img src="/logo.jpeg" alt="logo" class="avatar-icon" />
            <h2>旅游信息系统</h2>
            <p class="sub-title">TRAVEL INFORMATION SYSTEM</p>

            <el-form :model="loginForm" :rules="loginRules" ref="loginRef">
                <!-- 账号 -->
                <el-form-item prop="username" class="field-item">
                    <div class="label-row">
                        <span class="field-label">账号</span>
                        <el-tooltip content="账号长度3~16位" placement="top">
                            <span class="info-icon" title="账号长度3~16位">ⓘ</span>
                        </el-tooltip>
                    </div>
                    <el-input
                        v-model="loginForm.username"
                        size="large"
                        clearable
                        placeholder="请输入账号"
                        class="float-input">
                        <template #prefix>
                            <el-icon><User /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <!-- 密码：关闭原生show-message，手动控制报错位置 -->
                <el-form-item prop="password" class="field-item" :show-message="false">
                    <div class="label-row">
                        <span class="field-label">密码</span>
                        <el-tooltip content="密码需6~16位" placement="top">
                            <span class="info-icon" title="密码需6~16位">ⓘ</span>
                        </el-tooltip>
                    </div>
                    <el-input
                        ref="pwdRef"
                        v-model="loginForm.password"
                        type="password"
                        placeholder="请输入密码"
                        size="large"
                        show-password
                        clearable
                        class="float-input"
                        :class="{ 'is-error': passwordError }"
                        @input="onPasswordInput"
                        @blur="validatePassword">
                        <template #prefix>
                            <el-icon><Lock /></el-icon>
                        </template>
                    </el-input>
                    <!-- 自定义报错，固定在输入框下方 -->
                    <div class="tip-text" v-if="passwordError">
                        {{ passwordError }}
                    </div>
                    <!-- 密码强度条，固定在报错下方 -->
                    <div
                        class="pwd-strength-wrap"
                        :class="{ 'is-empty': !loginForm.password }"
                        :style="{ '--pwd-color': pwdTextColor }">
                        <div class="strength-track">
                            <div class="strength-fill" :style="{ width: pwdPercent + '%', background: pwdBarColor }"></div>
                        </div>
                        <div class="strength-labels">
                            <span :class="{ active: pwdLevel === 'weak' }">弱</span>
                            <span :class="{ active: pwdLevel === 'medium' }">中</span>
                            <span :class="{ active: pwdLevel === 'strong' }">强</span>
                        </div>
                    </div>
                </el-form-item>

                <!-- 验证码 -->
                <el-form-item prop="code" class="field-item">
                    <div class="label-row">
                        <span class="field-label">验证码</span>
                        <el-tooltip content="请输入4位验证码，点击图片可刷新" placement="top">
                            <span class="info-icon" title="点击图片刷新验证码">ⓘ</span>
                        </el-tooltip>
                    </div>
                    <div class="code-row">
                        <el-input
                            v-model="loginForm.code"
                            placeholder="请输入验证码"
                            size="large"
                            class="float-input">
                        </el-input>
                        <div class="code-img spring-target" @click="refreshCode" @pointerup="playSpringOnEvent" title="点击刷新">
                            <canvas ref="codeCanvas" class="code-canvas"></canvas>
                        </div>
                    </div>
                </el-form-item>

                <div class="row-between">
                    <div class="forget-pwd" @click="openForgetDialog">忘记密码？</div>
                </div>

                <el-form-item>
                    <el-button
                        class="login-btn"
                        type="primary"
                        @pointerup="playSpringOnEvent"
                        @click="handleLogin">登 录</el-button>
                </el-form-item>
            </el-form>

            <div class="register-text spring-target" @click="goRegister" @pointerup="playSpringOnEvent">立即注册</div>
        </div>

        <!-- 忘记密码弹窗 -->
        <el-dialog v-model="forgetVisible" width="400px" class="forget-dialog" align-center>
            <template #header>
                <div class="fd-header">
                    <div class="fd-icon">
                        <el-icon :size="18"><Lock /></el-icon>
                    </div>
                    <div class="fd-text">
                        <h3 class="fd-title">忘记密码</h3>
                        <p class="fd-sub">验证账号与手机号后即可重置</p>
                    </div>
                </div>
            </template>
            <el-form :model="forgetForm" :rules="forgetRules" ref="forgetRef" class="fd-form">
                <el-form-item prop="username">
                    <template #label><span class="fd-label">账号</span></template>
                    <el-input v-model="forgetForm.username" placeholder="请输入账号" size="large"></el-input>
                </el-form-item>
                <el-form-item prop="phone">
                    <template #label><span class="fd-label">手机号</span></template>
                    <el-input v-model="forgetForm.phone" maxlength="11" placeholder="请输入注册时填写的手机号" size="large"></el-input>
                </el-form-item>
                <el-form-item prop="newPassword">
                    <template #label><span class="fd-label">新密码</span></template>
                    <el-input v-model="forgetForm.newPassword" type="password" show-password maxlength="16" placeholder="6~16位，含大小写字母和数字" size="large"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="fd-footer">
                    <el-button class="fd-cancel" @click="forgetVisible=false">取消</el-button>
                    <el-button class="spring-target fd-confirm" type="primary" @pointerup="playSpringOnEvent" @click="submitForget">确认重置</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { loginApi, resetPwdApi } from '@/api/userData.js'

const router = useRouter()
const loginRef = ref(null)
const forgetRef = ref(null)
const pwdRef = ref(null)

const loginForm = ref({
    username: '',
    password: '',
    code: ''
})

const forgetVisible = ref(false)
const forgetForm = ref({
    username: '',
    phone: '',
    newPassword: ''
})

// 忘记密码弹窗校验规则
const forgetRules = ref({
    username: [
        { required: true, message: '请输入账号', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
    ],
    newPassword: [
        {
            validator: (rule, value, callback) => {
                const v = value || ''
                if (!v) return callback(new Error('请输入新密码'))
                if (v.length < 6 || v.length > 16) return callback(new Error('新密码长度6~16位'))
                if (!/[a-z]/.test(v) || !/[A-Z]/.test(v) || !/[0-9]/.test(v)) {
                    return callback(new Error('新密码需同时包含大写字母、小写字母和数字'))
                }
                callback()
            },
            trigger: 'blur'
        }
    ]
})

// 登录页规则：6‑16位，不强制大小写
const loginRules = ref({
    username: [
        { required: true, message: '账号不能为空', trigger: 'blur' },
        { min: 3, max: 16, message: '账号长度3~16位', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '密码不能为空', trigger: 'blur' },
        { min: 6, max: 16, message: '密码长度6~16位', trigger: 'blur' }
    ],
    code: [
        { required: true, message: '请输入验证码', trigger: 'blur' }
    ]
})

// 密码自定义错误
const passwordError = ref('')

// 验证码：codeText 只负责校验，canvas 只负责画图
const codeText = ref('')
const codeArr = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
const codeCanvas = ref(null)
const CODE_W = 110
const CODE_H = 42
const codeColors = ['#4a90e2', '#7b61c9', '#2fa84f', '#d98c1f', '#d9553f']

// 在 canvas 上随机画：背景 + 干扰曲线 + 噪点 + 倾斜字符
const drawCode = (text) => {
    const el = codeCanvas.value
    if (!el) return
    const dpr = window.devicePixelRatio || 1
    el.width = Math.round(CODE_W * dpr)
    el.height = Math.round(CODE_H * dpr)
    const ctx = el.getContext('2d')
    if (!ctx) return
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
    ctx.clearRect(0, 0, CODE_W, CODE_H)

    // 背景渐变
    const bg = ctx.createLinearGradient(0, 0, CODE_W, CODE_H)
    bg.addColorStop(0, 'rgba(244, 247, 253, 0.92)')
    bg.addColorStop(1, 'rgba(232, 239, 249, 0.82)')
    ctx.fillStyle = bg
    ctx.fillRect(0, 0, CODE_W, CODE_H)

    // 干扰曲线：每次刷新都是随机走向
    for (let i = 0; i < 5; i++) {
        ctx.beginPath()
        ctx.moveTo(Math.random() * CODE_W, Math.random() * CODE_H)
        ctx.quadraticCurveTo(
            Math.random() * CODE_W, Math.random() * CODE_H,
            Math.random() * CODE_W, Math.random() * CODE_H
        )
        const c = codeColors[Math.floor(Math.random() * codeColors.length)]
        ctx.strokeStyle = c
        ctx.globalAlpha = 0.22 + Math.random() * 0.18
        ctx.lineWidth = 0.5 + Math.random() * 1.1
        ctx.stroke()
    }
    ctx.globalAlpha = 1

    // 噪点：随机散布的小圆点
    for (let i = 0; i < 50; i++) {
        ctx.beginPath()
        ctx.arc(Math.random() * CODE_W, Math.random() * CODE_H, 0.4 + Math.random() * 1.1, 0, Math.PI * 2)
        const c = codeColors[Math.floor(Math.random() * codeColors.length)]
        ctx.fillStyle = c
        ctx.globalAlpha = 0.15 + Math.random() * 0.3
        ctx.fill()
    }
    ctx.globalAlpha = 1

    // 字符：逐个随机旋转、随机字号、随机上下偏移
    const chars = text.split('')
    chars.forEach((ch, i) => {
        ctx.save()
        const x = (CODE_W / 5) * (i + 1) + (Math.random() * 2 - 1)
        const y = CODE_H / 2 + (Math.random() * 4 - 2)
        ctx.translate(x, y)
        ctx.rotate((Math.random() * 18 - 9) * Math.PI / 180)
        ctx.font = `bold ${18 + Math.random() * 3}px "Arial Black", Arial, sans-serif`
        ctx.textAlign = 'center'
        ctx.textBaseline = 'middle'
        ctx.fillStyle = codeColors[Math.floor(Math.random() * codeColors.length)]
        ctx.globalAlpha = 0.92
        ctx.fillText(ch, 0, 0)
        ctx.restore()
    })
    ctx.globalAlpha = 1
}

const refreshCode = () => {
    let str = ''
    for (let i = 0; i < 4; i++) {
        str += codeArr[Math.floor(Math.random() * codeArr.length)]
    }
    codeText.value = str
    nextTick(() => drawCode(str))
}
refreshCode()

// 首次挂载后补画一次，确保 canvas 已就绪
onMounted(() => drawCode(codeText.value))

// 密码强度【调整梯度：0-2弱，3中，4-5强】
const pwdLevel = ref('')
const pwdPercent = ref(0)
const pwdBarColor = computed(() => {
    if (pwdLevel.value === 'weak') return 'linear-gradient(90deg, #ffc4c4, #f4664a)'
    if (pwdLevel.value === 'medium') return 'linear-gradient(90deg, #ffdc9c, #efa132)'
    if (pwdLevel.value === 'strong') return 'linear-gradient(90deg, #b7eb9a, #45c05d)'
    return 'transparent'
})
const pwdTextColor = computed(() => {
    if (pwdLevel.value === 'weak') return '#f4664a'
    if (pwdLevel.value === 'medium') return '#e0952b'
    if (pwdLevel.value === 'strong') return '#38a94f'
    return '#aab3c2'
})

const checkPwdLevel = (val) => {
    if (!val) {
        pwdLevel.value = ''
        pwdPercent.value = 0
        return
    }
    let score = 0
    if (/[a-z]/.test(val)) score++
    if (/[A-Z]/.test(val)) score++
    if (/[0-9]/.test(val)) score++
    if (/[^0-9a-zA-Z]/.test(val)) score++
    if (val.length >= 8) score++

    if (score <= 2) {
        pwdLevel.value = 'weak'
        pwdPercent.value = 33
    } else if (score === 3) {
        pwdLevel.value = 'medium'
        pwdPercent.value = 66
    } else {
        pwdLevel.value = 'strong'
        pwdPercent.value = 100
    }
}

// 登录页密码校验：6‑16位，去掉大小写强制
const validatePassword = () => {
    const val = loginForm.value.password
    if (!val) {
        passwordError.value = '密码不能为空'
        return false
    }
    if (val.length < 6 || val.length > 16) {
        passwordError.value = '密码长度6~16位'
        return false
    }
    passwordError.value = ''
    return true
}

const onPasswordInput = () => {
    // 输入时清空错误
    if (passwordError.value) passwordError.value = ''
    checkPwdLevel(loginForm.value.password)
}

/* 按压回弹：按下由 CSS :active 缩到 96%，松手给元素加一次 is-spring 播放弹簧动画 */
const playSpringOn = (el) => {
    if (!el || !el.classList) return
    el.classList.remove('is-spring')
    // 强制回流，保证连续点击时动画每次都会重新播放
    void el.offsetWidth
    el.classList.add('is-spring')
    if (el.__springTimer) clearTimeout(el.__springTimer)
    el.__springTimer = setTimeout(() => {
        el.classList.remove('is-spring')
    }, 470)
}
const playSpringOnEvent = (e) => playSpringOn(e?.currentTarget)

const handleLogin = async () => {
    // 先单独校验密码，保证点“登录”时密码框也能立刻给出提示
    // （如果先 await el-form 校验，账号/验证码不通过会直接抛出，密码提示永远轮不到）
    const pwdOk = validatePassword()
    try {
        await loginRef.value.validate()
    } catch (err) {
        // 账号、验证码等字段的错误由 el-form 自己提示，这里直接中断
        console.log('校验失败', err)
        return
    }
    if (!pwdOk) return

    if (loginForm.value.code.toUpperCase() !== codeText.value) {
        ElMessage.error('验证码错误，请重新输入')
        refreshCode()
        return
    }

    // =========对接后端=========
    try {
        const res = await loginApi(loginForm.value)
        // userData.js 用的是原生axios，res 是完整响应，后端数据在 res.data 里
        const body = (res && res.data !== undefined) ? res.data : res
        if (body && body.code === 200) {
            const userInfo = { ...body.data }
            // 即使后端误返回密码字段，也绝不写进浏览器
            delete userInfo.password
            sessionStorage.setItem("userInfo", JSON.stringify(userInfo))
            ElMessage.success("登录成功")
            if (userInfo.role === '管理员') {
                router.push('/admin/home')
            } else {
                router.push('/home')
            }
        } else {
            ElMessage.error((body && body.msg) ? body.msg : '登录失败')
            refreshCode()
        }
    } catch (err) {
        console.log('登录请求失败', err)
        ElMessage.error('网络异常，请稍后重试')
        refreshCode()
    }
}

const goRegister = () => {
    router.push('/register')
}

const openForgetDialog = () => {
    forgetVisible.value = true
}

const submitForget = async () => {
    try {
        await forgetRef.value.validate()
    } catch (e) {
        return
    }
    try {
        const res = await resetPwdApi({
            username: forgetForm.value.username,
            phone: forgetForm.value.phone,
            newPassword: forgetForm.value.newPassword
        })
        const body = (res && res.data !== undefined) ? res.data : res
        if (body && body.code === 200) {
            const resetUsername = forgetForm.value.username
            ElMessage.success(body.msg || '密码重置成功，请使用新密码登录')
            // 关闭弹窗、清空弹窗内容
            forgetVisible.value = false
            forgetForm.value = { username: '', phone: '', newPassword: '' }
            forgetRef.value && forgetRef.value.clearValidate()
            // 自动回填登录框：账号带上，密码/验证码清空，刷新验证码并聚焦密码框
            loginForm.value.username = resetUsername
            loginForm.value.password = ''
            loginForm.value.code = ''
            passwordError.value = ''
            pwdLevel.value = ''
            pwdPercent.value = 0
            refreshCode()
            nextTick(() => {
                if (pwdRef.value && pwdRef.value.focus) pwdRef.value.focus()
            })
        } else {
            ElMessage.error((body && body.msg) ? body.msg : '重置失败，请重试')
        }
    } catch (err) {
        console.log('重置请求异常', err)
        ElMessage.error('重置请求失败，请查看F12网络面板')
    }
}
</script>

<style scoped>
.login-page {
    width: 100vw;
    height: 100vh;
    /* 背景与遮罩统一由 App.vue 的固定底层提供，这里保持透明 */
    background: transparent;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
}

.login-card {
    width: 420px;
    max-width: calc(100vw - 40px);
    /* 玻璃卡片：上偏亮下偏透，让背后的照片透出来，和背景融在一起 */
    background: linear-gradient(
        160deg,
        rgba(255, 255, 255, 0.86) 0%,
        rgba(255, 255, 255, 0.72) 100%
    );
    backdrop-filter: blur(18px) saturate(150%);
    -webkit-backdrop-filter: blur(18px) saturate(150%);
    /* 高光边：只做一圈极细的白边，像玻璃边缘反光，不形成“白框” */
    border: 1px solid rgba(255, 255, 255, 0.65);
    border-radius: 16px;
    padding: 36px 40px;
    text-align: center;
    /* 阴影偏冷、扩散开，模拟自然投影，而不是压在卡片下面的硬块 */
    box-shadow:
        0 20px 50px rgba(15, 45, 55, 0.2),
        0 2px 8px rgba(15, 45, 55, 0.06),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
    z-index: 2;
}

/* 站徽头像：和导航栏“旅游信息系统”旁的 logo 同一张图 */
.avatar-icon {
    width: 58px;
    height: 58px;
    margin: 0 auto 10px;
    display: block;
    border-radius: 50%;
    object-fit: cover;
    background: #fff;
    box-shadow:
        0 8px 18px rgba(20, 40, 80, 0.22),
        0 0 0 2px rgba(255, 255, 255, 0.65);
}

h2 {
    margin: 0;
    color: #1f2937;
    font-size: 22px;
    font-weight: 600;
}

.sub-title {
    color: #6b7280;
    font-size: 12px;
    letter-spacing: 2px;
    margin: 4px 0 24px;
}

/* 表单项整体 */
.field-item {
    text-align: left;
    margin-bottom: 18px;
}

/* 标签行 */
.label-row {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 6px;
}

.field-label {
    font-size: 14px;
    color: #374151;
    font-weight: 500;
}

.info-icon {
    font-size: 14px;
    color: #909399;
    cursor: pointer;
    line-height: 1;
}

/* 输入框 */
:deep(.float-input .el-input__wrapper) {
    border-radius: 10px;
    /* 输入框半透明，避免在透光卡片里像一块实心白贴片 */
    background: rgba(255, 255, 255, 0.72);
    border: 1px solid rgba(255, 255, 255, 0.7);
    box-shadow: inset 0 1px 2px rgba(20, 30, 50, 0.04);
}

:deep(.float-input .el-input__wrapper.is-focus) {
    background: #fff;
    border-color: #0e7c86;
    box-shadow: 0 0 0 3px rgba(14, 124, 134, 0.14);
}
:deep(.float-input.is-error .el-input__wrapper) {
    border-color: #f56c6c;
    box-shadow: 0 0 0 3px rgba(245,108,108,0.12);
}

/* 自定义校验提示文字，放在输入框和进度条中间 */
/* 自定义校验提示文字，紧贴输入框下方 */
.tip-text {
    margin: 6px 0 0;
    padding-left: 19px;
    font-size: 12px;
    line-height: 1.5;
    color: #f56c6c;
}

/* 密码强度条：与上方输入框内边距对齐 */
.pwd-strength-wrap {
    margin-top: 10px;
    padding: 0 19px;
    width: 100%;
    box-sizing: border-box;
    transition: opacity 0.25s ease;
}
.pwd-strength-wrap.is-empty {
    opacity: 0.75;
}
.strength-track {
    width: 100%;
    height: 5px;
    background: rgba(222, 229, 240, 0.9);
    border-radius: 999px;
    box-shadow: inset 0 1px 2px rgba(150, 165, 190, 0.18);
    overflow: hidden;
}
.strength-fill {
    height: 100%;
    width: 0;
    border-radius: 999px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
    transition: width 0.4s cubic-bezier(0.22, 0.61, 0.36, 1), background 0.3s ease;
}
.strength-labels {
    display: flex;
    justify-content: space-between;
    margin-top: 7px;
    padding: 0 1px;
    font-size: 11px;
    letter-spacing: 0.5px;
    color: #aab3c2;
}
.strength-labels span {
    line-height: 1;
    transition: color 0.25s ease, transform 0.25s ease;
}
.strength-labels .active {
    color: var(--pwd-color, #aab3c2);
    font-weight: 600;
    transform: translateY(-1px);
}

/* 验证码行 */
.code-row {
    display: flex;
    gap: 10px;
    align-items: center;
}

.code-row :deep(.el-input) {
    flex: 1;
}

.code-img {
    position: relative;
    width: 110px;
    height: 42px;
    box-sizing: border-box;
    background: transparent;
    border-radius: 12px;
    cursor: pointer;
    overflow: hidden;
    user-select: none;
    transition: background 0.2s ease, box-shadow 0.2s ease;
}
.code-img:hover {
    background: rgba(230, 244, 245, 0.95);
    box-shadow: 0 4px 12px rgba(14, 124, 134, 0.18);
}
.code-canvas {
    display: block;
    width: 100%;
    height: 100%;
}

/* 按钮 */
.login-btn {
    width: 100%;
    height: 44px;
    border-radius: 10px;
    font-size: 16px;
    border: none;
    background: linear-gradient(90deg, #0e7c86, #149aa6);
    box-shadow: 0 8px 18px rgba(14, 124, 134, 0.3);
    transform: scale(1);
    transition: transform 0.28s cubic-bezier(0.22, 0.61, 0.36, 1), box-shadow 0.25s ease;
    will-change: transform;
}

.login-btn:hover {
    box-shadow: 0 12px 24px rgba(14, 124, 134, 0.4);
}

/* 按下：快速缩到 96% */
.login-btn:active {
    transform: scale(0.96);
    transition: transform 0.1s cubic-bezier(0.4, 0, 0.6, 1), box-shadow 0.25s ease;
}

/* 松手：弹簧回弹，先冲过 103% 再轻轻落回 100% */
.login-btn.is-spring {
    animation: btnSpring 0.46s cubic-bezier(0.33, 0.1, 0.3, 1) both;
}

@keyframes btnSpring {
    0% { transform: scale(0.96); }
    32% { transform: scale(1.03); }
    58% { transform: scale(0.995); }
    80% { transform: scale(1.004); }
    100% { transform: scale(1); }
}

/* 通用按压回弹：验证码、文字链接、弹窗按钮共用同一套手感 */
.spring-target {
    transition: transform 0.28s cubic-bezier(0.22, 0.61, 0.36, 1);
    will-change: transform;
}
.spring-target:active {
    transform: scale(0.96);
    transition: transform 0.1s cubic-bezier(0.4, 0, 0.6, 1);
}
.spring-target.is-spring {
    animation: btnSpring 0.46s cubic-bezier(0.33, 0.1, 0.3, 1) both;
}

.register-text {
    margin-top: 16px;
    color: #0e7c86;
    cursor: pointer;
    font-size: 14px;
}
.register-text:hover{
    color:#149aa6;
}

.row-between {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 8px;
}

.forget-pwd {
    font-size: 13px;
    color: #0e7c86;
    cursor: pointer;
}
.forget-pwd:hover{
    color:#149aa6;
}
</style>

<style>
/*
 * 忘记密码弹窗（非 scoped）
 * 弹层可能被挂到 body 上，scoped 的 :deep() 会失效，所以这里用 .forget-dialog 前缀隔离
 */
.forget-dialog.el-dialog {
    border-radius: 16px;
    overflow: hidden;
    padding: 0;
    background: rgba(255, 255, 255, 0.97);
    backdrop-filter: blur(20px) saturate(140%);
    -webkit-backdrop-filter: blur(20px) saturate(140%);
    border: 1px solid rgba(255, 255, 255, 0.75);
    box-shadow: 0 24px 60px rgba(10, 20, 40, 0.28);
}

.forget-dialog .el-dialog__header {
    padding: 26px 26px 4px;
    margin: 0;
}

/* 关闭按钮做成浅色小圆点，不抢标题的视觉 */
.forget-dialog .el-dialog__headerbtn {
    top: 20px;
    right: 20px;
    width: 30px;
    height: 30px;
    border-radius: 50%;
    background: #f3f7fd;
    transition: background 0.2s ease;
}
.forget-dialog .el-dialog__headerbtn:hover {
    background: #e8f1fe;
}
.forget-dialog .el-dialog__headerbtn .el-dialog__close {
    color: #8798ae;
    font-size: 14px;
}

.forget-dialog .el-dialog__body {
    padding: 16px 26px 4px;
}

.forget-dialog .el-dialog__footer {
    padding: 6px 26px 24px;
}

/* 自定义标题区：图标 + 主标题 + 说明 */
.fd-header {
    display: flex;
    align-items: center;
    gap: 12px;
    text-align: left;
}
.fd-icon {
    width: 38px;
    height: 38px;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    color: #fff;
    background: linear-gradient(135deg, #149aa6, #0e7c86);
    box-shadow: 0 8px 16px rgba(14, 124, 134, 0.3);
}
.fd-title {
    margin: 0;
    font-size: 17px;
    font-weight: 600;
    color: #1f2a3c;
    letter-spacing: 0.5px;
}
.fd-sub {
    margin: 4px 0 0;
    font-size: 12px;
    color: #90a0b4;
    letter-spacing: 0.3px;
}

/* 表单：标签右对齐固定宽度，输入框圆角胶囊 */
.fd-form .el-form-item {
    margin-bottom: 16px;
}
.fd-form .el-form-item__label {
    width: 56px;
    justify-content: flex-end;
    padding-right: 10px;
}
.fd-label {
    font-size: 14px;
    font-weight: 500;
    color: #43536b;
}

.forget-dialog .el-input__wrapper {
    border-radius: 999px;
    padding: 1px 16px;
    background: rgba(246, 249, 255, 0.95);
    border: 1px solid rgba(214, 226, 243, 0.9);
    box-shadow: none;
    transition: background 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease;
}
.forget-dialog .el-input__wrapper:hover {
    border-color: rgba(190, 212, 240, 0.95);
}
.forget-dialog .el-input__wrapper.is-focus {
    background: #fff;
    border-color: #0e7c86;
    box-shadow: 0 0 0 3px rgba(14, 124, 134, 0.14);
}

/* 底部按钮：同样是胶囊，主按钮沿用登录按钮的蓝色渐变 */
.fd-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}
.forget-dialog .el-button {
    height: 40px;
    border-radius: 999px;
    padding: 0 22px;
    font-size: 14px;
}
.forget-dialog .fd-cancel {
    background: #f4f7fc;
    border: 1px solid #e3eaf4;
    color: #5b6b82;
}
.forget-dialog .fd-cancel:hover {
    background: #eaf2fd;
    border-color: #d5e3f7;
    color: #0e7c86;
}
.forget-dialog .fd-confirm {
    border: none;
    background: linear-gradient(90deg, #0e7c86, #149aa6);
    box-shadow: 0 8px 18px rgba(14, 124, 134, 0.32);
}
</style>
