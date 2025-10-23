pipeline {
    agent any
    
    tools {
        // 使用在Jenkins中配置的Docker工具
        docker 'docker'
    }
    
    environment {
        // 定义环境变量
        GITHUB_REPO = 'papermooth/java-be'  // GitHub仓库名
        DOCKER_REGISTRY = '192.168.13.244:5000'  // Docker私有仓库地址
        DOCKER_IMAGE_NAME = 'library-management-system'  // Docker镜像名称
        DOCKER_IMAGE_TAG = "${env.BUILD_NUMBER}"  // 使用Jenkins构建号作为镜像标签
        DOCKER_FULL_IMAGE_NAME = "${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"  // 完整镜像名称
    }
    
    stages {
        stage('拉取代码') {
            steps {
                // 从GitHub拉取代码
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']], // GitHub默认分支
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [],
                    submoduleCfg: [],
                    userRemoteConfigs: [[
                        url: "git@github.com:papermooth/java-be.git",
                        credentialsId: 'github-credentials' // Jenkins中配置的GitHub SSH凭证ID
                    ]]
                ])
            }
        }
        
        stage('Maven打包') {
            steps {
                // 使用Maven Docker镜像打包项目
                sh '''
                    echo "开始Maven打包..."
                    # 检查Docker命令是否可用
                    if ! command -v docker &> /dev/null; then
                        echo "错误: Docker命令不可用！"
                        echo "请确认Jenkins中的Docker工具配置正确。"
                        exit 1
                    fi
                    echo "使用Docker运行Maven构建..."
                    docker run --rm -v "${PWD}:/usr/src/mymaven" -w /usr/src/mymaven maven:3.9.9-eclipse-temurin-17-noble mvn clean package -DskipTests
                '''
            }
        }
        
        stage('构建Docker镜像') {
            steps {
                // 检查Docker命令是否可用
                sh '''
                    if ! command -v docker &> /dev/null; then
                        echo "错误: Docker命令不可用！"
                        echo "请确认Jenkins中的Docker工具配置正确。"
                        exit 1
                    fi
                '''
                // 创建Dockerfile（如果不存在）
                script {
                    if (!fileExists('Dockerfile')) {
                        writeFile file: 'Dockerfile', text: '''
FROM openjdk:17.0.2 
WORKDIR /app
COPY target/library-management-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
'''
                    }
                }
                
                // 构建Docker镜像
                sh '''
                    echo "构建Docker镜像..."
                    docker build -t ${DOCKER_FULL_IMAGE_NAME} .
                '''
            }
        }
        
        stage('推送Docker镜像') {
            steps {
                // 检查Docker命令是否可用
                sh '''
                    if ! command -v docker &> /dev/null; then
                        echo "错误: Docker命令不可用！"
                        echo "请确认Jenkins中的Docker工具配置正确。"
                        exit 1
                    fi
                '''
                // 登录到Docker仓库并推送镜像
                withCredentials([usernamePassword(credentialsId: 'registry', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh '''
                        echo "登录到Docker仓库..."
                        docker login ${DOCKER_REGISTRY} -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}
                        echo "推送Docker镜像..."
                        docker push ${DOCKER_FULL_IMAGE_NAME}
                        
                        // 推送latest标签
                        docker tag ${DOCKER_FULL_IMAGE_NAME} ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest
                        docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest
                    '''
                }
            }
        }
        
        stage('清理') {
            steps {
                // 检查Docker命令是否可用
                sh '''
                    if ! command -v docker &> /dev/null; then
                        echo "警告: Docker命令不可用，跳过清理步骤！"
                        echo "请确认Jenkins中的Docker工具配置正确。"
                        exit 0
                    fi
                    // 清理本地Docker镜像
                    echo "清理本地Docker镜像..."
                    docker rmi ${DOCKER_FULL_IMAGE_NAME} || true
                    docker rmi ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest || true
                '''
            }
        }
    }
    
    post {
        success {
            echo '流水线执行成功！'
        }
        failure {
            echo '流水线执行失败！'
        }
        always {
            // 清理工作空间
            cleanWs()
        }
    }
}