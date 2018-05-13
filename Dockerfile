FROM maven:3-jdk-8-alpine as builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline \
    -s /usr/share/maven/ref/settings-docker.xml

COPY src ./src
RUN mvn package \
    -Dmaven.test.skip=true \
    -s /usr/share/maven/ref/settings-docker.xml && \
    mv target/dddsample*.jar target/dddsample.jar

FROM java:8-jdk-alpine as runner
EXPOSE 8080
RUN adduser -S app 
USER app
WORKDIR /app

COPY --from=builder /app/target/dddsample.jar /app/
CMD ["java", "-jar", "dddsample.jar"] 

