<template>
  <div class="container">
    <!-- 顶部状态 -->
    <div class="status-bar">
      <span class="status-dot">●</span>
      <span class="status-text">系统状态：运行中</span>
    </div>

    <!-- 主标题 -->
    <div class="header-section">
      <h1 class="main-title">
        下一代 <span class="highlight">AI 代码审计</span> 平台
      </h1>
      <p class="subtitle">
        上传您的代码文件，由经过深度学习训练的 AI 模型进行实时分析，精准识别安全漏洞与逻辑缺陷。
      </p>
    </div>

    <!-- 信息卡片 -->
    <div class="info-cards">
      <div class="info-card">
        <div class="card-icon server-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="2" y="2" width="20" height="8" rx="2" ry="2"></rect>
            <rect x="2" y="14" width="20" height="8" rx="2" ry="2"></rect>
            <line x1="6" y1="6" x2="6.01" y2="6"></line>
            <line x1="6" y1="18" x2="6.01" y2="18"></line>
          </svg>
        </div>
        <div class="card-content">
          <h3 class="card-title">服务器</h3>
          <p class="card-value">{{ serverUrl }}</p>
        </div>
      </div>

      <div class="info-card">
        <div class="card-icon version-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="4" y="4" width="16" height="16" rx="2" ry="2"></rect>
            <rect x="9" y="9" width="6" height="6"></rect>
            <line x1="9" y1="1" x2="9" y2="4"></line>
            <line x1="15" y1="1" x2="15" y2="4"></line>
            <line x1="9" y1="20" x2="9" y2="23"></line>
            <line x1="15" y1="20" x2="15" y2="23"></line>
            <line x1="20" y1="9" x2="23" y2="9"></line>
            <line x1="20" y1="14" x2="23" y2="14"></line>
            <line x1="1" y1="9" x2="4" y2="9"></line>
            <line x1="1" y1="14" x2="4" y2="14"></line>
          </svg>
        </div>
        <div class="card-content">
          <h3 class="card-title">内核版本</h3>
          <p class="card-value">Spring Boot 3.5.10</p>
        </div>
      </div>
    </div>

    <!-- 登录/注册区域 -->
    <div v-if="!isLoggedIn" class="auth-panel">
      <div class="auth-header">
        <svg class="auth-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
          <circle cx="12" cy="7" r="4"></circle>
        </svg>
        <h2 class="auth-title">用户登录 / 注册</h2>
      </div>

      <div class="auth-form">
        <div class="form-group">
          <label class="form-label">账号</label>
          <input
              v-model="authForm.account"
              type="text"
              placeholder="请输入账号"
              class="modern-input"
          />
        </div>

        <div class="form-group">
          <label class="form-label">密码</label>
          <input
              v-model="authForm.password"
              type="password"
              placeholder="请输入密码"
              class="modern-input"
          />
        </div>

        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
              v-model="authForm.username"
              type="text"
              placeholder="请输入用户名（注册时必填）"
              class="modern-input"
          />
        </div>

        <div class="auth-actions">
          <button @click="handleRegister" :disabled="authLoading" class="btn-register">
            {{ authLoading ? '处理中...' : '注册' }}
          </button>
          <button @click="handleLogin" :disabled="authLoading" class="btn-login">
            {{ authLoading ? '处理中...' : '登录' }}
          </button>
        </div>

        <div class="auth-status">{{ authStatus }}</div>
      </div>
    </div>

    <!-- 主操作区域（登录后显示） -->
    <div v-else class="main-panel">
      <!-- 用户信息栏 -->
      <div class="user-bar">
        <div class="user-info">
          <svg class="user-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          <span class="user-name">{{ currentUser.username || currentUser.account }}</span>
          <span class="user-id">(ID: {{ currentUser.userId }})</span>
        </div>
        <button @click="handleLogout" class="logout-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
            <polyline points="16 17 21 12 16 7"></polyline>
            <line x1="21" y1="12" x2="9" y2="12"></line>
          </svg>
          退出登录
        </button>
      </div>

      <!-- 用户标识 -->
      <div class="section">
        <div class="section-header">
          <div class="section-title">
            <svg class="section-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="16" x2="12" y2="12"></line>
              <line x1="12" y1="8" x2="12.01" y2="8"></line>
            </svg>
            <span>用户标识</span>
          </div>
          <span class="section-label">唯一凭证</span>
        </div>
        <div class="input-wrapper">
          <input
              v-model="uploadForm.userId"
              type="number"
              :disabled="true"
              class="modern-input"
          />
          <svg class="input-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </div>
      </div>

      <!-- 审计负载 -->
      <div class="section">
        <div class="section-header">
          <div class="section-title">
            <svg class="section-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
            <span>审查区域</span>
          </div>
          <span class="section-label">代码源码</span>
        </div>

        <!-- 文件上传区域 -->
        <div class="upload-zone" :class="{ 'has-file': selectedFile }">
          <input
              ref="fileInput"
              type="file"
              id="file"
              accept=".java,.py,.js,.txt,.cpp,.c,.go"
              @change="handleFileSelect"
              class="file-input-hidden"
          />
          <label v-if="!selectedFile" for="file" class="upload-zone-label">
            <div class="upload-zone-content">
              <div class="upload-icon-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                  <polyline points="14 2 14 8 20 8"></polyline>
                  <line x1="12" y1="18" x2="12" y2="12"></line>
                  <line x1="9" y1="15" x2="15" y2="15"></line>
                </svg>
              </div>
              <div class="upload-text">
                <p class="upload-main">点击上传文件</p>
                <p class="upload-sub">支持主流后端与前端语言文件</p>
              </div>
            </div>
          </label>

          <div v-else class="file-selected-zone">
            <div class="file-info">
              <svg class="file-icon-large" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
              <div class="file-details-info">
                <p class="file-name">{{ selectedFile.name }}</p>
                <p class="file-size">{{ formatFileSize(selectedFile.size) }}</p>
              </div>
              <button type="button" class="remove-btn" @click="clearFile">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 提交按钮 -->
      <button @click="handleSubmit" :disabled="uploading || !selectedFile" class="submit-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
          <polyline points="22 4 12 14.01 9 11.01"></polyline>
        </svg>
        <span>{{ uploading ? 'AI 审查中...' : '开始 AI 智能审查' }}</span>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="5" y1="12" x2="19" y2="12"></line>
          <polyline points="12 5 19 12 12 19"></polyline>
        </svg>
      </button>
    </div>

    <!-- 结果展示 -->
    <div v-if="result.show" :class="['result-panel', result.type]">
      <div class="result-header">
        <h3>{{ result.title }}</h3>
      </div>
      <div class="result-content" v-html="result.content"></div>

      <button
          v-if="currentResultId"
          @click="openReviewPage"
          class="open-file-btn"
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
          <circle cx="12" cy="12" r="3"></circle>
        </svg>
        打开审查文件
      </button>

      <div v-if="reviewedFileInfo" class="file-details">
        <div class="detail-item">
          <span class="detail-label">文件 ID：</span>
          <span class="detail-value">{{ reviewedFileInfo.fileId }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">文件名：</span>
          <span class="detail-value">{{ reviewedFileInfo.fileName }}</span>
        </div>
        <a :href="downloadUrl" :download="reviewedFileInfo.fileName" class="download-link">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
            <polyline points="7 10 12 15 17 10"></polyline>
            <line x1="12" y1="15" x2="12" y2="3"></line>
          </svg>
          下载审查文件
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 响应式数据
const isLoggedIn = ref(false)
const authLoading = ref(false)
const uploading = ref(false)
const currentResultId = ref(null)
const currentUser = ref(null)
const reviewedFileInfo = ref(null)
const fileInput = ref(null)
const selectedFile = ref(null)

// 表单数据
const authForm = reactive({
  account: '',
  password: '',
  username: ''
})

const uploadForm = reactive({
  userId: '1'
})

const result = reactive({
  show: false,
  type: 'success',
  title: '',
  content: ''
})

// 计算属性
const authStatus = computed(() => {
  if (currentUser.value) {
    return `当前用户：${currentUser.value.username || currentUser.value.account} (ID=${currentUser.value.userId})`
  }
  return '请先登录或注册'
})

const serverUrl = computed(() => {
  return window.location.origin || 'http://localhost:8080'
})

const downloadUrl = computed(() => {
  if (reviewedFileInfo.value) {
    return `/api/file/download/${reviewedFileInfo.value.fileId}`
  }
  return '#'
})

// 工具函数
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 生命周期
onMounted(() => {
  try {
    const stored = localStorage.getItem('coderadar_current_user')
    if (stored) {
      const user = JSON.parse(stored)
      if (user && user.userId) {
        currentUser.value = user
        uploadForm.userId = user.userId.toString()
        isLoggedIn.value = true
      }
    }
  } catch (e) {
    console.warn('恢复登录状态失败', e)
  }
})

// 事件处理函数
const handleFileSelect = (event) => {
  selectedFile.value = event.target.files[0]
}

const clearFile = () => {
  selectedFile.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const handleRegister = async () => {
  if (!authForm.account || !authForm.password || !authForm.username) {
    alert('注册时账号、密码、用户名都不能为空')
    return
  }

  authLoading.value = true
  try {
    const resp = await fetch('/api/users/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        account: authForm.account,
        username: authForm.username,
        passwordHash: authForm.password
      })
    })

    const data = await resp.json()

    if (resp.ok && data.code === 200 && data.data) {
      currentUser.value = data.data
      uploadForm.userId = currentUser.value.userId
      isLoggedIn.value = true

      localStorage.setItem('coderadar_current_user', JSON.stringify(currentUser.value))

      alert('注册成功并已自动登录')

      authForm.account = ''
      authForm.password = ''
      authForm.username = ''
    } else {
      alert('注册失败：' + (data.message || resp.status))
    }
  } catch (error) {
    alert('注册请求失败：' + error.message)
  } finally {
    authLoading.value = false
  }
}

const handleLogin = async () => {
  if (!authForm.account || !authForm.password) {
    alert('请输入账号和密码')
    return
  }

  authLoading.value = true
  try {
    const resp = await fetch('/api/users/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        account: authForm.account,
        passwordHash: authForm.password
      })
    })

    const data = await resp.json()

    if (resp.ok && data.code === 200 && data.data) {
      currentUser.value = data.data
      uploadForm.userId = currentUser.value.userId
      isLoggedIn.value = true

      localStorage.setItem('coderadar_current_user', JSON.stringify(currentUser.value))

      alert('登录成功')

      authForm.account = ''
      authForm.password = ''
    } else {
      alert('登录失败：' + (data.message || resp.status))
    }
  } catch (error) {
    alert('登录请求失败：' + error.message)
  } finally {
    authLoading.value = false
  }
}

const handleLogout = () => {
  currentUser.value = null
  localStorage.removeItem('coderadar_current_user')
  uploadForm.userId = '1'
  isLoggedIn.value = false
  resetResult()
  currentResultId.value = null
  reviewedFileInfo.value = null
}

const handleSubmit = async () => {
  if (!selectedFile.value) {
    alert('请选择文件！')
    return
  }

  uploading.value = true

  try {
    const formData = new FormData()
    formData.append('userId', uploadForm.userId)
    formData.append('file', selectedFile.value)

    const resp = await fetch('/api/file/upload', {
      method: 'POST',
      body: formData
    })

    const text = await resp.text()
    let data

    try {
      data = JSON.parse(text)
    } catch (e) {
      result.show = true
      result.type = 'error'
      result.title = '解析失败'
      result.content = '返回数据格式错误'
      return
    }

    if (!resp.ok) {
      result.show = true
      result.type = 'error'
      result.title = `上传失败（HTTP ${resp.status}）`
      result.content = text
      return
    }

    if (data.data && data.data.result) {
      const resultData = data.data.result
      currentResultId.value = resultData.resultId

      result.show = true
      result.type = 'success'
      result.title = 'AI 审计完成'
      result.content = `
        <p style="margin: 8px 0;">
          <strong>文件 ID：</strong>${resultData.fileId || '未知'}<br>
          <strong>请求 ID：</strong>${resultData.requestId || '未知'}
        </p>
        <p style="margin-top: 12px; color: #64748b; font-size: 14px;">
          点击下方"打开审查文件"，在新页面中按行查看代码与审计建议。
        </p>
      `
      return
    }

    if (data.data && data.data.reviewError) {
      result.show = true
      result.type = 'error'
      result.title = 'AI 审计失败'
      result.content = `
        <p>${data.data.reviewError}</p>
        <p style="margin-top: 10px; color: #64748b; font-size: 13px;">
          可能原因：API Key、网络或配额问题。<br>
          详见控制台日志（F12 → Console）。
        </p>
      `
      return
    }

    result.show = true
    result.type = 'success'
    result.title = '上传成功'
    result.content = `<pre style="background: #f1f5f9; padding: 12px; border-radius: 6px; overflow-x: auto; font-size: 12px;">${JSON.stringify(data, null, 2)}</pre>`

  } catch (error) {
    result.show = true
    result.type = 'error'
    result.title = '网络错误'
    result.content = error.message
  } finally {
    uploading.value = false
  }
}

const openReviewPage = () => {
  if (!currentResultId.value) {
    alert('当前没有可查看的审查结果')
    return
  }
  router.push(`/review?resultId=${encodeURIComponent(currentResultId.value)}`)
}

const resetResult = () => {
  result.show = false
  result.type = 'success'
  result.title = ''
  result.content = ''
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 40px 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Noto Sans SC', sans-serif;
}

/* 顶部状态栏 */
.status-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 24px;
  font-size: 13px;
  color: #10b981;
  font-weight: 500;
}

.status-dot {
  font-size: 8px;
}

.status-text {
  letter-spacing: 0.5px;
}

/* 主标题区域 */
.header-section {
  text-align: center;
  margin-bottom: 40px;
}

.main-title {
  font-size: 42px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 16px;
  letter-spacing: -1px;
}

.highlight {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 16px;
  color: #64748b;
  line-height: 1.7;
  max-width: 700px;
  margin: 0 auto;
}

/* 信息卡片 */
.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  max-width: 800px;
  margin: 0 auto 40px;
}

.info-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.info-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.server-icon {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #3b82f6;
}

.version-icon {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #f59e0b;
}

.card-icon svg {
  width: 24px;
  height: 24px;
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
  margin-bottom: 8px;
}

.card-value {
  font-size: 16px;
  color: #1e293b;
  font-weight: 600;
}

/* 登录/注册面板 */
.auth-panel {
  max-width: 500px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
}

.auth-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 32px;
}

.auth-icon {
  width: 32px;
  height: 32px;
  color: #6366f1;
}

.auth-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
}

.auth-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 8px;
}

.btn-register {
  padding: 14px 24px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-register:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 12px -2px rgba(16, 185, 129, 0.3);
}

.btn-login {
  padding: 14px 24px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 12px -2px rgba(99, 102, 241, 0.3);
}

.btn-register:disabled,
.btn-login:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.auth-status {
  text-align: center;
  font-size: 13px;
  color: #94a3b8;
  margin-top: 8px;
}

/* 主操作面板 */
.main-panel {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
}

/* 用户信息栏 */
.user-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 12px;
  margin-bottom: 32px;
  border: 1px solid #bae6fd;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  color: #1e293b;
  font-weight: 600;
}

.user-icon {
  width: 20px;
  height: 20px;
  color: #0ea5e9;
}

.user-id {
  color: #94a3b8;
  font-weight: 400;
  font-size: 13px;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #ef4444;
}

.logout-btn svg {
  width: 16px;
  height: 16px;
}

.section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.section-icon {
  width: 22px;
  height: 22px;
  color: #64748b;
}

.section-label {
  font-size: 12px;
  color: #cbd5e1;
  font-weight: 500;
}

/* 输入框样式 */
.input-wrapper {
  position: relative;
}

.modern-input {
  width: 100%;
  padding: 16px 20px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  transition: all 0.3s;
}

.modern-input:focus {
  outline: none;
  border-color: #6366f1;
  background: white;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.input-arrow {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  color: #cbd5e1;
}

/* 上传区域 - 缩小版 */
.upload-zone {
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  transition: all 0.3s;
  background: #f8fafc;
  min-height: 120px;
  display: flex;
  align-items: center;
}

.upload-zone:hover {
  border-color: #6366f1;
  background: #f1f5f9;
}

.upload-zone.has-file {
  border-color: #10b981;
  background: #f0fdf4;
  border-style: solid;
}

.file-input-hidden {
  display: none;
}

.upload-zone-label {
  display: block;
  width: 100%;
  cursor: pointer;
}

.upload-zone-content {
  padding: 24px;
  text-align: center;
}

.upload-icon-wrapper {
  width: 48px;
  height: 48px;
  margin: 0 auto 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-icon-wrapper svg {
  width: 24px;
  height: 24px;
  color: #64748b;
}

.upload-text {
  margin-top: 8px;
}

.upload-main {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.upload-sub {
  font-size: 13px;
  color: #94a3b8;
}

/* 已上传文件显示区域 */
.file-selected-zone {
  width: 100%;
  padding: 16px 24px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.file-icon-large {
  width: 40px;
  height: 40px;
  color: #10b981;
  flex-shrink: 0;
}

.file-details-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.remove-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #fee2e2;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.remove-btn:hover {
  background: #fecaca;
}

.remove-btn svg {
  width: 18px;
  height: 18px;
  color: #ef4444;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  padding: 18px 32px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  transition: all 0.3s;
  box-shadow: 0 4px 6px -1px rgba(99, 102, 241, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(99, 102, 241, 0.4);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.submit-btn svg {
  width: 22px;
  height: 22px;
}

/* 结果面板 */
.result-panel {
  max-width: 800px;
  margin: 32px auto 0;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
  border-left: 4px solid;
}

.result-panel.success {
  border-left-color: #10b981;
}

.result-panel.error {
  border-left-color: #ef4444;
}

.result-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
}

.result-content {
  font-size: 14px;
  color: #64748b;
  line-height: 1.7;
}

.open-file-btn {
  margin-top: 20px;
  width: 100%;
  padding: 14px 24px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s;
}

.open-file-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 12px -2px rgba(59, 130, 246, 0.3);
}

.open-file-btn svg {
  width: 20px;
  height: 20px;
}

.file-details {
  margin-top: 20px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
}

.detail-item {
  margin-bottom: 12px;
  font-size: 14px;
}

.detail-label {
  color: #64748b;
  font-weight: 500;
}

.detail-value {
  color: #1e293b;
  font-weight: 600;
}

.download-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  text-decoration: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
}

.download-link:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 12px -2px rgba(16, 185, 129, 0.3);
}

.download-link svg {
  width: 18px;
  height: 18px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 24px 16px;
  }

  .main-title {
    font-size: 28px;
  }

  .subtitle {
    font-size: 14px;
  }

  .info-cards {
    grid-template-columns: 1fr;
  }

  .auth-panel,
  .main-panel {
    padding: 24px;
  }

  .section-title {
    font-size: 16px;
  }

  .upload-zone-content {
    padding: 20px 16px;
  }

  .submit-btn {
    font-size: 16px;
    padding: 16px 24px;
  }

  .auth-actions {
    grid-template-columns: 1fr;
  }

  .user-bar {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .user-info {
    justify-content: center;
  }
}
</style>

