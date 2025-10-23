# 使用官方OpenJDK 11 JRE作为基础镜像
FROM openjdk:17.0.2 

# 维护者信息
LABEL maintainer="library-management-system"

# 设置工作目录
WORKDIR /app

# 将构建产物复制到容器中
COPY target/library-management-system-1.0.0.jar app.jar

# 暴露应用端口
EXPOSE 8080

# 设置环境变量（可选，可根据需要配置）
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]