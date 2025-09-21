<div align="center">

# 🤝 YiHu Platform Community Edition

*One-stop Public Welfare Project and Activity Management Platform*

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

[:cn: 中文版本](README.md) •
[🚀 Quick Start](#-quick-start) •
[📖 Documentation](#-documentation) •
[🌟 Features](#-features) •
[🛠️ Tech Stack](#️-tech-stack) •
[🤝 Contributing](#-contributing)

---

</div>

## 📖 Project Introduction

YiHu Platform Community Edition is a comprehensive project and activity management platform designed specifically for Chinese non-profit organizations. The platform supports in-depth management of multiple projects and activities, achieving full-process digitization and seamless integration with expert resources to create an efficient public welfare ecosystem. The system standardizes project management processes, assists social organizations in improving project information, and ensures project quality and compliance through expert review mechanisms.

## 🌟 Core Features

### 🎯 Project Management
- Multi-project parallel management with real-time status and progress tracking
- Clear categorization, project creation and switching
- Expert review system with clear result status
- Project archive storage and retrieval

### 🎉 Activity Management
- Multi-activity creation and management within projects
- Activity name, time, location and description management
- Activity notification system
- Activity archive and detail image upload
- Activity approval mechanism

### ✅ Check-in Statistics System
- Accurate recording of activity check-in information
- Support for online check-in and paper sign-in sheet upload
- Automatic aggregation of participant statistics
- Real-time saving of check-in data

### 📰 Press Release Management
- Support for both image and link formats
- Mixed text and image press release upload
- External media link embedding
- One-click publishing to enhance promotional effects

### 📝 Satisfaction Survey
- Built-in questionnaire system supporting multiple question types
- Preset satisfaction survey questionnaires
- Visual analysis reports (bar charts, pie charts, line charts)
- Real-time data insights

### 👨‍🏫 Expert Customization System
- Built-in one-on-one chat and guidance functions
- Real-time communication with file and image sharing support
- Professional support throughout project design and execution
- Real-time message notifications

### 📸 Archive Management
- Image archive recording and categorized storage
- Aliyun OSS object storage integration
- Multimedia resource management
- Quick search and access

### 🔐 Permission Control
- **Expert** - Project review and professional guidance
- **System Admin** - Platform management and system configuration
- **Social Organization** - Project creation, activity management and daily operations

## 🛠️ Tech Stack

### Frontend Technologies
- **Framework**: Vue.js 3.x + Vite 3.0+
- **UI Components**: Element Plus (El Component Library)
- **Routing**: Vue Router
- **State Management**: Vuex
- **Build Tool**: Vite 3.0+
- **Package Manager**: npm 8.0+
- **Styling**: CSS3 + Responsive Design

### Backend Technologies
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security
- **Data Access**: MyBatis
- **API Documentation**: OpenAPI 3.0
- **Validation**: Hibernate Validator
- **Build Tool**: Maven 3.6+

### Data Storage
- **Primary Database**: MySQL 8.0+
- **Cache**: Redis 6.0+
- **File Storage**: Aliyun OSS Object Storage

### Third-party Services
- **SMS Service**: Aliyun SMS/Tencent SMS
- **Object Storage**: Aliyun OSS
- **Message Push**: WebSocket

## 🚀 Quick Start

### Environment Requirements

**Frontend Environment**
- Node.js 16.0+
- npm 8.0+
- Vue CLI 5.0+ or Vite 3.0+

**Backend Environment**
- JDK 17+ (Required)
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

**Cloud Service Configuration**
- Aliyun OSS Account
- SMS Service Account (Aliyun SMS/Tencent SMS)

### Installation Steps

```bash
# 1. Clone the project
git clone [Project Repository URL]
cd yihu-platform-community

# 2. Backend Configuration
cd backend
# Configure database connection
cp src/main/resources/application-example.yml src/main/resources/application.yml
# Edit configuration file

# Install dependencies and start backend service
mvn clean install
mvn spring-boot:run

# 3. Frontend Configuration
cd ../frontend
# Install dependencies
npm install
# or use yarn
yarn install

# Configure API interface address
cp .env.example .env
# Edit .env file

# Start frontend development server
npm run dev
# or use yarn
yarn dev
```

### Configuration

<details>
<summary>Click to expand detailed configuration</summary>

1. **Database Configuration** (`application.yml`)
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/yihu_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
       username: your_username
       password: your_password
       driver-class-name: com.mysql.cj.jdbc.Driver
   ```

2. **Redis Configuration** (`application.yml`)
   ```yaml
   spring:
     redis:
       host: localhost
       port: 6379
       password: your_redis_password
       database: 0
   ```

3. **Aliyun OSS Configuration** (`application.yml`)
   ```yaml
   aliyun:
     oss:
       endpoint: https://oss-cn-hangzhou.aliyuncs.com
       accessKeyId: your_access_key_id
       accessKeySecret: your_access_key_secret
       bucketName: your_bucket_name
   ```

4. **SMS Service Configuration** (`application.yml`)
   ```yaml
   sms:
     accessKeyId: your_sms_access_key
     accessKeySecret: your_sms_secret_key
     signName: YiHu Platform
     templateCode: SMS_123456789
   ```

5. **Frontend API Configuration** (`.env`)
   ```env
   VITE_API_BASE_URL=http://localhost:8080/api
   VITE_OSS_DOMAIN=https://your-bucket.oss-cn-hangzhou.aliyuncs.com
   ```

</details>

## 📂 Project Structure

<details>
<summary>Click to expand project structure</summary>

```
yihu-platform-community/
├── backend/                 # Spring Boot Backend
│   ├── src/main/java/
│   │   ├── controller/     # Controller Layer
│   │   ├── service/        # Service Layer
│   │   ├── mapper/         # Data Access Layer
│   │   ├── entity/         # Entity Classes
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── config/         # Configuration Classes
│   │   └── utils/          # Utility Classes
│   ├── src/main/resources/
│   │   ├── mapper/         # MyBatis Mapping Files
│   │   ├── application.yml # Configuration File
│   │   └── db/migration/   # Database Migration Scripts
│   └── pom.xml            # Maven Configuration
├── frontend/              # Vue.js Frontend
│   ├── src/
│   │   ├── components/    # Common Components
│   │   ├── views/         # Page Components
│   │   │   ├── project/   # Project Management Pages
│   │   │   ├── activity/  # Activity Management Pages
│   │   │   ├── survey/    # Satisfaction Survey Pages
│   │   │   ├── news/      # News Management Pages
│   │   │   ├── chat/      # Expert Chat Pages
│   │   │   └── archive/   # Archive Management Pages
│   │   ├── api/           # API Interface Wrappers
│   │   ├── utils/         # Utility Functions
│   │   ├── store/         # State Management
│   │   ├── router/        # Routing Configuration
│   │   └── assets/        # Static Resources
│   ├── public/            # Public Resources
│   ├── .env.example       # Environment Variables Example
│   ├── package.json       # Dependency Management
│   └── vite.config.js     # Vite Configuration
├── docs/                  # Project Documentation
│   ├── api.md            # API Documentation
│   ├── deployment.md     # Deployment Documentation
│   └── user-manual.md    # User Manual
├── sql/                  # Database Scripts
│   ├── init.sql          # Initialization Scripts
│   └── data.sql          # Test Data
└── README.md             # Project Description
```

</details>

## 🌟 System Features

### 🌟 One-stop Management Experience
- Deep integration of projects and activities
- Full-process digital management from project creation to activity execution
- Responsive design for PC and mobile

### 🔄 Intelligent Review Mechanism
- Dual protection for project and activity review
- Expert review system ensuring project quality
- Final review by system administrators

### 💬 Real-time Expert Guidance
- Built-in chat system with file and image sharing
- One-on-one professional guidance
- Real-time message notifications

### 📊 Data Visualization Analysis
- Automatic chart generation for survey results
- Multi-dimensional data statistics
- Automatic check-in data aggregation

### ☁️ Secure Cloud Storage
- Aliyun OSS for secure and reliable data storage
- Automatic image archive classification
- Large file upload support

## 🗺️ Development Roadmap

### Current Version (v1.0)
- [x] Multi-project parallel management system
- [x] Multi-activity management within projects
- [x] Check-in statistics and paper form upload
- [x] Image archive recording and OSS storage
- [x] Press release management (image + link)
- [x] Built-in satisfaction survey system
- [x] Expert one-on-one communication system
- [x] Three-role permission control
- [x] Dual review process for projects and activities
- [x] SMS verification code login
- [x] Responsive design and mobile adaptation

### Next Version (v1.2)
- [ ] Data statistics analysis dashboard
- [ ] Batch import and export functions
- [ ] Message push and notification center
- [ ] Advanced questionnaire template library
- [ ] Project progress Gantt chart
- [ ] Multi-tenant support

### Future Plans (v2.0)
- [ ] WeChat Mini Program version
- [ ] Mobile application
- [ ] AI-assisted intelligent review
- [ ] Blockchain project certification
- [ ] Third-party system integration API
- [ ] Multi-language internationalization support

## 🤝 Contributing

We welcome contributions of any kind! Please follow these steps:

### Code Contribution Process

1. Fork this project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Create a Pull Request

### Code Standards
- Use consistent code formatting tools
- Write clear commit messages
- Write test cases for new features
- Update relevant documentation

## 📄 License

This project is licensed under the [GPL-3.0 License](LICENSE).

## 💬 Support & Feedback

If you encounter any problems during use or have suggestions for improvement, please contact us through the following methods:

- Submit an [Issue](Project Repository URL/issues)
- Send email to：[Contact Email]

## 🙏 Acknowledgments

Thanks to all developers and users who have contributed code and suggestions to YiHu Platform Community Edition.

**Special Thanks:**
- Non-profit organizations participating in testing
- Volunteers providing technical support
- Community maintainers

## 📋 Changelog

See [CHANGELOG.md](CHANGELOG.md) for detailed version update records.

---

<div align="center">

**YiHu Platform Community Edition**  
*Making public welfare project management simpler and more standardized*

[![GitHub stars](https://img.shields.io/github/stars/yourusername/yihu-platform-community?style=social)](https://github.com/yourusername/yihu-platform-community)
[![GitHub forks](https://img.shields.io/github/forks/yourusername/yihu-platform-community?style=social)](https://github.com/yourusername/yihu-platform-community)

</div>