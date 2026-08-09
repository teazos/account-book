# Account Book（多账本记账应用）

一个带用户体系的多账本记账应用：注册登录后，可创建多个账本，在每个账本内管理分类、记录收入/支出账目，并查看月度统计、分类占比与趋势图表。已做手机端适配。

## 技术栈

| 端 | 技术 |
| --- | --- |
| 后端 | Spring Boot 3.5、MyBatis-Plus、H2（MySQL 兼容模式）、单体 REST API |
| 前端 | Vue 3、Vite、TypeScript、Element Plus、Pinia、Vue Router、Axios、ECharts |

## 功能特性

- **用户体系**：邮箱 + 密码注册/登录，Bearer Token 鉴权，数据按用户隔离（他人无法访问你的账本）
- **账本管理**：创建、编辑、删除、设为默认；封面本地图片上传（≤5MB，jpg/png/gif/webp/bmp）；封面作为卡片背景展示
- **分类管理**：创建账本时自动初始化默认收入/支出分类；分类增删改，同账本同类型下名称唯一
- **账目记账**：记收入/支出，金额、日期、备注；列表分页与按日期/类型筛选；删除
- **统计报表**：月度收入/支出/结余、支出分类占比（饼图）、每日趋势（折线图）、年度趋势
- **移动端适配**：≤768px 自动切换为汉堡菜单 + 抽屉导航，图表/表格/栅格自适应

## 目录结构

```text
account-book/
  backend/     Spring Boot 后端（com.example.accountbook）
    common/      统一返回、异常、分页、UserContext
    config/      鉴权拦截器、Jackson(ID转字符串)、MyBatis-Plus、文件存储、静态资源
    user/        用户与鉴权（注册/登录）
    book/        账本模块
    category/    分类模块
    transaction/ 账目模块
    statistics/  统计模块
  frontend/    Vue 3 单页应用
    src/api/       Axios API 封装
    src/stores/    Pinia 状态（auth/book/category/transaction）
    src/views/     Login/Register、账本列表、看板、账目、分类、统计
    src/components/ TransactionDrawer（记一笔抽屉）
  README.md    本文档
```

## 快速启动

### 后端（默认端口 8080）

```powershell
cd backend
mvn "-Dmaven.repo.local=C:\Users\mjf\.m2\repository" spring-boot:run
```

- 健康检查：`GET http://localhost:8080/api/health`
- H2 控制台：`http://localhost:8080/h2-console`（JDBC URL：`jdbc:h2:file:./data/account-book;MODE=MySQL;DATABASE_TO_LOWER=TRUE`，用户名 `sa`，密码空）
- 数据库文件：`backend/data/account-book.mv.db`（文件库，schema 由 `db/schema.sql` 每次启动自动执行，新列用 `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`）

### 前端（默认端口 5173，使用 pnpm，勿用 npm）

```powershell
cd frontend
pnpm install
pnpm dev
```

- Vite 代理将 `/api` 与 `/uploads` 转发到 `http://localhost:8080`
- 类型检查 + 构建：`pnpm build`（即 `vue-tsc -b && vite build`）
- 浏览器访问 `http://localhost:5173`，首次进入请先注册

## API 概览

除 `/api/auth/**` 与 `/api/health` 外，所有 `/api/**` 接口需携带请求头 `Authorization: Bearer <token>`。统一返回 `{ code, message, data }`，`code=0` 表示成功。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/register` | 注册 `{email, password, nickname?}`，返回 `{token, user}` |
| POST | `/api/auth/login` | 登录，返回 `{token, user}`（登录会刷新 token） |
| GET/POST | `/api/books` | 账本列表 / 新建账本 |
| PUT/DELETE | `/api/books/{bookId}` | 修改 / 删除账本 |
| PUT | `/api/books/{bookId}/default` | 设为默认账本 |
| POST | `/api/books/cover` | 上传封面（multipart，字段 `file`），返回 `/uploads/<文件名>` |
| GET/POST | `/api/books/{bookId}/categories` | 分类列表 / 新增分类 |
| PUT/DELETE | `/api/books/{bookId}/categories/{id}` | 修改 / 删除分类 |
| GET/POST | `/api/books/{bookId}/transactions` | 账目分页查询（支持 `startDate/endDate/type/categoryId/page/size`）/ 记账 |
| PUT/DELETE | `/api/books/{bookId}/transactions/{id}` | 修改 / 删除账目 |
| GET | `/api/books/{bookId}/statistics/monthly?month=YYYY-MM` | 月度收入/支出/结余 |
| GET | `/api/books/{bookId}/statistics/category?type=&startDate=&endDate=` | 分类汇总 |
| GET | `/api/books/{bookId}/statistics/daily-trend?startDate=&endDate=` | 每日趋势 |
| GET | `/api/books/{bookId}/statistics/yearly?year=` | 年度趋势 |

## 构建检查

```powershell
# 后端
cd backend
mvn "-Dmaven.repo.local=C:\Users\mjf\.m2\repository" -DskipTests clean package

# 前端（类型检查 + 构建）
cd frontend
pnpm build
```

仓库当前无单元/集成测试，后端改动以 `clean package` + 手动接口验证为准。

## 重要约定

- **ID 精度**：后端雪花 ID（Long）统一序列化为字符串（`JacksonConfig`），前端所有 ID 一律按 `string` 处理，禁止 `Number(id)`
- **数据隔离**：账本归属用户（`account_book.user_id`），所有按 `bookId` 的操作都会校验归属，跨用户访问返回「无权访问该账本」
- **账本边界**：分类/账目/统计都必须携带 `bookId`，`categoryId` 必须属于该账本，账目 `type` 必须与分类 `type` 一致
- **密码安全**：SHA-256 + 16 字节随机盐（Hex），不存明文；邮箱统一小写存储并保证唯一
