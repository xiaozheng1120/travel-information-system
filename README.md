# 旅游信息系统

基于 Spring Boot + Vue 的旅游信息管理系统，包含景点、攻略、住宿、门票、订单、收藏和后台管理等功能。

## 技术栈

- 后端：Spring Boot、MyBatis、MySQL
- 前端：Vue 3、Vite、Element Plus

## 功能模块

- 用户注册登录、个人中心、收藏
- 景点、攻略、住宿浏览与详情
- 门票预订、订单管理
- 后台用户、景点、攻略、住宿、订单、轮播图管理

## 目录结构

- `backend`：后端源码
- `frontend`：前端源码
- `database/travel_db.sql`：数据库脚本

## 本地运行

### 1. 导入数据库

创建 `travel_db` 数据库，导入 `database/travel_db.sql`。

### 2. 启动后端

修改 `backend/src/main/resources/application.yml` 中的数据库账号和密码，然后：

```bash
cd backend
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`

默认管理员账号：`admin`
默认密码：`123456`
