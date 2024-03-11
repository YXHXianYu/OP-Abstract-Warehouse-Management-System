# 使用 OpenJDK 为基础镜像
FROM maven:3.8.5-openjdk-17

# 设置工作目录
WORKDIR /app

# 将 Maven 项目复制到容器中
COPY . ./

# 使用 Maven 构建应用
RUN mvn -s ./settings.xml clean package -X

# 指定运行时的端口
EXPOSE 8080

# 运行 SpringBoot 应用
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
