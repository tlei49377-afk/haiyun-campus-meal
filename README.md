# 海韵食刻 · 校园食堂预约取餐平台

> 学生按「食堂 → 档口 → 菜品 → 取餐时段」预约下单，档口按序备餐，学生凭 6 位取餐码到店核销。

本项目是 Java 后端主导、Vue 3 前端 AI 辅助完成的全栈练习项目。核心目标不是堆叠中间件，而是把“预约取餐”这条业务链路做完整：时段容量原子占位、订单状态机、支付回调幂等、超时释放、档口出餐和取餐核销。

## 项目演进说明

项目最初用于 Java Web 综合训练，参考了公开教学项目的业务框架。2026 年起，我在原有结构上逐步完成了 RBAC、缓存三防护、订单状态机、CAS 定时任务、支付回调幂等、traceId、慢 SQL 治理和 AI 工具调用等改造。

当前版本进一步将“外卖配送”重构为“校园食堂预约取餐”，并重建了 Vue 3 管理端与学生 H5。Git 历史保留了真实的学习曲线；README 不把教学阶段写成“从 0 到 1 自研”。前端由 AI 辅助生成初稿，本人负责接口契约、目录结构、请求层、权限路由、联调、异常态和 Nginx 部署。

## 功能

### 学生端 H5

- 学号 + 密码登录
- 浏览食堂、档口、分类和菜品
- 加入购物车；购物车按档口隔离
- 选择当天取餐时段并提交订单
- 模拟支付
- 查看订单和 6 位取餐码
- 取消未支付/待接单订单

### 档口运营端

- 管理员登录与 RBAC 权限码
- 食堂、档口、取餐时段管理
- 订单条件查询、接单、出餐、取消
- 取餐码核销与重复核销幂等
- AI 助手查询真实业务数据

## 技术栈

| 层次 | 技术 |
|---|---|
| 后端 | Spring Boot 3.5、MyBatis、MySQL 8、Redis |
| 权限 | JWT、自定义 `@RequirePermission`、AOP、RBAC |
| 并发与一致性 | 条件更新、订单状态 CAS、唯一索引、支付流水幂等 |
| 可观测性 | traceId、Actuator、Micrometer、Prometheus |
| AI | LangChain4j + 通义千问工具调用 |
| 前端 | Vue 3、Vite、TypeScript、Element Plus、Pinia、Vue Router |
| 部署 | Nginx 静态托管与反向代理 |

## 模块结构

```text
haiyun-meal/
├─ haiyun-common/   常量、异常、枚举、工具、JWT、配置属性
├─ haiyun-model/    Entity、DTO、VO
├─ haiyun-app/      Controller、Service、Mapper、Config、Task
├─ db/              完整建库脚本、迁移脚本、演示数据
├─ docs/            改造方案与接口/地址文档（公开前筛选）
└─ pom.xml
```

## 三条核心链路

### 1. 时段占位

```sql
UPDATE pickup_slot
SET booked = booked + 1
WHERE id = ?
  AND status = 1
  AND booked < capacity;
```

占位和订单落库在同一个事务内；影响行数为 0 表示已约满或已关闭。取消、拒单和超时关闭都通过订单状态 CAS 成功后再释放库存，确保库存只释放一次。

### 2. 订单状态机

```text
10 待支付 → 20 待接单 → 30 制作中 → 40 待取餐 → 50 已取餐
                                      ↘ 60 已取消
20/30 → 70 已退款
```

支付回调使用 `payment_callback_log` 唯一键去重，订单状态使用 `WHERE id = ? AND status = ?` 条件更新，避免支付、超时取消和人工取消互相覆盖。

### 3. 权限与可观测性

登录返回权限码列表，后端通过 `@RequirePermission` + AOP 二次校验；前端按权限码过滤菜单。每个请求生成或透传 `X-Trace-Id`，同时写入响应头、响应体和日志 MDC。

## 快速开始

### 1. 准备数据库

```powershell
mysql -uroot -p -e "source db/haiyun_meal.sql"
```

或者在已有基础库上依次执行：

```powershell
mysql -uroot -p -e "source db/migration/V7__canteen_pickup.sql"
mysql -uroot -p -e "source db/seed_haiyun.sql"
```

### 2. 准备配置

```powershell
Copy-Item haiyun-app\src\main\resources\application-dev.yml.example haiyun-app\src\main\resources\application-dev.yml
```

按本机环境填写数据库、Redis、JWT、OSS、微信和 AI 配置。真实配置已被 `.gitignore` 排除，不要提交。

### 3. 启动后端

```powershell
mvn clean test
mvn -q -DskipTests install
mvn -q -pl haiyun-app spring-boot:run
```

默认地址：`http://localhost:8080`。接口文档：`http://localhost:8080/doc.html`。

### 4. 启动前端

```powershell
cd E:\Java\java_code\sky-take-out-frontend
npm install
npm run dev
```

默认地址：`http://localhost:5173`。

## 演示账号

| 端 | 账号 | 密码 | 说明 |
|---|---|---|---|
| 管理端 | `admin` | `123456` | 历史 MD5 密码首次登录后自动升级为 BCrypt |
| 学生端 | `20230001` | `123456` | 演示学生账号 |

## 关键接口

| 方法 | 地址 | 说明 |
|---|---|---|
| POST | `/api/admin/employee/login` | 管理端登录 |
| GET | `/api/admin/canteen/list` | 食堂列表 |
| GET | `/api/admin/stall/list` | 档口列表 |
| POST | `/api/admin/slot/generate` | 批量生成取餐时段 |
| GET | `/api/admin/order/conditionSearch` | 订单分页查询 |
| PUT | `/api/admin/order/confirm` | 接单 |
| PUT | `/api/admin/order/ready/{id}` | 出餐 |
| POST | `/api/admin/order/pickup/verify` | 取餐核销 |
| POST | `/api/app/user/login` | 学生登录 |
| GET | `/api/app/canteen/list` | 学生端食堂列表 |
| POST | `/api/app/order/submit` | 提交订单并占位 |
| PUT | `/api/app/order/payment` | 模拟支付 |

完整地址见 [docs/改造文件与地址文档.md](docs/改造文件与地址文档.md)。

## 测试与证据

- 订单状态机单元测试：`haiyun-app/src/test/java/com/haiyun/meal/enumeration/OrderStatusTest.java`
- 缓存测试：`haiyun-app/src/test/java/com/haiyun/meal/cache/CacheHelperTest.java`
- traceId 测试：`haiyun-app/src/test/java/com/haiyun/meal/filter/TraceIdFilterTest.java`

并发压测脚本和数据库前后对比结果应在本地生成并保留；README 不写无法复现的性能数字。

## 当前边界

- 默认使用模拟支付，真实微信支付只保留适配器入口；
- RAG 不是主链路必选项，当前重点是工具调用；
- 生产部署前仍需完成外部密钥轮换、Git 历史敏感文件清理和服务器权限配置；
- 项目来源和内部改造文档不随公开仓库发布。公开前请先审查 `docs/`。
