# 基于 Spring Boot + Vue.js 的个人博客系统

> 毕业设计项目 · 前后端分离 · 现代化UI设计 · 响应式布局 · AI智能功能

---

## 项目介绍

本系统是一套完整的**个人博客平台**，采用前后端分离的 B/S 架构开发，具备现代化的UI设计和良好的用户体验。系统支持文章发布、评论互动、用户管理、AI智能评论审核等功能，采用响应式设计，支持PC和移动端访问。

### 核心亮点

- **现代化UI设计**：采用简洁现代的界面风格，支持深色/浅色主题切换
- **响应式布局**：完美适配PC、平板、手机等不同设备
- **前后端分离**：Spring Boot后端 + Vue.js前端，架构清晰
- **AI智能审核**：集成AI模型进行评论智能审核
- **完整功能闭环**：从内容创作到社区互动的完整流程

### 功能模块

#### 用户端功能
| 功能 | 说明 |
|------|------|
| 用户注册/登录 | JWT无状态认证，密码加密存储 |
| 文章浏览 | 文章列表、分类筛选、搜索功能 |
| 文章详情 | 富文本内容渲染、点赞功能 |
| 评论系统 | 发表评论、评论列表展示 |
| 文章创作 | 富文本编辑器，支持图片上传 |
| 个人中心 | 个人信息管理、我的文章管理 |
| 主题切换 | 深色/浅色主题一键切换 |

#### 管理员端功能
| 功能 | 说明 |
|------|------|
| 用户管理 | 用户列表、权限管理 |
| 文章管理 | 文章审核、编辑、删除 |
| 评论管理 | 评论审核、管理 |
| 数据统计 | 系统运行数据概览 |
| AI智能审核 | 自动审核评论内容 |

### 技术栈

#### 前端技术栈
- **框架**: Vue 3 + Composition API
- **构建工具**: Vite 5.2.6
- **UI组件库**: Element Plus 2.6.2
- **状态管理**: Pinia 2.1.7
- **路由**: Vue Router 4.3.0
- **HTTP客户端**: Axios 1.6.8
- **富文本编辑器**: wangEditor 5.1.23
- **代码高亮**: highlight.js 11.9.0
- **日期处理**: dayjs 1.11.10
- **样式**: CSS3 + CSS变量（主题系统）
- **其他**: NProgress、dompurify

#### 后端技术栈
- **框架**: Spring Boot 3.2.3
- **语言**: Java 17
- **数据持久化**: Spring Data JPA
- **安全认证**: Spring Security + JWT
- **数据库**: H2（默认，零配置）/ MySQL（可选）
- **AI集成**: 深度求索AI模型集成（可选）
- **构建工具**: Maven

---

## 项目特色

### 1. 现代化UI设计
- 采用Inter字体，提升阅读体验
- 简洁的卡片式布局设计
- 平滑的动画过渡效果
- 统一的色彩系统设计

### 2. 响应式设计
- 完美适配不同屏幕尺寸
- 移动端优化的交互体验
- 灵活的布局适配

### 3. 主题系统
- 完整的深色/浅色主题支持
- CSS变量驱动的主题切换
- 平滑的主题过渡动画

### 4. 技术架构优势
- 前后端完全分离，便于维护
- RESTful API设计规范
- 组件化开发模式
- 模块化代码组织

---

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- Maven 3.6+

### 方式一：H2 零配置启动（推荐，开箱即用）

系统默认使用 H2 内存数据库，无需安装任何数据库，启动即可运行：

```bash
# 后端启动（H2 模式，零配置）
cd blog-backend
mvn spring-boot:run
```

后端启动后，H2 控制台可通过 http://localhost:8081/h2-console 访问（JDBC URL: `jdbc:h2:mem:blogdb`）。

### 方式二：MySQL 启动（可选）

如需使用 MySQL 持久化数据：

1. 创建数据库：`blogdb`（注意：应用会自动建表，数据库名与 `application-mysql.yml` 中一致）
2. 复制根目录 `.env.example` 为 `.env.local`，填写数据库账号密码：

```bash
cp .env.example .env.local
# 编辑 .env.local，填写 DB_USERNAME 和 DB_PASSWORD
```

3. 使用 MySQL profile 启动：

```bash
cd blog-backend
SPRING_PROFILES_ACTIVE=mysql mvn spring-boot:run
```

### 配置 AI 功能（可选）

AI 功能（评论智能审核、文章 AI 总结）默认关闭。如需启用：

1. 在 `.env.local` 中填写 AI 配置：

```bash
# 编辑 .env.local，填写 AI_API_KEY
```

或直接通过环境变量启动：

```bash
AI_API_KEY=your_key_here mvn spring-boot:run
```

### 前端启动

```bash
cd blog-frontend
cp .env.example .env.local
# 编辑 .env.local，确认 VITE_API_BASE_URL=http://localhost:8081
npm install
npm run dev
```

---

## 访问地址

- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:8081
- **H2控制台**: http://localhost:8081/h2-console（仅 H2 模式）
- **默认管理员账号**: admin / admin123
- **AI功能**: 需配置 API Key 后启用（评论智能审核、文章AI总结）

---

## 项目结构

```
blog/
├── blog-backend/                 # Spring Boot后端
│   ├── src/main/java/com/blog/
│   │   ├── ai/                  # AI服务模块
│   │   ├── config/              # 配置类
│   │   ├── controller/          # 控制器
│   │   ├── dto/                 # 数据传输对象
│   │   ├── entity/              # 实体类
│   │   ├── exception/           # 异常处理
│   │   ├── repository/          # 数据访问层
│   │   ├── security/            # 安全配置
│   │   └── service/             # 业务逻辑层
│   ├── src/main/resources/      # 配置文件
│   ├── files/                   # 文件存储目录
│   └── pom.xml                  # Maven配置
├── blog-frontend/               # Vue.js前端
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 公共组件
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   ├── utils/             # 工具函数
│   │   └── views/             # 页面组件
│   ├── vite.config.js         # Vite配置
│   └── package.json           # 依赖配置
├── ai接入文档.txt              # AI接入参考文档（含敏感信息，已gitignore）
└── README.md                  # 项目说明
```

---

## 开发特色

### 前端开发特色
1. **组件化设计**: 采用Vue 3 Composition API，代码结构清晰
2. **响应式布局**: 使用CSS Grid和Flexbox实现自适应布局
3. **主题系统**: 基于CSS变量的主题切换机制
4. **富文本编辑**: 集成wangEditor富文本编辑器，支持图片上传
5. **性能优化**: Vite构建工具，快速热重载

### 后端开发特色
1. **RESTful API**: 标准的REST接口设计
2. **安全认证**: JWT令牌认证机制
3. **数据验证**: 完整的数据验证和异常处理
4. **分层架构**: 清晰的分层架构设计
5. **AI集成**: 集成深度求索AI模型，实现智能评论审核
6. **文件上传**: 支持图片等文件上传功能

---

## 部署说明

### 开发环境
- 数据库: H2（默认）或 MySQL 8.0+（可选）
- 端口: 前端3000，后端8081
- AI服务: 可选，需自行配置 API Key

### 生产环境
- 数据库: MySQL 8.0+
- 配置: 修改 `.env.local` 中的数据库连接信息或 `application-mysql.yml`
- AI服务: 可配置 AI 模型 API Key 或使用本地模型

---

## 技术亮点

1. **现代化技术栈**: 采用最新的Spring Boot 3和Vue 3框架
2. **良好的开发体验**: 前后端分离，开发效率高
3. **优秀的用户体验**: 响应式设计，主题切换
4. **代码质量**: 规范的代码结构和注释

---

## 联系方式

如有问题或建议，欢迎联系项目开发者。

---

> 开发环境：JDK 17 · Spring Boot 3.2.3 · Vue 3 · Vite 5 · Element Plus