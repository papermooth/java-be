pipeline {
    agent any
    // 关键1：禁用默认 checkout 阶段，避免提前执行 git config
    options {
        skipDefaultCheckout(true)
    }
    environment {
        GITHUB_REPO_URL = "git@github.com:papermooth/java-be.git"
        GITHUB_CRED = "github-credentials"  // Jenkins 中配置的 SSH 凭证 ID
        DOCKER_REGISTRY = "192.168.13.244:5000"
        DOCKER_IMAGE_NAME = "library-management-system"
        DOCKER_IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKER_FULL_IMAGE_NAME = "${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
    }
    stages {
        // 阶段1：预处理（缓存 GitHub 密钥 + 初始化 Git 仓库）
        stage('预处理：初始化环境') {
            steps {
                script {
                    // 1. 缓存 GitHub ED25519 主机密钥（避免 SSH 连接时手动确认）
                    def githubKey = "github.com ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIOMqqnkVzrm0SdG6UOoqKLsabgH5C9okWi0dh2l9GKJl"
                    sh '''
                        mkdir -p ~/.ssh && chmod 700 ~/.ssh
                        touch ~/.ssh/known_hosts && chmod 600 ~/.ssh/known_hosts
                        if ! grep -qF '${githubKey}' ~/.ssh/known_hosts; then
                            echo '${githubKey}' >> ~/.ssh/known_hosts
                        fi
                    '''

                    // 2. 清理并初始化 Git 仓库（确保在 Git 目录内执行后续命令）
                    sh '''
                        echo "清理工作目录: ${PWD}"
                        rm -rf ./* .git  # 彻底删除旧文件和仓库
                        git init  # 手动初始化 Git 仓库
                        if [ ! -d ".git" ]; then
                            echo "Git 仓库初始化失败！"
                            exit 1
                        fi
                    '''

                    // 3. 验证 Docker 可用性（使用宿主机挂载的 Docker，无需 dockerTool）
                    sh '''
                        if ! docker --version >/dev/null 2>&1; then
                            echo "Docker 命令不可用！请检查宿主机 Docker 挂载是否正确。"
                            exit 1
                        fi
                        echo "Docker 版本: $(docker --version)"
                    '''
                }
            }
        }

        // 阶段2：拉取代码（此时已在 Git 仓库内，可正常执行 Git 命令）
        stage('拉取代码') {
            steps {
                withCredentials([sshUserPrivateKey(
                    credentialsId: env.GITHUB_CRED,
                    keyFileVariable: 'SSH_KEY'
                )]) {
                    sh '''
                        // 配置 SSH 私钥权限 + 指定密钥路径
                        chmod 600 ${SSH_KEY}
                        export GIT_SSH_COMMAND="ssh -i ${SSH_KEY}"
                        
                        // 添加远程仓库并拉取代码（避免默认 checkout 的配置问题）
                        git remote add origin ${GITHUB_REPO_URL}
                        git pull origin main --force  # 强制拉取 main 分支，避免冲突
                        git checkout main  # 确保切换到 main 分支
                        
                        // 验证拉取结果
                        echo "当前分支: $(git branch --show-current)"
                        echo "代码文件列表: $(ls -la)"
                    '''
                }
            }
        }

        // 阶段3：Maven 打包（复用之前逻辑，添加权限适配）
        stage('Maven打包') {
            steps {
                sh '''
                    echo "开始Maven打包..."
                    docker run --rm \
                      --user $(id -u):$(id -g) \  # 适配工作目录权限，避免文件无法读写
                      -v "${PWD}:/usr/src/mymaven" \
                      -w /usr/src/mymaven \
                      maven:3.9.9-eclipse-temurin-17-noble \
                      mvn clean package -DskipTests
                    
                    // 验证打包结果（确保生成 JAR 包）
                    if [ ! -f "target/${DOCKER_IMAGE_NAME}-1.0.0.jar" ]; then
                        echo "Maven 打包失败！未生成 JAR 包。"
                        exit 1
                    fi
                '''
            }
        }

        // 阶段4-6：构建/推送镜像、清理（保留原逻辑，修复镜像名称匹配）
        stage('构建Docker镜像') {
            steps {
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
                sh '''
                    echo "构建Docker镜像: ${DOCKER_FULL_IMAGE_NAME}"
                    docker build -t ${DOCKER_FULL_IMAGE_NAME} .
                    docker images | grep ${DOCKER_IMAGE_NAME}  # 验证镜像是否生成
                '''
            }
        }

        stage('推送Docker镜像') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'registry',
                    passwordVariable: 'DOCKER_PWD',
                    usernameVariable: 'DOCKER_USER'
                )]) {
                    sh '''
                        // 登录私有仓库（跳过 HTTPS 证书验证，因是内网仓库）
                        docker login ${DOCKER_REGISTRY} -u ${DOCKER_USER} -p ${DOCKER_PWD}
                        // 推送镜像
                        docker push ${DOCKER_FULL_IMAGE_NAME}
                        docker tag ${DOCKER_FULL_IMAGE_NAME} ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest
                        docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest
                        // 验证推送结果
                        echo "推送完成，私有仓库镜像: ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}"
                    '''
                }
            }
        }

        stage('清理') {
            steps {
                sh '''
                    echo "清理本地镜像..."
                    docker rmi ${DOCKER_FULL_IMAGE_NAME} || true
                    docker rmi ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest || true
                    echo "清理工作目录冗余文件..."
                    rm -rf target/  # 清理 Maven 构建产物
                '''
            }
        }
    }

    post {
        success {
            echo "流水线执行成功！镜像已推送至: ${DOCKER_FULL_IMAGE_NAME}"
        }
        failure {
            echo "流水线执行失败！请查看日志定位问题。"
        }
        always {
            // 修复 cleanWs 上下文问题：在 node 内执行
            node {
                cleanWs()
            }
        }
    }
}
