<template>
  <div class="admin-user">
    <el-card>
      <h2>用户管理</h2>
      <div class="search-bar">
        <el-input v-model="searchName" placeholder="搜索用户名 / 姓名 / 手机号" clearable style="width: 260px" @keyup.enter="handleSearch" />
        <el-select v-model="searchRole" placeholder="选择角色" style="width:160px">
          <el-option label="全部角色" value="" />
          <el-option label="管理员" value="管理员" />
          <el-option label="普通用户" value="普通用户" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="账号状态" style="width:140px">
          <el-option label="全部状态" value="" />
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="openAddDialog">新增用户</el-button>
        <el-button type="danger" plain :disabled="!selectedRows.length" :loading="batchDeleting" @click="handleBatchDelete">
          批量删除{{ selectedRows.length ? `(${selectedRows.length})` : '' }}
        </el-button>
        <el-button type="success" plain :disabled="!selectedRows.length" :loading="batchStatusLoading" @click="handleBatchStatus(1)">
          批量启用
        </el-button>
        <el-button type="warning" plain :disabled="!selectedRows.length" :loading="batchStatusLoading" @click="handleBatchStatus(0)">
          批量禁用
        </el-button>
        <el-popover trigger="click" :width="220" placement="bottom-end">
          <template #reference>
            <el-button>列设置</el-button>
          </template>
          <el-checkbox-group v-model="visibleColumns" class="col-setting">
            <el-checkbox label="avatar">头像</el-checkbox>
            <el-checkbox label="name">姓名</el-checkbox>
            <el-checkbox label="phone">手机号</el-checkbox>
            <el-checkbox label="role">角色</el-checkbox>
            <el-checkbox label="status">状态</el-checkbox>
            <el-checkbox label="lastLoginTime">最近登录</el-checkbox>
            <el-checkbox label="loginCount">登录次数</el-checkbox>
          </el-checkbox-group>
        </el-popover>
      </div>
      <el-table
        :data="tableData"
        v-loading="loading"
        element-loading-text="加载中…"
        border
        stripe
        style="margin-top:20px;width:100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="46" :selectable="(row) => row.role !== '管理员'" />
        <el-table-column label="ID" prop="id" width="70" sortable />
        <el-table-column v-if="visibleColumns.includes('avatar')" label="头像" prop="avatar" width="80">
          <template #default="scope">
            <el-image
              :src="getImgSrc(scope.row.avatar)"
              style="width:44px;height:44px;border-radius:50%;object-fit:cover"
              fit="cover"
              preview
            />
          </template>
        </el-table-column>
        <el-table-column label="用户名" prop="username" min-width="110" />
        <el-table-column v-if="visibleColumns.includes('name')" label="姓名" prop="name" width="100" />
        <el-table-column v-if="visibleColumns.includes('phone')" label="手机号" prop="phone" width="115" />
        <el-table-column v-if="visibleColumns.includes('role')" label="角色" prop="role" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === '管理员' ? 'danger' : 'primary'" effect="light" size="small">
              {{ scope.row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="visibleColumns.includes('status')" label="状态" width="80">
          <template #default="scope">
            <el-switch
              :model-value="Number(scope.row.status) === 1"
              :disabled="scope.row.role === '管理员'"
              @change="(val) => toggleStatus(scope.row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column v-if="visibleColumns.includes('lastLoginTime')" label="最近登录" width="140">
          <template #default="scope">
            {{ formatDateTime(scope.row.lastLoginTime) || '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column v-if="visibleColumns.includes('loginCount')" label="登录次数" prop="loginCount" width="95" sortable>
          <template #default="scope">{{ Number(scope.row.loginCount || 0) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="185" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="warning" link @click="openResetDialog(scope.row)">重置密码</el-button>
            <el-button size="small" type="danger" link :disabled="scope.row.role === '管理员'" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top:20px;justify-content:flex-end;display:flex"
      />
    </el-card>

    <!-- 新增 / 编辑用户 -->
    <el-dialog v-model="dialogVisible" title="用户信息" width="520px">
      <el-form :model="form" label-width="100px" :rules="formRules" ref="formRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            :placeholder="isEdit ? '编辑时留空表示不修改' : '请输入密码'"
            show-password
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" :disabled="editingAdmin">
            <el-option label="管理员" value="管理员" />
            <el-option label="普通用户" value="普通用户" />
          </el-select>
        </el-form-item>
        <el-form-item label="头像文件名" prop="avatar">
          <el-input v-model="form.avatar" placeholder="assets里填avatar1.jpeg；public图片直接填/admin-avatar.jpeg" />
          <div v-if="form.avatar" style="margin-top:8px;">
            <el-image :src="getImgSrc(form.avatar)" style="width:50px;height:50px;border-radius:50%" fit="cover" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码 -->
    <el-dialog v-model="resetDialogVisible" title="重置密码" width="420px">
      <p class="reset-tip">
        正在为 <b>{{ resetUser?.username }}</b> 设置新密码，保存后该用户需使用新密码登录。
      </p>
      <el-input v-model="newPassword" placeholder="请输入新密码（6~16位）" show-password maxlength="16" />
      <template #footer>
        <el-button @click="resetDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="resetting" @click="submitResetPwd">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserListApi, addUserApi, updateUserApi, deleteUserApi,
  updateUserStatusApi, adminResetPwdApi
} from '@/api/userData.js'
import { useUndo } from '@/composables/useUndo'
const { pushUndo } = useUndo()

// 【核心修改】自动区分 public 图片 和 assets图片
function getImgSrc(imgName) {
  if (!imgName) return ''
  if (imgName.startsWith('http') || imgName.startsWith('/')) {
    return imgName
  }
  return new URL(`../../assets/images/${imgName}`, import.meta.url).href
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

const searchName = ref('')
const searchRole = ref('')
const searchStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const editingAdmin = ref(false)
const formRef = ref(null)
const allUsers = ref([])
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const selectedRows = ref([])
const batchDeleting = ref(false)
const batchStatusLoading = ref(false)
const visibleColumns = ref(['avatar', 'name', 'phone', 'role', 'status', 'lastLoginTime', 'loginCount'])

const resetDialogVisible = ref(false)
const resetUser = ref(null)
const newPassword = ref('')
const resetting = ref(false)

const form = ref({
  id: '',
  username: '',
  password: '',
  name: '',
  phone: '',
  role: '',
  avatar: ''
})

// 通用校验规则；密码规则按“新增/编辑”动态区分
const baseFormRules = {
  username: [
    { required: true, message: '账号不能为空', trigger: 'blur' },
    { min: 3, max: 16, message: '账号长度3~16位', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const formRules = computed(() => ({
  ...baseFormRules,
  password: isEdit.value
    ? [
        {
          validator: (rule, value, callback) => {
            if (!value) return callback()
            if (value.length < 6 || value.length > 16) {
              return callback(new Error('密码长度6~16位'))
            }
            callback()
          },
          trigger: 'blur'
        }
      ]
    : [
        { required: true, message: '密码不能为空', trigger: 'blur' },
        { min: 6, max: 16, message: '密码长度6~16位', trigger: 'blur' }
      ]
}))

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// 加载用户列表（筛选交给后端，分页在前端）
async function loadUserList() {
  loading.value = true
  try {
    const res = await getUserListApi({
      role: searchRole.value || undefined,
      keyword: searchName.value || undefined
    })
    if (res.data.code === 200) {
      allUsers.value = res.data.data || []
      applyFilterAndPage()
    } else {
      ElMessage.error(res.data.msg || '用户加载失败')
    }
  } catch (e) {
    ElMessage.error('用户加载失败，请检查后端')
  } finally {
    loading.value = false
  }
}

// 状态筛选仍在前端做（后端列表接口不含 status 条件）
function applyFilterAndPage() {
  let list = allUsers.value
  if (searchStatus.value !== '') {
    list = list.filter((item) => Number(item.status ?? 1) === Number(searchStatus.value))
  }
  total.value = list.length
  const start = (currentPage.value - 1) * pageSize.value
  tableData.value = list.slice(start, start + pageSize.value)
}

const handleSearch = () => {
  currentPage.value = 1
  loadUserList()
}
const handleReset = () => {
  searchName.value = ''
  searchRole.value = ''
  searchStatus.value = ''
  currentPage.value = 1
  loadUserList()
}

const openAddDialog = () => {
  isEdit.value = false
  editingAdmin.value = false
  form.value = { username: '', password: '', name: '', phone: '', role: '', avatar: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  editingAdmin.value = row.role === '管理员'
  form.value = { ...row }
  form.value.password = ''
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate()
  let res
  if (isEdit.value) {
    res = await updateUserApi(form.value)
  } else {
    res = await addUserApi(form.value)
  }
  if (res.data.code === 200) {
    dialogVisible.value = false
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    loadUserList()
  } else {
    ElMessage.error(res.data.msg)
  }
}

// 启用 / 禁用
const toggleStatus = async (row, val) => {
  const next = val ? 1 : 0
  if (!val) {
    try {
      await ElMessageBox.confirm(`禁用后「${row.username}」将无法登录，确定继续吗？`, '禁用账号', {
        type: 'warning',
        confirmButtonText: '确定禁用',
        cancelButtonText: '取消'
      })
    } catch (e) {
      return
    }
  }
  try {
    const res = await updateUserStatusApi(row.id, next)
    if (res.data.code === 200) {
      row.status = next
      ElMessage.success(val ? '账号已启用' : '账号已禁用')
    } else {
      ElMessage.error(res.data.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败，请重试')
  }
}

// 重置密码
const openResetDialog = (row) => {
  resetUser.value = row
  newPassword.value = ''
  resetDialogVisible.value = true
}
const submitResetPwd = async () => {
  const pwd = newPassword.value.trim()
  if (pwd.length < 6 || pwd.length > 16) {
    ElMessage.warning('密码长度需 6~16 位')
    return
  }
  resetting.value = true
  try {
    const res = await adminResetPwdApi({ id: resetUser.value.id, newPassword: pwd })
    if (res.data.code === 200) {
      ElMessage.success('密码已重置')
      resetDialogVisible.value = false
    } else {
      ElMessage.error(res.data.msg || '重置失败')
    }
  } catch (e) {
    ElMessage.error('重置失败，请重试')
  } finally {
    resetting.value = false
  }
}

// 批量启用 / 禁用
const handleBatchStatus = async (status) => {
  const rows = selectedRows.value.filter((row) => row.role !== '管理员')
  if (!rows.length) {
    ElMessage.warning('管理员账号不能批量修改状态')
    return
  }
  const action = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定批量${action}选中的 ${rows.length} 个账号吗？`, `批量${action}`, {
      type: 'warning',
      confirmButtonText: `确定${action}`,
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  batchStatusLoading.value = true
  try {
    const results = await Promise.allSettled(rows.map((row) => updateUserStatusApi(row.id, status)))
    const okCount = results.filter((r) => r.status === 'fulfilled' && r.value?.data?.code === 200).length
    if (okCount) ElMessage.success(`已${action} ${okCount} 个账号`)
    if (rows.length - okCount > 0) ElMessage.warning(`${rows.length - okCount} 个操作失败`)
    await loadUserList()
  } finally {
    batchStatusLoading.value = false
  }
}

// 单条删除（保留撤销条）
const handleDelete = (row) => {
  const index = tableData.value.findIndex((item) => item.id === row.id)
  const snapshot = { ...row }
  if (index > -1) tableData.value.splice(index, 1)
  total.value = Math.max(0, total.value - 1)
  pushUndo({
    message: `已删除用户「${row.username || row.nickname || row.id}」`,
    onUndo: () => {
      tableData.value.splice(index, 0, snapshot)
      total.value += 1
    },
    onCommit: async () => {
      try {
        const res = await deleteUserApi(row.id)
        if (res.data.code === 200) {
          ElMessage.success('删除成功')
        } else {
          ElMessage.error(res.data.msg)
        }
      } catch (err) {
        ElMessage.error('删除失败，请重试')
      }
      loadUserList()
    }
  })
}

// 批量删除
const handleBatchDelete = async () => {
  if (!selectedRows.value.length) return
  const rows = [...selectedRows.value]
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${rows.length} 个用户吗？删除后不可恢复。`, '批量删除', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  batchDeleting.value = true
  try {
    const results = await Promise.allSettled(rows.map((row) => deleteUserApi(row.id)))
    const okCount = results.filter((r) => r.status === 'fulfilled' && r.value?.data?.code === 200).length
    if (okCount) ElMessage.success(`已删除 ${okCount} 个用户`)
    if (rows.length - okCount > 0) ElMessage.warning(`${rows.length - okCount} 个删除失败`)
    await loadUserList()
  } finally {
    batchDeleting.value = false
  }
}

onMounted(() => {
  loadUserList()
})
watch([currentPage, pageSize], () => {
  applyFilterAndPage()
})
watch(searchStatus, () => {
  currentPage.value = 1
  applyFilterAndPage()
})
</script>

<style scoped>
.admin-user {
  width: 100%;
}
.admin-user :deep(.el-card__body) {
  width: 100%;
  box-sizing: border-box;
}
.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}
.reset-tip {
  margin: 0 0 14px;
  font-size: 13px;
  color: #6b7f99;
  line-height: 1.7;
}
.col-setting {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>
