FROM maven:3-jdk-8-alpine as builder
WORKDIR /app
COPY . .
RUN mvn package

FROM java:8-jdk-alpine as runner
WORKDIR /app
COPY --from=builder /app/target/dddsample*.jar /app/
CMD java -jar dddsample*.jar 
