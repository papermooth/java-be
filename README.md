# 图书管理系统后端项目

## 项目介绍
这是一个图书管理系统的后端应用，基于Spring Boot框架开发，提供用户认证、权限管理等功能。

## 环境要求
- JDK 1.8 或更高版本
- Maven 3.6.0 或更高版本
- MySQL 8.0

## 启动步骤

### 1. 数据库准备
确保MySQL数据库已启动，并创建名为`library_management`的数据库：
```sql
CREATE DATABASE library_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 配置数据库连接
修改`src/main/resources/application.properties`文件中的数据库连接信息：
```properties
spring.datasource.url=jdbc:mysql://192.168.13.247:3306/library_management?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true&failOverReadOnly=false&maxReconnects=10
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. 初始化数据库
项目包含数据库初始化脚本，位于`src/main/resources/init.sql`，可以手动执行或使用应用程序自动执行。

### 4. 编译和打包
使用Maven编译和打包项目：
```bash
cd /root/java-be
mvn clean package -DskipTests
```

### 5. 运行应用
执行打包后的JAR文件：
```bash
java -jar target/library-management-system-1.0.0.jar
```

### 6. 访问系统
应用启动后，可以通过以下方式访问：
- 登录页面：http://localhost:8080/
- API接口：http://localhost:8080/api/*

## 功能特点

### 核心功能
- 用户登录认证
- 权限管理
- 图书管理
- 用户管理

### API接口
- POST /api/login - 用户登录
- GET /api/user/* - 用户相关接口
- GET /api/permission/* - 权限相关接口

## 项目结构
```
src/main/
├── java/com/library/          # 源代码
│   ├── controller/           # 控制器
│   ├── service/              # 服务层
│   ├── mapper/               # 数据访问层
│   ├── entity/               # 实体类
│   ├── common/               # 公共组件
│   └── config/               # 配置类
└── resources/                # 资源文件
    ├── static/               # 静态资源
    ├── mapper/               # MyBatis映射文件
    ├── application.properties # 应用配置
    └── init.sql              # 数据库初始化脚本
```

## 常见问题处理

### 端口被占用
如果启动时提示端口8080被占用，可以使用以下命令停止占用端口的进程：
```bash
lsof -i:8080 | grep LISTEN | awk '{print $2}' | xargs kill
```

### 数据库连接失败
检查数据库连接信息是否正确，确保数据库服务正在运行，并且用户有足够的权限。

## 开发指南

### 添加新功能
1. 在`entity`包中定义新的实体类
2. 在`mapper`包中创建对应的Mapper接口
3. 在`resources/mapper`目录下创建XML映射文件
4. 在`service`包中实现业务逻辑
5. 在`controller`包中定义API接口

### 测试接口
可以使用Postman或其他API测试工具测试接口功能。

## 安全注意事项
- 请确保在生产环境中修改默认密码
- 建议配置HTTPS以提高安全性
- 定期备份数据库

## 版本信息
- 当前版本：1.0.0
- 最后更新：2025-10-23

## 将项目上传到GitHub

### 在GitHub上创建仓库
1. 登录你的GitHub账号
2. 点击右上角的「+」按钮，选择「New repository」
3. 填写仓库信息：
   - Repository name: 输入仓库名称，如 `library-management-system`
   - Description: 可选，添加仓库描述
   - 选择仓库可见性：公开（Public）或私有（Private）
   - 不要勾选「Add a README file」（因为我们已经有了）
   - 不要勾选「Add .gitignore」（我们稍后添加）
   - 不要勾选「Choose a license」
4. 点击「Create repository」按钮

### 本地配置并推送代码
1. 在项目根目录初始化Git仓库：
```bash
cd /root/java-be
git init
git add .
git commit -m "Initial commit"
```

2. 添加.gitignore文件（忽略不必要的文件）：
```bash
touch .gitignore
```

3. 编辑.gitignore文件，添加以下内容：
```
# Maven
/target/
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# IDE
.idea/
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
Thumbs.db

# Logs
logs/
*.log

# Environment variables
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# Temporary files
*.tmp
*.temp
.cache/
```

4. 将.gitignore添加到Git并提交：
```bash
git add .gitignore
git commit -m "Add .gitignore file"
```

5. 关联远程仓库（将下面的URL替换为你刚创建的GitHub仓库URL）：
```bash
git remote add origin https://github.com/your-username/library-management-system.git
```

6. 推送代码到GitHub：
```bash
git push -u origin master
```

### 注意事项
- 如果你是第一次使用Git与GitHub交互，可能需要配置Git的用户名和邮箱：
  ```bash
  git config --global user.name "Your Name"
  git config --global user.email "your-email@example.com"
  ```
- 如果推送时需要身份验证，GitHub可能要求使用Personal Access Token而不是密码。请参考GitHub官方文档创建和使用Access Token。
- 如果遇到权限问题，请确保你的GitHub账号有仓库的写入权限。