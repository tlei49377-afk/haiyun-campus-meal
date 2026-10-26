# 海韵食刻 · 校园食堂预约取餐前端

海韵食刻的学生 H5 与管理端前端。学生按食堂 → 档口 → 菜品 → 取餐时段下单；档口运营人员接单、出餐并核销取餐码。

## 技术栈

- Vue 3 + Vite + TypeScript
- Element Plus
- Pinia + Vue Router
- Axios 统一请求层

## 本地启动

```powershell
npm install
npm run dev
```

Vite 默认运行在 `http://localhost:5173`，开发代理将 `/api` 和 `/ws` 转发到 `http://localhost:8080`。

## 构建与部署

```powershell
npm run build
```

将 `dist/` 交给 Nginx；参考 [conf/nginx.conf](conf/nginx.conf)。生产环境访问地址：

- 管理端入口：`http://localhost/admin/dashboard`
- 学生端入口：`http://localhost/student/home`
- 演示管理员：`admin / 123456`
- 演示学生：`20230001 / 123456`

## 页面

| 端 | 页面 | 地址 |
|---|---|---|
| 管理端 | 工作台 | `/admin/dashboard` |
| 管理端 | 食堂管理 | `/admin/canteens` |
| 管理端 | 档口管理 | `/admin/stalls` |
| 管理端 | 取餐时段 | `/admin/slots` |
| 管理端 | 订单管理 | `/admin/orders` |
| 管理端 | 取餐核销 | `/admin/verify` |
| 管理端 | AI 助手 | `/admin/ai` |
| 学生端 | 食堂首页 | `/student/home` |
| 学生端 | 档口菜单 | `/student/menu/:stallId` |
| 学生端 | 购物车 | `/student/cart` |
| 学生端 | 我的订单 | `/student/orders` |
| 学生端 | 取餐码 | `/student/pickup/:id` |

## 说明

前端由 AI 辅助生成初稿，本人负责接口契约、目录结构、请求层、权限路由、联调、异常态和 Nginx 部署。项目来源和演进说明见后端仓库 README。
