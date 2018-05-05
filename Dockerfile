FROM maven:3-jdk-8-alpine as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve dependency:go-offline -s /usr/share/maven/ref/settings-docker.xml
COPY src ./src
RUN mvn package -Djar.finalName=dddsample -s /usr/share/maven/ref/settings-docker.xml

FROM java:8-jdk-alpine as runner
WORKDIR /app
COPY --from=builder /app/target/dddsample.jar /app/
CMD ["java", "-jar", "dddsample.jar"] 

