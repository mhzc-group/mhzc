# 该镜像需要依赖的基础镜像
FROM java:8
# 将当前maven目录生成的文件复制到docker容器的/目录下
COPY /target/mhzc-admin-api-0.1.0-exec.jar  /
# 声明服务运行在9000端口
EXPOSE 9000
# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-jar","/mhzc-admin-api-0.1.0-exec.jar"]
# 指定维护者的名字
MAINTAINER zhx