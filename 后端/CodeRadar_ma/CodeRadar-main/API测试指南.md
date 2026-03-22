# CodeRadar API 测试指南

## 🌐 访问地址

### 主页面（推荐）
启动项目后，在浏览器访问：
```
http://localhost:8080
```
或
```
http://localhost:8080/index.html
```

这个页面提供了：
- ✅ 系统状态检查
- 📤 可视化文件上传界面
- 📡 API 端点说明
- 🎨 美观的用户界面

---

## 📡 API 端点详情

### 1. 上传文件并审查
**端点：** `POST /api/file/upload`

**参数：**
- `userId` (Long) - 用户 ID
- `file` (MultipartFile) - 代码文件

**使用 curl 测试：**
```bash
curl -X POST http://localhost:8080/api/file/upload \
  -F "userId=1" \
  -F "file=@D:/test.java"
```

**使用 PowerShell 测试：**
```powershell
$uri = "http://localhost:8080/api/file/upload"
$filePath = "D:/test.java"
$userId = 1

$form = @{
    userId = $userId
    file = Get-Item -Path $filePath
}

Invoke-RestMethod -Uri $uri -Method Post -Form $form
```

**返回示例：**
```json
{
  "resultId": 1,
  "requestId": "req-12345",
  "userId": 1,
  "fileId": 1,
  "model": "deepseek-chat",
  "reviewTime": "2024-01-01T10:00:00",
  "summary": "代码整体质量良好，有几处需要改进...",
  "rawJson": "{...}",
  "createdAt": "2024-01-01T10:00:00"
}
```

---

### 2. 获取审查结果
**端点：** `GET /api/file/result/{requestId}`

**参数：**
- `requestId` (String) - 请求 ID（从上传接口返回）

**使用浏览器测试：**
```
http://localhost:8080/api/file/result/req-12345
```

**使用 curl 测试：**
```bash
curl http://localhost:8080/api/file/result/req-12345
```

**使用 PowerShell 测试：**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/file/result/req-12345" -Method Get
```

---

## 🧪 完整测试流程

### 步骤 1：启动项目
在 IntelliJ IDEA 中运行 `CodeRadarApplication.java`，或使用命令：
```bash
cd "D:/浏览器下载/CodeRadar-main/CodeRadar-main"
mvn spring-boot:run
```

### 步骤 2：验证服务启动
看到以下日志表示启动成功：
```
Started CodeRadarApplication in X.XXX seconds
```

### 步骤 3：访问主页
打开浏览器访问：
```
http://localhost:8080
```

### 步骤 4：上传测试文件
创建一个测试 Java 文件 `test.java`：
```java
public class Test {
    public static void main(String[] args) {
        String password = "123456"; // 硬编码密码
        System.out.println(password);
    }
}
```

### 步骤 5：通过网页上传
1. 在主页选择文件
2. 输入用户 ID（默认 1）
3. 点击"开始 AI 审查"
4. 等待结果显示

---

## 🔍 常见问题排查

### 问题 1：无法访问 http://localhost:8080
**可能原因：**
- 项目未启动
- 端口被占用
- 防火墙阻止

**解决方案：**
```powershell
# 检查端口占用
netstat -ano | findstr :8080

# 如果被占用，修改 application.yml 添加：
server:
  port: 8081
```

### 问题 2：上传文件报错 500
**可能原因：**
- 数据库未配置
- AI API Key 未配置
- 数据库表未创建

**解决方案：**
1. 检查 `application.yml` 配置
2. 执行 `database/schema.sql`
3. 查看控制台错误日志

### 问题 3：AI 分析失败
**可能原因：**
- API Key 无效
- 网络连接问题
- API 配额用尽

**解决方案：**
1. 验证 API Key 是否正确
2. 检查网络连接
3. 查看 DeepSeek 控制台配额

---

## 📊 测试用例

### 测试用例 1：正常上传
```bash
curl -X POST http://localhost:8080/api/file/upload \
  -F "userId=1" \
  -F "file=@test.java"
```
**预期结果：** 返回 200，包含 requestId 和审查结果

### 测试用例 2：获取结果
```bash
curl http://localhost:8080/api/file/result/req-12345
```
**预期结果：** 返回 200，包含完整审查信息

### 测试用例 3：无效请求 ID
```bash
curl http://localhost:8080/api/file/result/invalid-id
```
**预期结果：** 返回 404 Not Found

### 测试用例 4：大文件上传
```bash
# 上传 10MB 文件（配置的最大值）
curl -X POST http://localhost:8080/api/file/upload \
  -F "userId=1" \
  -F "file=@large-file.java"
```
**预期结果：** 返回 200 或 413（超过限制）

---

## 🎯 推荐测试工具

### 1. 浏览器（最简单）
直接访问 `http://localhost:8080` 使用网页界面

### 2. Postman
1. 创建新请求
2. 选择 POST 方法
3. URL: `http://localhost:8080/api/file/upload`
4. Body → form-data
5. 添加 `userId` 和 `file` 字段

### 3. curl（命令行）
适合自动化测试和脚本

### 4. IntelliJ IDEA HTTP Client
创建 `test.http` 文件：
```http
### 上传文件
POST http://localhost:8080/api/file/upload
Content-Type: multipart/form-data; boundary=WebAppBoundary

--WebAppBoundary
Content-Disposition: form-data; name="userId"

1
--WebAppBoundary
Content-Disposition: form-data; name="file"; filename="test.java"
Content-Type: text/plain

< ./test.java
--WebAppBoundary--

### 获取结果
GET http://localhost:8080/api/file/result/req-12345
```

---

## 📝 注意事项

1. **首次运行前必须：**
   - ✅ 刷新 Maven 依赖
   - ✅ 配置数据库密码
   - ✅ 配置 AI API Key
   - ✅ 执行数据库脚本

2. **支持的文件类型：**
   - Java (.java)
   - Python (.py)
   - JavaScript (.js)
   - C/C++ (.c, .cpp)
   - Go (.go)
   - 文本文件 (.txt)

3. **文件大小限制：**
   - 最大 10MB（可在 application.yml 修改）

4. **数据库要求：**
   - MySQL 5.7+ 或 8.0+
   - 字符集：utf8mb4

---

**祝测试顺利！** 🚀
