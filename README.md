# Account Book

多账本记账应用，后端 Spring Boot 3.5 + MyBatis-Plus + H2，前端 Vue 3 + Vite + Element Plus + Pinia。

## 目录

- `backend/` 后端 REST API
- `frontend/` 前端单页应用

## 启动后端

```powershell
cd backend
mvn -Dmaven.repo.local=C:\Users\mjf\.m2\repository spring-boot:run
```

- API 健康检查：`http://localhost:8080/api/health`
- H2 控制台：`http://localhost:8080/h2-console`
- JDBC URL：`jdbc:h2:file:./data/account-book`
- 用户名：`sa`

## 启动前端

```powershell
cd frontend
npm install
npm run dev
```

前端开发服务默认：`http://localhost:5173`，并通过 Vite 代理访问后端 `/api`。

## MVP 功能

1. 创建/查看/删除/设置默认账本
2. 创建账本时初始化收入/支出默认分类
3. 分类管理
4. 记一笔收入或支出
5. 账目列表、筛选、删除
6. 月度统计、分类统计、趋势图
