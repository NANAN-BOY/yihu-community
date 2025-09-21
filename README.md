<div align="center">

# 🤝 益护平台社区版

*一站式公益项目与活动管理平台*

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-4FC08D?logo=vue.js)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-Latest-409EFF?logo=element)](https://element-plus.org/)
[![Vuex](https://img.shields.io/badge/Vuex-4.x-4FC08D?logo=vue.js)](https://vuex.vuejs.org/)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.x-DC143C?logo=java)](https://mybatis.org/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539?logo=openapi-initiative)](https://www.openapis.org/)

[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0+-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![Aliyun OSS](https://img.shields.io/badge/Aliyun-OSS-FF6A00?logo=alibabacloud)](https://www.aliyun.com/product/oss)

[:us: English Version](README_en.md) •
[🚀 快速开始](#-快速开始) •
[📖 文档](#-文档) •
[🌟 特性](#-特性) •
[🛠️ 技术栈](#️-技术栈) •
[🤝 贡献](#-贡献)

---

</div>

## 📖 项目简介

益护平台社区版是一个专为中国民间公益组织设计的一站式项目与活动管理平台。平台支持多项目与多活动的深度管理，实现全流程数字化，无缝对接专家资源，打造高效公益生态。系统通过规范化管理项目流程，协助社会组织完善项目信息，并通过专家评审机制确保项目质量与规范性。

## 🌟 核心功能

### 🎯 项目管理
- 多项目并行管理，实时查看状态与进度
- 清晰分类，项目创建与切换
- 专家评审系统，结果状态一目了然
- 项目档案存储与检索

### 🎉 活动管理
- 项目内多活动创建与管理
- 活动名称、时间、地点与描述管理
- 活动通知系统
- 活动档案与细节图片上传
- 活动审核机制

### ✅ 签到统计系统
- 精准记录活动签到信息
- 支持在线签到与纸质签到表上传
- 自动汇总参与人数统计
- 签到数据实时保存

### 📰 新闻稿管理
- 支持图片和链接两种形式
- 图文混排新闻稿上传
- 外部媒体链接嵌入
- 一键发布，提升宣传效果

### 📝 满意度调查
- 内置问卷系统，支持多题型
- 预设满意度调查问卷
- 可视化分析报告（柱状图、饼图、折线图）
- 实时数据洞察

### 👨‍🏫 专家定制系统
- 内置一对一聊天与指导功能
- 实时沟通，支持文件与图片共享
- 项目设计与执行全程专业支持
- 实时消息通知

### 📸 档案管理
- 图片档案记录与分类存储
- 阿里云OSS对象存储集成
- 多媒体资源管理
- 快速检索与访问

### 🔐 权限控制
- **专家** - 项目评审与专业指导
- **系统管理员** - 平台管理与系统配置
- **社会组织** - 项目创建、活动管理与日常操作

## 🛠️ 技术架构

### 前端技术栈
- **框架**: Vue.js 3.x + Vite 3.0+
- **UI组件**: Element Plus (El组件库)
- **路由**: Vue Router
- **状态管理**: Vuex
- **构建工具**: Vite 3.0+
- **包管理**: npm 8.0+
- **样式**: CSS3 + 响应式设计

### 后端技术栈
- **框架**: Spring Boot 3.x
- **安全**: Spring Security
- **数据访问**: MyBatis
- **API文档**: OpenAPI 3.0
- **验证**: Hibernate Validator
- **构建工具**: Maven 3.6+

### 数据存储
- **主数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **文件存储**: 阿里云OSS对象存储

### 第三方服务
- **短信服务**: 阿里云SMS/腾讯云SMS
- **对象存储**: 阿里云OSS
- **消息推送**: WebSocket

## 🚀 快速开始

### 环境要求

**前端环境**
- Node.js 16.0+
- npm 8.0+
- Vue CLI 5.0+ 或 Vite 3.0+

**后端环境**
- JDK 17+ (必须)
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

**云服务配置**
- 阿里云OSS账号
- 短信服务账号（阿里云SMS/腾讯云SMS）

### 安装步骤

```bash
# 1. 克隆项目
git clone [项目仓库地址]
cd yihu-platform-community

# 2. 后端配置
cd backend
# 配置数据库连接
cp src/main/resources/application-example.yml src/main/resources/application.yml
# 编辑配置文件，设置数据库、Redis、OSS等配置

# 安装依赖并启动后端服务
mvn clean install
mvn spring-boot:run

# 3. 前端配置
cd ../frontend
# 安装依赖
npm install
# 或使用 yarn
yarn install

# 配置API接口地址
cp .env.example .env
# 编辑 .env 文件，配置后端API地址

# 启动前端开发服务器
npm run dev
# 或使用 yarn
yarn dev
```

### 配置说明

<details>
<summary>点击展开详细配置</summary>

1. **数据库配置** (`application.yml`)
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/yihu_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
       username: your_username
       password: your_password
       driver-class-name: com.mysql.cj.jdbc.Driver
   ```

2. **Redis配置** (`application.yml`)
   ```yaml
   spring:
     redis:
       host: localhost
       port: 6379
       password: your_redis_password
       database: 0
   ```

3. **阿里云OSS配置** (`application.yml`)
   ```yaml
   aliyun:
     oss:
       endpoint: https://oss-cn-hangzhou.aliyuncs.com
       accessKeyId: your_access_key_id
       accessKeySecret: your_access_key_secret
       bucketName: your_bucket_name
   ```

4. **短信服务配置** (`application.yml`)
   ```yaml
   sms:
     accessKeyId: your_sms_access_key
     accessKeySecret: your_sms_secret_key
     signName: 益护平台
     templateCode: SMS_123456789
   ```

5. **前端API配置** (`.env`)
   ```env
   VITE_API_BASE_URL=http://localhost:8080/api
   VITE_OSS_DOMAIN=https://your-bucket.oss-cn-hangzhou.aliyuncs.com
   ```

</details>

## 📂 项目结构

<details>
<summary>点击展开项目结构</summary>

```
yihu-platform-community/
├── backend/                 # Spring Boot后端
│   ├── src/main/java/
│   │   ├── controller/     # 控制器层
│   │   ├── service/        # 业务逻辑层
│   │   ├── mapper/         # 数据访问层
│   │   ├── entity/         # 实体类
│   │   ├── dto/            # 数据传输对象
│   │   ├── config/         # 配置类
│   │   └── utils/          # 工具类
│   ├── src/main/resources/
│   │   ├── mapper/         # MyBatis映射文件
│   │   ├── application.yml # 配置文件
│   │   └── db/migration/   # 数据库迁移脚本
│   └── pom.xml            # Maven配置
├── frontend/              # Vue.js前端
│   ├── src/
│   │   ├── components/    # 公共组件
│   │   ├── views/         # 页面组件
│   │   │   ├── project/   # 项目管理页面
│   │   │   ├── activity/  # 活动管理页面
│   │   │   ├── survey/    # 满意度调查页面
│   │   │   ├── news/      # 新闻稿管理页面
│   │   │   ├── chat/      # 专家沟通页面
│   │   │   └── archive/   # 档案管理页面
│   │   ├── api/           # API接口封装
│   │   ├── utils/         # 工具函数
│   │   ├── store/         # 状态管理
│   │   ├── router/        # 路由配置
│   │   └── assets/        # 静态资源
│   ├── public/            # 公共资源
│   ├── .env.example       # 环境变量示例
│   ├── package.json       # 依赖管理
│   └── vite.config.js     # Vite配置
├── docs/                  # 项目文档
│   ├── api.md            # API文档
│   ├── deployment.md     # 部署文档
│   └── user-manual.md    # 用户手册
├── sql/                  # 数据库脚本
│   ├── init.sql          # 初始化脚本
│   └── data.sql          # 测试数据
└── README.md             # 项目说明
```

</details>

## 🌟 系统特色

### 🌟 一站式管理体验
- 项目与活动深度集成，一个平台解决所有需求
- 从项目创建到活动执行，全流程数字化管理
- 响应式设计，支持PC端和移动端无缝切换

### 🔄 智能审核机制
- 项目审核与活动审核双重保障
- 专家评审系统，确保项目质量
- 系统管理员终审，规范性检查

### 💬 专家实时指导
- 内置聊天系统，支持文件与图片共享
- 一对一专业指导，项目全程跟踪
- 实时消息通知，不错过任何重要信息

### 📊 数据可视化分析
- 满意度调查结果自动生成图表
- 多维度数据统计，实时洞察项目效果
- 签到数据自动汇总，参与情况一目了然

### ☁️ 云端存储安全
- 阿里云OSS对象存储，数据安全可靠
- 图片档案自动分类，快速检索访问
- 支持大容量文件上传，满足各类需求

## 🗺️ 开发路线图

### 当前版本 (v1.0)
- [x] 多项目并行管理系统
- [x] 项目内多活动管理
- [x] 签到统计与纸质表上传
- [x] 图片档案记录与OSS存储
- [x] 新闻稿管理（图片+链接形式）
- [x] 内置满意度调查问卷系统
- [x] 专家一对一沟通系统
- [x] 三角色权限控制
- [x] 项目与活动双审核流程
- [x] 短信验证码登录
- [x] 响应式设计与移动端适配

### 下个版本 (v1.2)
- [ ] 数据统计分析看板
- [ ] 批量导入导出功能
- [ ] 消息推送与通知中心
- [ ] 高级问卷模板库
- [ ] 项目进度甘特图
- [ ] 多子账户支持

### 未来规划 (v2.0)
- [ ] 微信小程序版本
- [ ] 移动端APP
- [ ] AI智能审核辅助
- [ ] 区块链项目存证
- [ ] 第三方系统集成API
- [ ] 多语言国际化支持

## 🤝 贡献指南

我们欢迎任何形式的贡献！请遵循以下步骤：

### 代码贡献流程

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 代码规范
- 使用统一的代码格式化工具
- 编写清晰的提交信息
- 为新功能编写测试用例
- 更新相关文档

## 📄 许可证

本项目采用 [GPL-3.0 License](LICENSE) 许可证。

## 💬 支持与反馈

如果您在使用过程中遇到任何问题，或有改进建议，请通过以下方式联系我们：

- 提交 Issue
- 发送邮件至：nanansole@outlook.com

## 🙏 致谢

感谢所有为益护平台社区版贡献代码和建议的开发者和用户。

**特别感谢:**
- 参与测试的公益组织
- 提供技术支持的志愿者
- 社区维护人员

## 📋 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解详细的版本更新记录。

---

<div align="center">

**益护平台社区版**  
*让公益项目管理更简单、更规范*

[![GitHub stars](https://img.shields.io/github/stars/yourusername/yihu-platform-community?style=social)](https://github.com/yourusername/yihu-platform-community)
[![GitHub forks](https://img.shields.io/github/forks/yourusername/yihu-platform-community?style=social)](https://github.com/yourusername/yihu-platform-community)

</div>