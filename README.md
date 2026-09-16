# 智能外卖平台

一个面向餐厅的外卖管理平台，包含**管理端后台**与**用户端下单**两套业务，并集成了基于 **LangChain4j + 通义千问** 的 **AI 数据查询助手**（支持自然语言查询菜品、分类、订单）。

---

## 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [AI 助手](#ai-助手)
- [接口文档](#接口文档)
- [项目来源](#项目来源)

---

## 功能特性

### 管理端

| 模块 | 说明 |
|---|---|
| 员工管理 | 员工登录、新增/编辑、启用禁用、分页查询 |
| 分类管理 | 菜品分类与套餐分类的增删改查、启用禁用 |
| 菜品管理 | 菜品增删改查、口味配置、图片上传、起售停售 |
| 套餐管理 | 套餐增删改查、关联菜品、起售停售 |
| 订单管理 | 订单搜索、接单/拒单、派送/完成、各状态订单统计 |
| 数据统计 | 工作台、营业额统计、用户统计、订单统计、销量排名 Top10 |
| 报表导出 | 运营数据报表导出 Excel |
| 店铺管理 | 营业状态设置（营业中 / 打烊中） |
| **AI 助手** | **自然语言查询菜品、分类、订单** |

### 用户端

- 微信登录、浏览分类 / 菜品 / 套餐
- 购物车、收货地址管理
- 下单、微信支付、取消订单、再来一单
- 历史订单查询、订单详情

### 平台能力

- **WebSocket 来单提醒**：新订单实时推送到管理端
- **定时任务**：每分钟处理超时未支付订单，每日凌晨处理派送中订单
- **微信支付**：支付与退款回调处理
- **阿里云 OSS**：菜品图片云存储
- **百度地图**：商家端配送距离检测

---

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 3.5.0 | 基础框架（Jakarta EE） |
| Java | 17+ | 项目以 `release 17` 编译 |
| MyBatis | 3.0.3 | 持久层框架 |
| PageHelper | 1.4.7 | 分页插件 |
| MySQL | 8.x | 数据库（`mysql-connector-j` 8.0.33） |
| Druid | 1.2.28 | 数据库连接池（`druid-spring-boot-3-starter`） |
| Redis | — | 缓存 / 会话记忆 |
| Knife4j | 4.5.0 | 接口文档（OpenAPI 3 / springdoc） |
| LangChain4j | 1.0.1-beta6 | AI 应用框架（Function Calling、流式输出） |
| Apache POI | 3.16 | Excel 报表导出 |
| JJWT | 0.9.1 | JWT 令牌 |
| WebSocket | — | 来单提醒 |

### 前端

- Vue 2 + Element UI（编译产物）
- Nginx 托管静态资源并反向代理后端接口

---

## 项目结构

```
sky-take-out
├── sky-common/          通用模块：常量、上下文、枚举、异常、工具类、统一返回结果
├── sky-pojo/            实体模块：Entity、DTO、VO
├── sky-server/          服务模块：Controller、Service、Mapper、Config、Task
│   └── src/main/java/com/sky/
│       ├── controller/
│       │   ├── admin/       管理端接口
│       │   ├── user/        用户端接口
│       │   └── notify/      支付回调
│       ├── service/         业务逻辑
│       ├── mapper/          数据访问
│       ├── config/          Spring 配置
│       ├── task/            定时任务
│       ├── websocket/       WebSocket 服务
│       └── aiservice/       AI 助手（AiService 接口 + Tools 工具）
└── pom.xml              父 POM，统一依赖版本管理
```

---

## 环境要求

| 组件 | 版本要求 |
|---|---|
| JDK | 17 及以上 |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Redis | 5.0+ |
| Nginx | 1.20+（前端部署，可选） |

---

## 快速开始

### 1. 准备数据库

创建名为 `sky_take_out` 的数据库，导入项目所需的表结构与基础数据（分类、菜品、员工等）。

默认管理员账号：`admin` / `123456`（取决于数据库中的员工数据）。

### 2. 修改配置

编辑 `sky-server/src/main/resources/application-dev.yml`：

```yaml
sky:
  datasource:
    host: localhost
    port: 3306
    database: sky_take_out
    username: root
    password: 你的密码
  redis:
    host: localhost
    port: 6379
    password: 你的Redis密码
    database: 10
```

### 3. 配置 AI 密钥

AI 助手的 API Key 通过**环境变量**注入，不写在配置文件里。启动前需设置：

```bash
# Windows (PowerShell)
$env:QW-API-KEY="你的阿里云百炼API-Key"

# Windows (CMD)
set QW-API-KEY=你的API-Key

# Linux / macOS
export QW-API-KEY=你的API-Key
```

> 在 IDEA 中运行：`Run/Debug Configurations` → `Environment variables` 填入 `QW-API-KEY=sk-xxxxxx`

### 4. 启动后端

```bash
mvn clean package -DskipTests
java -jar sky-server/target/sky-server-1.0-SNAPSHOT.jar
```

或在 IDEA 中直接运行 `com.sky.SkyApplication`。

启动成功后监听 `8080` 端口，控制台输出 `server started`。

### 5. 部署前端

前端为编译后的静态资源，用 Nginx 托管：

```nginx
server {
    listen 80;
    server_name localhost;

    # 静态资源
    location / {
        root   html/sky;
        index  index.html;
    }

    # 管理端接口：/api/xxx -> 后端 /admin/xxx
    location /api/ {
        proxy_pass http://localhost:8080/admin/;
    }

    # 用户端接口
    location /user/ {
        proxy_pass http://127.0.0.1:8080/user/;
    }

    # WebSocket 来单提醒
    location /ws/ {
        proxy_pass http://127.0.0.1:8080/ws/;
        proxy_http_version 1.1;
        proxy_read_timeout 3600s;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

启动 Nginx 后访问 `http://localhost` 即可。

> **注意**：Nginx 的 `/api/xxx` 会转发到后端 `/admin/xxx`，因此后端管理端接口路径都带 `/admin` 前缀。

---

## 配置说明

### 对外部服务的依赖

以下服务在 `application-dev.yml` 中配置，**不配置不影响核心业务启动**，但对应功能不可用：

| 配置项 | 用途 | 是否必需 |
|---|---|---|
| `sky.alioss.*` | 菜品图片上传到阿里云 OSS | 上传图片时需要 |
| `sky.wechat.*` | 微信支付（商户号、证书、回调地址） | 支付功能需要 |
| `sky.baidu.ak` | 百度地图距离计算 | 配送距离检测需要 |
| `QW-API-KEY` | 通义千问 API Key | **AI 助手需要** |

### Redis 说明

项目使用**两个 Redis 实例**：

| 端口 | 用途 | 配置位置 |
|---|---|---|
| 6379 | 业务缓存、AI 会话记忆 | `sky.redis.*` |
| 6380 | LangChain4j 向量库（当前未启用） | `langchain4j.community.redis.*` |

> Spring Boot 3 中 Redis 配置前缀为 `spring.data.redis.*`（不再是 `spring.redis.*`）。

---

## AI 助手

### 实现方式

基于 **LangChain4j Function Calling**：大模型识别用户意图后，**主动调用后端工具方法查询真实数据库**，再用自然语言组织答案。不是简单的提示词问答，也不会编造数据。

```
用户提问
  → 大模型识别意图
  → 调用 @Tool 方法（查真实数据库）
  → 模型根据查询结果组织自然语言回答
  → 流式返回前端
```

### 查询工具

| 工具类 | 方法 | 查询维度 |
|---|---|---|
| `DishTools` | `queryDish` | 菜品名称、分类名称、售卖状态 |
| `CategoryTools` | `queryCategory` | 分类类型（菜品分类 / 套餐分类） |
| `OrderTools` | `queryOrder` | 订单号、手机号、订单状态、下单时间范围 |

### 关键文件

| 文件 | 作用 |
|---|---|
| `aiservice/ConsultantService.java` | AI 服务接口，`@AiService` 声明式装配 |
| `aiservice/tools/*.java` | `@Tool` 工具方法，对接现有 Service |
| `repository/RedisChatMemoryStore.java` | 会话记忆存 Redis，支持多轮对话 |
| `config/CommonConfig.java` | 对话记忆、向量检索等 Bean |
| `resources/system.txt` | 系统提示词（角色、规则、行为约束） |
| `controller/admin/ChatController.java` | 对话接口 `GET /admin/chat` |

### 对话接口

```http
GET /admin/chat?memoryId={会话ID}&memory={用户问题}
Header: token: {JWT令牌}
```

- `memoryId`：会话标识，后端据此区分不同会话的上下文
- 返回：`text/plain` 流式输出

### 行为约束

提示词（`system.txt`）中已约束模型：

- 必须先调用工具查询，**禁止编造**数据
- 查不到就如实说明，不臆造结果
- 信息不足时**主动追问**（如只说"查订单"未给订单号）
- 超出业务范围（闲聊、写代码）礼貌拒绝
- **只读不写**，不执行增删改操作

### 前端入口

管理端登录后，左侧菜单底部「**AI 助手**」，聊天页通过 iframe 嵌入，支持流式打字效果、多轮对话、新建会话。

---

## 接口文档

项目集成 Knife4j，启动后访问：

```
http://localhost:8080/doc.html
```

---

## 项目来源

本项目基于黑马程序员《苍穹外卖》教学项目进行开发与重构，主要改动包括：

- 从 Spring Boot 2.7 升级至 **Spring Boot 3.5**（Jakarta EE 迁移、Druid/Knife4j 等依赖适配）
- 集成 **LangChain4j + 通义千问**，实现 AI 数据查询助手
- 前端品牌与界面调整
