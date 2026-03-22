 <script>
 export default {
   name: "ReviewPage"
 }
 </script>

 <template>
   <div class="container">
     <!-- 顶部状态栏 -->
     <div class="status-bar">
       <span class="status-dot">●</span>
       <span class="status-text">系统状态：运行中</span>
     </div>

     <!-- 主标题 -->
     <div class="header-section">
       <h1 class="main-title">
         📄 <span class="highlight">审查结果查看</span>
       </h1>
       <p class="meta">{{ metaInfo }}</p>
     </div>

     <!-- 信息卡片 - 审查总结 -->
     <div class="summary-card">
       <div class="card-icon summary-icon">
         <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
           <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
           <polyline points="14 2 14 8 20 8"></polyline>
           <line x1="16" y1="13" x2="8" y2="13"></line>
           <line x1="16" y1="17" x2="8" y2="17"></line>
           <polyline points="10 9 9 9 8 9"></polyline>
         </svg>
       </div>
       <div class="card-content">
         <h3 class="card-title">AI 审查总结</h3>
         <p class="card-value">{{ summary }}</p>
       </div>
     </div>

     <!-- 工具栏 -->
     <div class="toolbar">
       <button
           @click="handleReReview"
           :disabled="reReviewLoading"
           class="btn-primary"
       >
         <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
           <polyline points="23 4 23 10 17 10"></polyline>
           <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"></path>
         </svg>
         <span>{{ reReviewButtonText }}</span>
       </button>
       <button
           @click="handleApplyDownload"
           :disabled="!hasSuggestions"
           class="btn-success"
       >
         <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
           <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
           <polyline points="7 10 12 15 17 10"></polyline>
           <line x1="12" y1="15" x2="12" y2="3"></line>
         </svg>
         选择建议并下载审查文件
       </button>
       <button
           @click="handleDiscard"
           :disabled="!currentReviewedFileId"
           class="btn-danger"
       >
         <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
           <polyline points="3 6 5 6 21 6"></polyline>
           <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
         </svg>
         放弃
       </button>
     </div>

     <!-- 返回按钮 -->
     <div class="back-button-wrapper">
       <button @click="handleBack" class="back-btn">
         <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
           <line x1="19" y1="12" x2="5" y2="12"></line>
           <polyline points="12 19 5 12 12 5"></polyline>
         </svg>
         返回上传页面
       </button>
     </div>

     <!-- 内容区域 -->
     <div class="content">
       <!-- 代码面板 -->
       <div class="code-panel">
         <div class="panel-header">
           <div class="panel-title">
             <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
               <polyline points="16 18 22 12 16 6"></polyline>
               <polyline points="8 6 2 12 8 18"></polyline>
             </svg>
             <span>{{ fileName }}</span>
           </div>
           <span class="panel-label">{{ lineInfo }}</span>
         </div>
         <div class="panel-body">
           <div class="panel-body-inner" ref="codeContainer">
             <div v-if="!codeContent" class="empty-tip">
               正在加载代码内容...
             </div>
             <div
                 v-for="(line, index) in codeLines"
                 :key="index"
                 :class="['code-line', { 'has-suggestion': hasSuggestion(index + 1) }]"
                 @click="handleLineClick(index + 1)"
             >
               <span class="line-number">{{ index + 1 }}</span>
               <span class="line-content">{{ line === '' ? ' ' : line }}</span>
             </div>
           </div>
         </div>
       </div>

       <!-- 建议面板 -->
       <div class="suggestion-panel">
         <div class="panel-header">
           <div class="panel-title">
             <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
               <circle cx="12" cy="12" r="10"></circle>
               <line x1="12" y1="16" x2="12" y2="12"></line>
               <line x1="12" y1="8" x2="12.01" y2="8"></line>
             </svg>
             <span>审查建议</span>
           </div>
           <span class="panel-label">{{ sugCountText }}</span>
         </div>
         <div class="suggestions-list" ref="suggestionsContainer">
           <div v-if="suggestions.length === 0" class="empty-tip">
             ✨ 代码质量良好，暂无建议。
           </div>
           <div
               v-for="(suggestion, index) in suggestions"
               :key="suggestion.suggestionId || index"
               :class="['suggestion-item', { active: activeSuggestionIndex === index }]"
               @click="handleSuggestionClick(suggestion, index)"
           >
             <div class="suggestion-meta">
               <span class="suggestion-left">
                 <input
                     type="checkbox"
                     v-model="selectedSuggestions[index]"
                     @click.stop
                 />
                 <span class="category-pill">{{ prettyCategory(suggestion.category) }}</span>
                 <span class="line-pill">行 {{ suggestion.lineNumbers || '-' }}</span>
               </span>
               <span
                   :class="['severity-tag', getSeverityClass(suggestion.severity)]"
               >
                 {{ suggestion.severity || 'MEDIUM' }}
               </span>
             </div>
             <div class="suggestion-text">{{ suggestion.suggestion }}</div>
           </div>
         </div>
       </div>
     </div>

     <!-- 底部状态栏 -->
     <div class="status-bar-bottom">{{ statusText }}</div>
   </div>
 </template>

 <script setup>
 import { ref, computed, onMounted, reactive, watch, nextTick } from 'vue'
 import { useRoute, useRouter } from 'vue-router'

 const route = useRoute()
 const router = useRouter()

 // 响应式数据
 const metaInfo = ref('正在加载审查信息...')
 const summary = ref('正在加载 AI 审查总结...')
 const fileName = ref('代码文件')
 const lineInfo = ref('行数：0')
 const statusText = ref('就绪')

 const currentResultId = ref(null)
 const currentFileId = ref(null)
 const currentUserId = ref(null)
 const currentModel = ref('deepseek-chat')
 const currentReviewedFileId = ref(null)
 const originalContent = ref('')
 const originalFileName = ref('file.txt')
 const originalFileType = ref('')

 const codeContent = ref('')
 const suggestions = ref([])
 const codeLines = ref([])
 const suggestionsByLine = ref({})
 const selectedSuggestions = ref([])
 const activeSuggestionIndex = ref(-1)
 const reReviewLoading = ref(false)
 const applyDownloadLoading = ref(false)
 const discardLoading = ref(false)

 // 计算属性
 const sugCountText = computed(() => {
   return suggestions.value.length === 0
       ? '0 条建议'
       : `${suggestions.value.length} 条建议`
 })

 const reReviewButtonText = computed(() => {
   return reReviewLoading.value ? '正在重新审查...' : '开始 AI 审查'
 })

 const hasSuggestions = computed(() => suggestions.value.length > 0)

 // 生命周期
 onMounted(() => {
   const resultId = route.query.resultId
   if (resultId) {
     currentResultId.value = resultId
     loadDetail(resultId)
   } else {
     metaInfo.value = '缺少 resultId，请从上传页面重新进入本页面。'
     summary.value = '无法加载审查详情，请返回上传页面重新开始。'
     statusText.value = '缺少 resultId'
   }
 })

 // 方法
 const handleBack = () => {
   router.push('/')
 }

 const hasSuggestion = (lineNumber) => {
   return !!suggestionsByLine.value[lineNumber]
 }

 const handleLineClick = (lineNumber) => {
   highlightLine(lineNumber)
   scrollToSuggestionByLine(lineNumber)
 }

 const handleSuggestionClick = (suggestion, index) => {
   activeSuggestionIndex.value = index
   if (suggestion.lineStart) {
     highlightLine(suggestion.lineStart)
   }
 }

 const prettyCategory = (category) => {
   if (!category) return '代码问题'
   const c = category.trim()
   if (!c) return '代码问题'
   const lower = c.toLowerCase()

   // 英文关键词映射
   if (lower.includes('security')) return '安全风险'
   if (lower.includes('performance')) return '性能问题'
   if (lower.includes('readability')) return '可读性问题'
   if (lower.includes('style')) return '代码风格'
   if (lower.includes('logic')) return '逻辑错误'
   if (lower.includes('bug')) return '潜在 Bug'
   if (lower.includes('reliab')) return '可靠性问题'
   if (lower.includes('maintainab')) return '可维护性问题'
   if (lower.includes('robust')) return '健壮性问题'
   if (lower.includes('correct')) return '正确性问题'

   // 中文关键词映射
   if (c.includes('安全')) return '安全风险'
   if (c.includes('性能')) return '性能问题'
   if (c.includes('可读')) return '可读性问题'
   if (c.includes('风格')) return '代码风格'
   if (c.includes('逻辑')) return '逻辑错误'
   if (c.includes('可靠性')) return '可靠性问题'
   if (c.includes('可维护性')) return '可维护性问题'
   if (c.includes('健壮性')) return '健壮性问题'
   if (c.includes('正确性')) return '正确性问题'
   if (c.includes('并发') || c.includes('线程')) return '并发问题'

   return c
 }

 const getSeverityClass = (severity) => {
   const sev = (severity || 'MEDIUM').toUpperCase()
   switch (sev) {
     case 'HIGH': return 'sev-high'
     case 'LOW': return 'sev-low'
     default: return 'sev-medium'
   }
 }

 const highlightLine = (lineNumber) => {
   const codeLines = document.querySelectorAll('.code-line')
   codeLines.forEach(el => {
     if (Number(el.dataset.line) === Number(lineNumber)) {
       el.classList.add('has-suggestion')
       el.scrollIntoView({ block: 'center', behavior: 'smooth' })
     }
   })
 }

 const scrollToSuggestionByLine = (lineNumber) => {
   const target = Array.from(document.querySelectorAll('.suggestion-item'))
       .find(el => Number(el.dataset.line || 0) === Number(lineNumber))
   if (target) {
     target.classList.add('active')
     target.scrollIntoView({ block: 'nearest', behavior: 'smooth' })
     setTimeout(() => {
       target.classList.remove('active')
     }, 1200)
   }
 }

 const loadDetail = async (resultId) => {
   if (!resultId) {
     metaInfo.value = '缺少 resultId，无法加载审查详情。'
     setStatus('加载失败：缺少 resultId')
     return
   }

   setStatus('正在加载审查详情...')
   try {
     const resp = await fetch(`/api/reviews/${resultId}/detail`)
     const data = await resp.json()

     if (!resp.ok || data.code !== 200 || !data.data) {
       metaInfo.value = '加载审查详情失败。'
       summary.value = data.message || '无法获取审查详情。'
       setStatus('加载审查详情失败')
       return
     }

     const result = data.data.result
     const suggestionsData = data.data.suggestions || []

     // 更新数据
     suggestions.value = suggestionsData
     selectedSuggestions.value = new Array(suggestionsData.length).fill(false)

     // 重新分组建议
     groupSuggestionsByLine(suggestionsData)

     currentResultId.value = result.resultId
     currentFileId.value = result.fileId
     currentUserId.value = result.userId
     currentModel.value = result.model || currentModel.value

     metaInfo.value = `用户 ID：${currentUserId.value} · 文件 ID：${currentFileId.value} · 模型：${currentModel.value}`
     summary.value = result.summary || 'AI 审查完成。'

     await loadFileAndRender(currentFileId.value, suggestionsData)
     setStatus('审查详情加载完成')
   } catch (error) {
     metaInfo.value = '加载审查详情时出错。'
     summary.value = error.message || '未知错误'
     setStatus('加载审查详情时出错')
   }
 }

 const groupSuggestionsByLine = (suggestionsData) => {
   const map = {}
   suggestionsData.forEach(s => {
     let line = s.lineStart
     if (!line && s.lineNumbers) {
       try {
         const first = s.lineNumbers.split(/[-,]/)[0].trim()
         line = parseInt(first, 10)
       } catch (e) {
         line = null
       }
     }
     if (!line || line <= 0) return
     if (!map[line]) map[line] = []
     map[line].push(s)
   })
   suggestionsByLine.value = map
 }

 const loadFileAndRender = async (fileId, suggestionsData) => {
   if (!fileId) {
     fileName.value = '文件信息获取失败（缺少 fileId）'
     renderCode('', suggestionsData)
     return
   }

   setStatus('正在加载文件内容...')
   try {
     const resp = await fetch(`/api/file/${fileId}`)
     const data = await resp.json()

     if (!resp.ok || data.code !== 200 || !data.data) {
       fileName.value = `文件 ID：${fileId}（加载失败）`
       renderCode('', suggestionsData)
       setStatus('加载文件失败')
       return
     }

     const file = data.data
     fileName.value = `${file.originalFileName || '未命名文件'}（ID：${file.fileId}）`

     originalContent.value = file.fileContent || ''
     originalFileName.value = file.originalFileName || 'file.txt'
     originalFileType.value = file.fileType || ''
     codeContent.value = originalContent.value

     renderCode(originalContent.value, suggestionsData)
     setStatus('文件与建议加载完成')
   } catch (error) {
     fileName.value = `文件 ID：${fileId}（加载失败）`
     renderCode('', suggestionsData)
     setStatus('加载文件时出错')
   }
 }

 const renderCode = (content, suggestionsData) => {
   if (!content) {
     codeLines.value = []
     lineInfo.value = '行数：0'
     return
   }

   const lines = content.split(/\r?\n/)
   codeLines.value = lines
   lineInfo.value = `行数：${lines.length}`

   // 确保 DOM 更新后重新分组
   nextTick(() => {
     groupSuggestionsByLine(suggestionsData)
   })
 }

 const handleReReview = async () => {
   if (!currentUserId.value || !currentFileId.value) {
     alert('当前缺少用户或文件信息，无法发起审查。')
     return
   }

   reReviewLoading.value = true
   setStatus('正在重新发起 AI 审查...')

   try {
     const resp = await fetch('/api/reviews/submit', {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify({
         userId: currentUserId.value,
         fileId: currentFileId.value,
         background: null,
         model: currentModel.value || 'deepseek-chat'
       })
     })

     const data = await resp.json()

     if (!resp.ok || data.code !== 200 || !data.data) {
       alert('重新审查失败：' + (data.message || resp.status))
       setStatus('重新审查失败')
       return
     }

     const newResultId = data.data
     setStatus('重新审查完成，正在跳转页面...')

     // 跳转到新的审查结果页
     router.push(`/review?resultId=${encodeURIComponent(newResultId)}`)
   } catch (error) {
     alert('重新审查请求出错：' + error.message)
     setStatus('重新审查请求出错')
   } finally {
     reReviewLoading.value = false
   }
 }

 const handleDiscard = async () => {
   if (!currentReviewedFileId.value) {
     alert('当前没有可放弃的审查文件。')
     return
   }

   if (!confirm('确认要放弃并删除已生成的审查文件吗？')) {
     return
   }

   discardLoading.value = true
   setStatus('正在放弃并删除审查文件...')

   try {
     const resp = await fetch(`/api/reviews/files/${currentReviewedFileId.value}/discard`, {
       method: 'POST'
     })

     const data = await resp.json()

     if (!resp.ok || data.code !== 200) {
       alert('放弃失败：' + (data.message || resp.status))
       setStatus('放弃审查文件失败')
       return
     }

     currentReviewedFileId.value = null
     alert('已放弃并删除审查文件。')
     setStatus('审查文件已放弃并删除')
   } catch (error) {
     alert('放弃时出错：' + error.message)
     setStatus('放弃审查文件时出错')
   } finally {
     discardLoading.value = false
   }
 }

 const handleApplyDownload = async () => {
   if (!originalContent.value) {
     alert('当前文件内容为空或尚未加载完成。')
     return
   }

   // 获取选中的建议索引
   const checkedIndexes = selectedSuggestions.value
       .map((checked, index) => checked ? index : -1)
       .filter(i => i !== -1)

   if (checkedIndexes.length === 0) {
     if (!confirm('当前未选择任何建议，是否仅下载原始代码文件？')) {
       return
     }
   }

   // 只使用选中的建议
   const selected = checkedIndexes
       .map(i => suggestions.value[i])
       .filter(Boolean)

   // 生成并下载文件
   try {
     setStatus('正在生成审查文件...')

     // 生成请求体
     const body = checkedIndexes.length > 0
         ? checkedIndexes
             .map(i => suggestions.value[i])
             .filter(s => s.suggestionId)
             .map(s => s.suggestionId)
         : null

     // 1. 后端生成
     const resp = await fetch(`/api/reviews/${encodeURIComponent(currentResultId.value)}/generate-file`, {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: body && body.length > 0 ? JSON.stringify(body) : 'null'
     })

     const data = await resp.json()
     if (!resp.ok || data.code !== 200 || !data.data) {
       alert('生成审查文件失败：' + (data.message || resp.status))
       setStatus('生成审查文件失败')
       return
     }

     const newFileId = data.data
     currentReviewedFileId.value = newFileId

     // 2. 前端下载
     const newContent = buildReviewedContent(originalContent.value, selected, originalFileType.value)
     const blob = new Blob([newContent], { type: 'text/plain;charset=utf-8' })

     let downloadName = originalFileName.value || 'file.txt'
     const dotIndex = downloadName.lastIndexOf('.')
     if (dotIndex > 0) {
       const name = downloadName.substring(0, dotIndex)
       const ext = downloadName.substring(dotIndex)
       downloadName = name + '_reviewed' + ext
     } else {
       downloadName = downloadName + '_reviewed'
     }

     const url = URL.createObjectURL(blob)
     const a = document.createElement('a')
     a.href = url
     a.download = downloadName
     document.body.appendChild(a)
     a.click()
     document.body.removeChild(a)
     URL.revokeObjectURL(url)

     setStatus(`已生成并保存审查文件（ID=${newFileId}），同时完成本地下载`)
   } catch (error) {
     console.error('下载处理失败', error)
     alert('生成或下载审查文件时出错：' + error.message)
     setStatus('生成或下载审查文件时出错')
   }
 }

 const getCommentPrefix = (fileType) => {
   if (!fileType) return '//'
   const t = fileType.toLowerCase()
   if (['java', 'js', 'ts', 'cpp', 'c'].includes(t)) return '//'
   if (['py', 'sh'].includes(t)) return '#'
   if (['html', 'xml'].includes(t)) return '<!--'
   return '//'
 }

 const stripExistingSuggestionComments = (baseContent, fileType) => {
   const prefix = getCommentPrefix(fileType)
   const lines = baseContent.split(/\r?\n/)
   const cleaned = lines.filter(line => {
     const trimmed = line.trimStart()
     if (trimmed.startsWith(prefix)) {
       const afterPrefix = trimmed.substring(prefix.length).trim()
       if (afterPrefix.startsWith('[') && afterPrefix.includes(']') && afterPrefix.includes(':')) {
         return false
       }
     }
     return true
   })
   return cleaned.join('\n')
 }

 const buildReviewedContent = (baseContent, selectedSuggestions, fileType) => {
   const baseWithoutComments = stripExistingSuggestionComments(baseContent, fileType)

   if (!selectedSuggestions || selectedSuggestions.length === 0) {
     return baseWithoutComments
   }

   const suggestionsCopy = selectedSuggestions.slice().map(s => {
     let line = s.lineStart
     if (!line && s.lineNumbers) {
       try {
         const first = s.lineNumbers.split(/[-,]/)[0].trim()
         line = parseInt(first, 10)
       } catch (e) {
         line = null
       }
     }
     return { ...s, lineStart: line }
   }).filter(s => s.lineStart && s.lineStart > 0)

   suggestionsCopy.sort((a, b) => a.lineStart - b.lineStart)

   const lines = baseWithoutComments.split(/\r?\n/)
   let result = ''
   let idx = 0

   for (let i = 0; i < lines.length; i++) {
     const currentLine = i + 1
     while (idx < suggestionsCopy.length && suggestionsCopy[idx].lineStart === currentLine) {
       result += formatSuggestionAsComment(suggestionsCopy[idx], fileType) + '\n'
       idx++
     }
     result += lines[i] + '\n'
   }

   while (idx < suggestionsCopy.length) {
     result += formatSuggestionAsComment(suggestionsCopy[idx], fileType) + '\n'
     idx++
   }

   return result
 }

 const formatSuggestionAsComment = (suggestion, fileType) => {
   const prefix = getCommentPrefix(fileType)
   const categoryLabel = prettyCategory(suggestion.category)
   const lineInfo = suggestion.lineNumbers ? `行${suggestion.lineNumbers}` : '相关行'
   return `${prefix} ("${categoryLabel}"):    ${lineInfo}: ${suggestion.suggestion || ''}`
 }

 const setStatus = (text) => {
   statusText.value = text
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
   margin-bottom: 32px;
 }

 .main-title {
   font-size: 36px;
   font-weight: 700;
   color: #1e293b;
   margin-bottom: 12px;
   letter-spacing: -1px;
 }

 .highlight {
   background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
   -webkit-background-clip: text;
   -webkit-text-fill-color: transparent;
   background-clip: text;
 }

 .meta {
   font-size: 14px;
   color: #64748b;
   line-height: 1.6;
 }

 /* 信息卡片 - 审查总结 */
 .summary-card {
   max-width: 1100px;
   margin: 0 auto 24px;
   background: white;
   border-radius: 16px;
   padding: 24px;
   display: flex;
   align-items: center;
   gap: 20px;
   box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
   transition: all 0.3s;
 }

 .summary-card:hover {
   transform: translateY(-2px);
   box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
 }

 .summary-icon {
   width: 56px;
   height: 56px;
   border-radius: 12px;
   background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
   color: #3b82f6;
   display: flex;
   align-items: center;
   justify-content: center;
   flex-shrink: 0;
 }

 .summary-icon svg {
   width: 28px;
   height: 28px;
 }

 .card-title {
   font-size: 13px;
   color: #94a3b8;
   font-weight: 500;
   margin-bottom: 8px;
 }

 .card-value {
   font-size: 15px;
   color: #1e293b;
   font-weight: 600;
   line-height: 1.6;
 }

 /* 工具栏 */
 .toolbar {
   max-width: 1100px;
   margin: 0 auto 24px;
   display: flex;
   gap: 12px;
   flex-wrap: wrap;
 }

 .toolbar button {
   flex: 0 0 auto;
   padding: 12px 20px;
   border-radius: 10px;
   border: none;
   cursor: pointer;
   font-size: 14px;
   font-weight: 600;
   color: #fff;
   display: flex;
   align-items: center;
   gap: 8px;
   transition: all 0.3s;
 }

 .toolbar button svg {
   width: 18px;
   height: 18px;
 }

 .btn-primary {
   background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
 }

 .btn-primary:hover:not(:disabled) {
   transform: translateY(-2px);
   box-shadow: 0 8px 12px -2px rgba(99, 102, 241, 0.3);
 }

 .btn-success {
   background: linear-gradient(135deg, #10b981 0%, #059669 100%);
 }

 .btn-success:hover:not(:disabled) {
   transform: translateY(-2px);
   box-shadow: 0 8px 12px -2px rgba(16, 185, 129, 0.3);
 }

 .btn-danger {
   background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
 }

 .btn-danger:hover:not(:disabled) {
   transform: translateY(-2px);
   box-shadow: 0 8px 12px -2px rgba(239, 68, 68, 0.3);
 }

 .toolbar button:disabled {
   opacity: 0.5;
   cursor: not-allowed;
   transform: none;
 }

 /* 返回按钮 */
 .back-button-wrapper {
   max-width: 1100px;
   margin: 0 auto 24px;
   text-align: right;
 }

 .back-btn {
   display: inline-flex;
   align-items: center;
   gap: 8px;
   padding: 12px 20px;
   background: linear-gradient(135deg, #64748b 0%, #475569 100%);
   color: white;
   border: none;
   border-radius: 10px;
   font-size: 14px;
   font-weight: 600;
   cursor: pointer;
   transition: all 0.3s;
   box-shadow: 0 4px 6px -1px rgba(100, 116, 139, 0.3);
 }

 .back-btn:hover {
   transform: translateY(-2px);
   box-shadow: 0 8px 12px -2px rgba(100, 116, 139, 0.4);
 }

 .back-btn svg {
   width: 18px;
   height: 18px;
 }

 /* 内容区域 */
 .content {
   max-width: 1100px;
   margin: 0 auto;
   display: grid;
   grid-template-columns: 2fr 1fr;
   gap: 16px;
   height: calc(100vh - 400px);
      min-height: 500px;
 }

 .code-panel, .suggestion-panel {
   border-radius: 16px;
   overflow: hidden;
   display: flex;
   flex-direction: column;
   background: white;
   box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
   min-height: 0;
 }

 .panel-header {
   background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
   padding: 16px 20px;
   border-bottom: 1px solid #e2e8f0;
   font-size: 14px;
   display: flex;
   justify-content: space-between;
   align-items: center;
 }

 .panel-title {
   display: flex;
   align-items: center;
   gap: 10px;
   font-weight: 600;
   color: #1e293b;
 }

 .panel-title svg {
   width: 18px;
   height: 18px;
   color: #64748b;
 }

 .panel-label {
   font-size: 13px;
   color: #94a3b8;
   font-weight: 500;
 }

 .panel-body {
    flex: 1;
    overflow: auto;
    background: #0b1020;
    color: #e0e0e0;
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
    font-size: 0.85rem;
    min-height: 0;


    scrollbar-width: thin;
    scrollbar-color: #475569 #1e293b;
  }

  .panel-body::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  .panel-body::-webkit-scrollbar-track {
    background: #1e293b;
    border-radius: 4px;
  }

  .panel-body::-webkit-scrollbar-thumb {
    background: #475569;
    border-radius: 4px;
  }

  .panel-body::-webkit-scrollbar-thumb:hover {
    background: #64748b;
  }

 .panel-body-inner {
   padding: 12px 16px;
   min-width: max-content;
 }

 .code-line {
   display: grid;
   grid-template-columns: 48px 1fr;
   gap: 12px;
   align-items: flex-start;
   padding: 4px 0;
   cursor: pointer;
   transition: background 0.2s;
   min-width: fit-content;
 }

 .code-line:hover {
   background: rgba(99, 102, 241, 0.1);
 }

 .line-number {
   text-align: right;
   color: #6b7280;
   user-select: none;
   font-weight: 500;
 }

 .line-content {
   white-space: pre;
   overflow-wrap: normal;
 }

 .code-line.has-suggestion .line-number {
   color: #f97373;
   font-weight: 700;
 }

 .code-line.has-suggestion {
   background: rgba(239, 68, 68, 0.1);
 }

 .suggestion-panel {
   background: white;
 }

 .suggestions-list {
    padding: 16px;
    overflow-y: auto;
    font-size: 0.85rem;
    flex: 1;
    min-height: 0;


    scrollbar-width: thin;
    scrollbar-color: #cbd5e1 #f1f5f9;
  }

  .suggestions-list::-webkit-scrollbar {
    width: 8px;
  }

  .suggestions-list::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 4px;
  }

  .suggestions-list::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 4px;
  }

  .suggestions-list::-webkit-scrollbar-thumb:hover {
    background: #94a3b8;
  }

 .suggestion-item {
   border-radius: 10px;
   padding: 14px;
   margin-bottom: 10px;
   border-left: 4px solid #9ca3af;
   background: #f8fafc;
   cursor: pointer;
   transition: all 0.2s;
 }

 .suggestion-item:hover {
   background: #f1f5f9;
   transform: translateX(2px);
   border-left-color: #6366f1;
 }

 .suggestion-item.active {cd
   background: #e0e7ff;
   border-left-color: #6366f1;
   box-shadow: 0 2px 8px rgba(99, 102, 241, 0.15);
 }

 .suggestion-meta {
   display: flex;
   justify-content: space-between;
   align-items: center;
   margin-bottom: 6px;
   font-size: 12px;
   line-height: 1.4;
   color: #64748b;
 }

 .suggestion-left {
   display: inline-flex;
   align-items: center;
   gap: 8px;
   min-width: 0;
 }

 .suggestion-left input[type="checkbox"] {
   width: 16px;
   height: 16px;
   cursor: pointer;
 }

 .category-pill {
   display: inline-flex;
   align-items: center;
   height: 22px;
   padding: 0 10px;
   border-radius: 999px;
   font-size: 11px;
   font-weight: 700;
   letter-spacing: 0.3px;
   background: #e2e8f0;
   color: #475569;
   white-space: nowrap;
 }

 .line-pill {
   white-space: nowrap;
   color: #64748b;
   font-weight: 600;
   font-size: 11px;
 }

 .severity-tag {
   padding: 2px 8px;
   border-radius: 999px;
   font-size: 10px;
   font-weight: 700;
   color: #fff;
   text-transform: uppercase;
 }

 .sev-high { background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); }
 .sev-medium { background: linear-gradient(135deg, #fb923c 0%, #ea580c 100%); }
 .sev-low { background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%); }

 .suggestion-text {
   font-size: 13px;
   color: #1e293b;
   line-height: 1.6;
 }

 .empty-tip {
   color: #94a3b8;
   font-size: 14px;
   padding: 24px;
   text-align: center;
 }

 /* 底部状态栏 */
 .status-bar-bottom {
   max-width: 1100px;
   margin: 20px auto 0;
   font-size: 13px;
   color: #64748b;
   text-align: right;
   padding: 12px 20px;
   background: white;
   border-radius: 12px;
   box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
 }

 /* 响应式设计 */
 @media (max-width: 900px) {
   .container {
     padding: 24px 16px;
   }

   .main-title {
     font-size: 28px;
   }

   .content {
     grid-template-columns: 1fr;
   }

   .summary-card {
     flex-direction: column;
     text-align: center;
   }

   .toolbar {
     justify-content: center;
   }

   .back-button-wrapper {
     text-align: center;
   }
 }
 </style>
