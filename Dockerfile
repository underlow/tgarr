FROM --platform=linux/x86_64 openjdk:19-jdk-alpine as builder
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} application.jar

RUN java -Djarmode=layertools -jar application.jar extract

FROM --platform=linux/x86_64 openjdk:19-jdk-alpine
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
#ENTRYPOINT ["java", ${JAVA_OPTS}, "org.springframework.boot.loader.JarLauncher"]
ENTRYPOINT "java" ${JAVA_OPTS} "org.springframework.boot.loader.JarLauncher"
