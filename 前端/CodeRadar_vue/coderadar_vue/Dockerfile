# 构建阶段
FROM node:20-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制 package.json
COPY package.json ./

# 安装依赖（不复制 package-lock.json，让 Docker 重新生成）
RUN npm install

# 复制项目源代码
COPY . .

# 设置环境变量
ENV NODE_ENV=production

# 构建项目
RUN npm run build

# 生产阶段
FROM nginx:alpine

# 复制构建产物到 nginx 容器
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 nginx 配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动 nginx
CMD ["nginx", "-g", "daemon off;"]

