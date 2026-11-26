<template>
    <div class="register-page">
        <div class="register-card">
            <!-- 头部固定，不参与滚动 -->
            <div class="card-header">
                <img src="/logo.jpeg" alt="logo" class="avatar-icon" />
                <h2>旅游信息系统</h2>
                <p class="sub-title">TRAVEL INFORMATION SYSTEM</p>
            </div>

            <!-- 表单区域：内容过多时内部滚动 -->
            <div class="form-scroll">
                <el-form :model="registerForm" :rules="registerRules" ref="registerRef">
                    <!-- 用户名 -->
                    <el-form-item prop="username" class="field-item">
                        <div class="label-row">
                            <span class="field-label">账号</span>
                            <el-tooltip content="账号长度3~16位" placement="top">
                                <span class="info-icon" title="账号长度3~16位">ⓘ</span>
                            </el-tooltip>
                        </div>
                        <el-input v-model="registerForm.username" placeholder="请输入用户名" size="large" clearable class="float-input">
                            <template #prefix>
                                <el-icon>
                                    <User />
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>

                    <!-- 密码（和登录页一致：关闭原生提示，手动校验+强度条） -->
                    <el-form-item prop="password" class="field-item" :show-message="false">
                        <div class="label-row">
                            <span class="field-label">密码</span>
                            <el-tooltip content="密码需8~16位，包含大小写字母" placement="top">
                                <span class="info-icon" title="密码需8~16位，包含大小写字母">ⓘ</span>
                            </el-tooltip>
                        </div>
                        <el-input
                            v-model="registerForm.password"
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
                                <el-icon>
                                    <Lock />
                                </el-icon>
                            </template>
                        </el-input>
                        <div class="tip-text" v-if="passwordError">
                            {{ passwordError }}
                        </div>
                        <div
                            class="pwd-strength-wrap"
                            :class="{ 'is-empty': !registerForm.password }"
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

                    <!-- 确认密码 -->
                    <el-form-item prop="confirmPwd" class="field-item" :show-message="false">
                        <div class="label-row">
                            <span class="field-label">确认密码</span>
                        </div>
                        <el-input
                            v-model="registerForm.confirmPwd"
                            type="password"
                            placeholder="请再次输入密码"
                            size="large"
                            show-password
                            clearable
                            class="float-input"
                            :class="{ 'is-error': confirmPwdError }"
                            @input="onConfirmPwdInput"
                            @blur="validateConfirmPwd">
                            <template #prefix>
                                <el-icon>
                                    <Lock />
                                </el-icon>
                            </template>
                        </el-input>
                        <div class="tip-text" v-if="confirmPwdError">
                            {{ confirmPwdError }}
                        </div>
                    </el-form-item>

                    <!-- 姓名 -->
                    <el-form-item prop="name" class="field-item">
                        <div class="label-row">
                            <span class="field-label">姓名</span>
                        </div>
                        <el-input v-model="registerForm.name" placeholder="请输入姓名" size="large" clearable class="float-input">
                            <template #prefix>
                                <el-icon>
                                    <User />
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>

                    <!-- 手机号 -->
                    <el-form-item prop="phone" class="field-item">
                        <div class="label-row">
                            <span class="field-label">手机号</span>
                        </div>
                        <el-input v-model="registerForm.phone" placeholder="请输入手机号" size="large" clearable class="float-input">
                            <template #prefix>
                                <el-icon>
                                    <Phone />
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>
                </el-form>
            </div>

            <!-- 底部固定区域，不滚动 -->
            <div class="form-bottom">
                <el-button
                    class="register-btn"
                    type="primary"
                    @pointerup="playSpringOnEvent"
                    @click="handleRegister">注 册</el-button>
                <div class="tip-text spring-target" @click="goLogin" @pointerup="playSpringOnEvent">已有账号？返回登录</div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Phone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
// 替换为后端注册接口
import { registerApi } from '@/api/userData.js'
const router = useRouter()
const registerRef = ref(null)
const registerForm = ref({
    username: '',
    password: '',
    confirmPwd: '',
    name: '',
    phone: '',
    role: '普通用户',
    avatar: 'default-avatar.png'
})

// 自定义错误
const passwordError = ref('')
const confirmPwdError = ref('')

// 密码强度
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

// 密码校验，注册页面保持严格8‑16位+大小写
const validatePassword = () => {
    const val = registerForm.value.password
    if (!val) {
        passwordError.value = '密码不能为空'
        return false
    }
    if (val.length < 8 || val.length > 16) {
        passwordError.value = '密码长度8~16位'
        return false
    }
    if (!/[a-z]/.test(val) || !/[A-Z]/.test(val)) {
        passwordError.value = '密码需包含大小写字母'
        return false
    }
    passwordError.value = ''
    return true
}
const onPasswordInput = () => {
    if (passwordError.value) passwordError.value = ''
    checkPwdLevel(registerForm.value.password)
    // 密码改动时顺便校验确认密码
    if (registerForm.value.confirmPwd) {
        validateConfirmPwd()
    }
}

// 确认密码校验
const validateConfirmPwd = () => {
    const pwd = registerForm.value.password
    const confirm = registerForm.value.confirmPwd
    if (!confirm) {
        confirmPwdError.value = '请再次输入密码'
        return false
    }
    if (confirm !== pwd) {
        confirmPwdError.value = '两次密码不一致'
        return false
    }
    confirmPwdError.value = ''
    return true
}
const onConfirmPwdInput = () => {
    if (confirmPwdError.value) confirmPwdError.value = ''
}

const registerRules = ref({
    username: [
        { required: true, message: '账号不能为空', trigger: 'blur' },
        { min: 3, max: 16, message: '账号长度3~16位', trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' }
    ]
})

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

const handleRegister = async () => {
    // 先单独校验密码与确认密码，保证点“注册”时这两栏也能立刻给出提示
    // （如果先 await el-form 校验，账号/姓名/手机号不通过会直接抛出，密码提示永远轮不到）
    const pwdOk = validatePassword()
    const confirmOk = validateConfirmPwd()
    try {
        await registerRef.value.validate()
    } catch (err) {
        // 其它字段的错误由 el-form 自己提示，这里直接中断
        console.log('校验失败', err)
        return
    }
    if (!pwdOk || !confirmOk) return

    const submitData = {
        username: registerForm.value.username,
        password: registerForm.value.password,
        name: registerForm.value.name,
        phone: registerForm.value.phone,
        role: registerForm.value.role,
        avatar: registerForm.value.avatar
    }
    try {
        const res = await registerApi(submitData)
        if (res.data.code === 200) {
            ElMessage.success({
                message: '注册成功，请登录！',
                onClose: () => {
                    router.push('/login')
                }
            })
        } else {
            ElMessage.error(res.data.msg)
        }
    } catch (err) {
        console.log('注册请求失败', err)
        ElMessage.error('网络异常，请稍后重试')
    }
}
const goLogin = () => {
    router.push('/login')
}
</script>

<style scoped>
.register-page {
    width: 100vw;
    height: 100vh;
    /* 背景与遮罩统一由 App.vue 的固定底层提供，这里保持透明 */
    background: transparent;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
}

/* 和登录页卡片完全一模一样：宽度、玻璃、圆角、阴影 */
.register-card {
    width: 420px;
    height: auto;
    max-height: 86vh;
    background: linear-gradient(
        160deg,
        rgba(255, 255, 255, 0.86) 0%,
        rgba(255, 255, 255, 0.72) 100%
    );
    backdrop-filter: blur(18px) saturate(150%);
    -webkit-backdrop-filter: blur(18px) saturate(150%);
    border: 1px solid rgba(255, 255, 255, 0.65);
    border-radius: 16px;
    box-shadow:
        0 20px 50px rgba(15, 45, 55, 0.2),
        0 2px 8px rgba(15, 45, 55, 0.06),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
    z-index: 2;
    display: flex;
    flex-direction: column;
    padding: 34px 36px;
    box-sizing: border-box;
}

.card-header {
    text-align: center;
    flex-shrink: 0;
    margin-bottom: 18px;
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

.card-header h2 {
    margin: 0;
    color: #1f2937;
    font-size: 22px;
    font-weight: 600;
}

.sub-title {
    color: #6b7280;
    font-size: 12px;
    letter-spacing: 2px;
    margin: 4px 0 0;
}

/* 表单滚动容器：内容多就内部滚动，卡片整体不会溢出屏幕 */
.form-scroll {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
    padding-right: 4px;
}
.form-scroll::-webkit-scrollbar {
    width: 4px;
}
.form-scroll::-webkit-scrollbar-thumb {
    background: #cccccc;
    border-radius: 4px;
}

/* 底部固定，不参与滚动 */
.form-bottom {
    flex-shrink: 0;
    margin-top: 16px;
}

/* 表单项整体，收紧间距 */
.field-item {
    text-align: left;
    margin-bottom: 15px;
}

.label-row {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 5px;
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

/* 输入框样式与登录页完全保持一致 */
:deep(.float-input .el-input__wrapper) {
    border-radius: 10px;
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

/* 校验提示文字，与输入框内边距对齐（仅表单内，避免影响底部链接） */
.field-item .tip-text {
    margin: 6px 0 0;
    padding-left: 19px;
    font-size: 12px;
    line-height: 1.5;
    color: #f56c6c;
}

/* 密码强度条：与登录页保持一致 */
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

.register-btn {
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
.register-btn:hover {
    box-shadow: 0 12px 24px rgba(14, 124, 134, 0.4);
}
/* 按下：快速缩到 96% */
.register-btn:active {
    transform: scale(0.96);
    transition: transform 0.1s cubic-bezier(0.4, 0, 0.6, 1), box-shadow 0.25s ease;
}
/* 松手：弹簧回弹，先冲过 103% 再轻轻落回 100% */
.register-btn.is-spring {
    animation: btnSpring 0.46s cubic-bezier(0.33, 0.1, 0.3, 1) both;
}
/* 通用按压回弹：文字链接等共用同一套手感 */
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
@keyframes btnSpring {
    0% { transform: scale(0.96); }
    32% { transform: scale(1.03); }
    58% { transform: scale(0.995); }
    80% { transform: scale(1.004); }
    100% { transform: scale(1); }
}

.form-bottom .tip-text {
    text-align: center;
    margin-top: 14px;
    color: #0e7c86;
    cursor: pointer;
    font-size: 14px;
}
.form-bottom .tip-text:hover {
    color: #149aa6;
}
</style>
