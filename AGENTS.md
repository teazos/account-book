# AGENTS.md

本文件用于指导 Codex / AI Agent 在本仓库中协作开发。请在修改代码前先阅读本文件，并尽量遵守约定。

## 项目概览

这是一个多账本记账应用：

- 后端：Spring Boot 3.5、MyBatis-Plus、H2 内置数据库、单体 REST API
- 前端：Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios、ECharts
- 业务核心：所有分类、账目、统计都必须归属于某个账本 `bookId`

目录结构：

```text
account-book/
  backend/     Spring Boot 后端
  frontend/    Vue 3 前端
  README.md    启动说明
```

## 运行与检查命令

### 后端

```powershell
cd backend
mvn "-Dmaven.repo.local=C:\Users\mjf\.m2\repository" spring-boot:run
```

构建检查：

```powershell
cd backend
mvn "-Dmaven.repo.local=C:\Users\mjf\.m2\repository" -DskipTests clean package
```

后端默认端口：`8080`

- 健康检查：`GET /api/health`
- H2 Console：`/h2-console`
- JDBC URL：`jdbc:h2:file:./data/account-book;MODE=MySQL;DATABASE_TO_LOWER=TRUE`（H2 运行在 MySQL 兼容模式）
- 用户名：`sa`
- 密码：空
- 仓库当前**没有单元/集成测试**；后端改动用上面的 `clean package` 验证，必要时配合 `spring-boot:run` 手动验证

### 前端

使用 pnpm（仓库根目录有 `pnpm-lock.yaml` / `pnpm-workspace.yaml`，**不要用 npm**）：

```powershell
cd frontend
pnpm install
pnpm dev
```

前端默认端口：`5173`，通过 Vite proxy 将 `/api` 转发到 `http://localhost:8080`。

前端类型检查 + 构建（无单独 lint/typecheck 脚本，`build` 即类型检查）：

```powershell
pnpm build   # 实际执行 vue-tsc -b && vite build
```

> 当前环境中 npm/pnpm 可能不可用；如需安装或构建前端，请先确认本机 Node 配置。

## 后端开发约定

### 分包规范

按业务模块分包：

```text
com.example.accountbook
  common/       通用返回、异常、枚举、分页
  config/       Spring/MyBatis-Plus 配置、鉴权拦截器
  user/         用户与鉴权模块（注册/登录）
  book/         账本模块
  category/     分类模块
  transaction/  账目模块
  statistics/   统计模块
```

每个模块优先使用：

```text
controller/
service/
mapper/
entity/
dto/
```

### API 返回结构

所有接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

业务错误通过 `BizException` 抛出，由 `GlobalExceptionHandler` 统一转换为失败响应。

### 账本边界规则

开发任何分类、账目、统计相关功能时，必须遵守：

1. 新增分类必须带 `bookId`
2. 新增账目必须带 `bookId` 和 `categoryId`
3. `categoryId` 必须属于当前 `bookId`
4. 账目 `type` 必须与分类 `type` 一致
5. 统计查询必须基于 `bookId`
6. 不要跨账本查询或修改数据

### 用户与鉴权

- 除 `/api/auth/**` 与 `/api/health` 外，所有 `/api/**` 接口都需 `Authorization: Bearer <token>` 请求头，由 `config/AuthInterceptor` 校验，失败返回 HTTP 401
- 注册/登录返回 `{ token, user }`，token 为 32 位 UUID（存储在 `account_user.token`，登录会刷新 token）
- 密码使用 SHA-256（16 字节随机盐，Hex 编码），不要存明文
- 当前登录用户通过 `UserContext.requireUserId()` 获取（`common/UserContext`，ThreadLocal）
- **数据归属规则**：账本属于用户（`account_book.user_id`）。所有按 `bookId` 操作的 service 都必须先校验该账本属于当前用户（`ensureBook` 中做归属校验），否则抛 `BizException("无权访问该账本")`；列表查询按 `user_id` 过滤
- 邮箱统一转为小写存储，注册时保证唯一

### 数据库约定

- H2 schema 文件位于 `backend/src/main/resources/db/schema.sql`
- H2 是文件数据库且 `spring.sql.init.mode: always`：改表结构时，新列必须用 `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`（`CREATE TABLE IF NOT EXISTS` 不会改动已存在的表）
- 使用逻辑删除字段：`deleted BOOLEAN DEFAULT FALSE`
- 金额使用 `DECIMAL(18,2)` / Java `BigDecimal`
- 日期：发生日期使用 `LocalDate`，创建/更新时间使用 `LocalDateTime`

### MyBatis-Plus 约定

- 实体使用 `@TableName`
- 主键使用 `@TableId(type = IdType.ASSIGN_ID)`
- 逻辑删除字段使用 `@TableLogic`
- 查询优先使用 `LambdaQueryWrapper`
- 分页查询使用 MyBatis-Plus 分页插件

### 文件上传

- 账本封面上传：`POST /api/books/cover`（multipart，仅图片、≤5MB），返回 `/uploads/<文件名>`，文件存 `app.upload-dir`（默认 `backend/uploads/`，已加入 `.gitignore`）
- 通过 `WebConfig` 的 `/uploads/**` 资源映射静态访问（不受鉴权拦截器影响）；前端 `vite.config.ts` 需把 `/uploads` 加入 proxy
- 更换/移除/删除封面时，`BookService` 会清理旧的上传文件（`FileStorageService.deleteIfUploaded`）
- 注意：`GlobalExceptionHandler` 的兜底 `Exception` 处理器会把缺失静态资源的 `NoResourceFoundException` 变成 500，需为其单独注册 404 处理器

## 前端开发约定

### 目录规范

```text
frontend/src/
  api/        Axios API 封装
  stores/     Pinia 状态
  types/      TypeScript 类型
  router/     Vue Router
  layouts/    页面布局
  views/      路由页面
  components/ 通用/业务组件
  styles/     全局样式
```

### 状态管理

- `authStore`：登录用户、token（存 localStorage：`account_book_token` / `account_book_user`）
- `bookStore`：账本列表、当前账本
- `categoryStore`：当前账本分类
- `transactionStore`：账目列表、分页、加载状态

当前账本是前端上下文核心，页面路由优先使用 `/books/:bookId/...`。

### API 约定

- API 基础路径使用 `/api`
- 所有后端响应由 `src/api/request.ts` 统一解包
- `request.ts` 请求拦截器自动附加 `Authorization: Bearer <token>`，收到 401 时清除登录态并跳转 `/login`
- 接口失败时通过 Element Plus `ElMessage` 提示
- 路由守卫：`/books/**` 需要登录，未登录跳转 `/login`

### UI 约定

优先使用 Element Plus 组件：

- 布局：`el-container`、`el-aside`、`el-header`、`el-main`
- 表单：`el-form`、`el-input`、`el-select`、`el-date-picker`、`el-input-number`
- 数据：`el-table`、`el-pagination`、`el-card`、`el-tag`
- 操作确认：`el-popconfirm`

图表使用 ECharts。

## 编码规范

- Java 源码使用 UTF-8 无 BOM
- TypeScript/Vue 文件使用 UTF-8
- 不要提交 `backend/target/`、`backend/data/`、`frontend/node_modules/`、`frontend/dist/`
- 优先保持小步提交、单一职责修改
- 修改接口时同步更新前端 `api/` 和 `types/`
- 修改数据库字段时同步更新：`schema.sql`、entity、DTO、前端类型

## 开发顺序参考

各模块均已实现基础增删改查；若需修改接口/数据库字段，按「后端 entity + DTO → schema.sql → 前端 api + types」的顺序联动更新。