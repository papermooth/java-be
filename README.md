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

### 6. 停止应用
如果应用在前台运行，可以使用以下方法停止：
- 在运行应用的终端中，按 `Ctrl + C` 组合键

如果应用在后台运行，可以使用以下命令停止：
```bash
# 查找应用进程ID
lsof -i:8080 | grep LISTEN | awk '{print $2}'
# 终止进程（将 <PID> 替换为实际的进程ID）
kill <PID>
# 或者使用一步命令
lsof -i:8080 | grep LISTEN | awk '{print $2}' | xargs kill
```

## Jenkins流水线配置

本项目已包含Jenkinsfile，用于配置CI/CD流水线，实现从GitHub拉取代码、Maven打包、构建Docker镜像并推送到私人仓库的自动化流程。

### 前置条件
1. 已安装Jenkins服务器
2. Jenkins已安装必要插件：Git、Pipeline、Docker Pipeline
3. 已在GitHub上创建并推送项目代码
4. 已准备好Docker私人仓库

### Jenkins配置步骤

#### 1. 配置凭证
1. 登录Jenkins管理界面
2. 点击"凭据管理" > "系统" > "全局凭据" > "添加凭据"
3. 添加GitHub凭据（用于拉取代码）：
   - 类型：Username with password或SSH Username with private key
   - ID：设置为`github-credentials`（与Jenkinsfile中保持一致）
   - 填写GitHub用户名和访问令牌（或SSH私钥）
4. 添加Docker仓库凭据（用于推送镜像）：
   - 类型：Username with password
   - ID：设置为`registry`（与Jenkinsfile中保持一致）
   - 填写Docker仓库用户名和密码

#### 2. 准备Docker环境
确保Jenkins服务器上已安装Docker，并且Jenkins用户有权限访问Docker。

```bash
# 将Jenkins用户添加到docker组
sudo usermod -aG docker jenkins
# 重启Jenkins
sudo systemctl restart jenkins
```

#### 3. 修改Jenkinsfile（可选）
Jenkinsfile已配置为：
- `GITHUB_REPO`：`papermooth/java-be`
- `DOCKER_REGISTRY`：`192.168.13.244:5000`
- 仓库URL：`git@github.com:papermooth/java-be.git`（SSH方式）
- `branches: [[name: '*/master']]`：根据实际默认分支调整（可能是main）

#### 4. 创建Jenkins流水线任务
1. 在Jenkins首页点击"新建任务"
2. 输入任务名称，选择"流水线"，点击确定
3. 在配置页面，找到"流水线"部分
4. 选择"Pipeline script from SCM"
5. SCM选择"Git"
6. 填写仓库URL：`git@github.com:papermooth/java-be.git`
7. 选择之前配置的GitHub凭据
8. 指定分支（如`*/master`或`*/main`）
9. 脚本路径保持为`Jenkinsfile`
10. 点击"保存"

#### 5. 运行流水线
1. 在任务页面点击"立即构建"
2. 查看构建日志，监控流水线执行状态
3. 成功后，可以在Docker私人仓库中查看推送的镜像

### 流水线各阶段说明
1. **拉取代码**：从GitHub仓库拉取最新代码
2. **Maven打包**：使用Maven构建项目，生成JAR文件
3. **构建Docker镜像**：基于生成的JAR文件创建Docker镜像
4. **推送Docker镜像**：将镜像推送到配置的私人仓库，并添加latest标签
5. **清理**：删除本地构建的Docker镜像，释放空间

### 常见问题处理
1. **Docker权限问题**：确保Jenkins用户加入了docker组并重启Jenkins服务
2. **凭据错误**：检查Jenkins中配置的凭据ID是否与Jenkinsfile中一致
3. **网络连接问题**：确保Jenkins服务器可以访问GitHub和Docker仓库
4. **构建失败**：查看详细构建日志，定位具体错误原因

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