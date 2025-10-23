pipeline {
    agent any
    
    environment {
        // 定义环境变量
        GITHUB_REPO = 'papermooth/java-be'
        DOCKER_REGISTRY = '192.168.13.244:5000'
        DOCKER_IMAGE_NAME = 'library-management-system'
        DOCKER_IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKER_FULL_IMAGE_NAME = "${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
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
                // 使用Maven打包项目
                sh '''
                    echo "开始Maven打包..."
                    mvn clean package -DskipTests
                '''
            }
        }
        
        stage('构建Docker镜像') {
            steps {
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
                // 清理本地Docker镜像
                sh '''
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