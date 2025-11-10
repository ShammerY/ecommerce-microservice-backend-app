# order-service Dockerfile
FROM eclipse-temurin:11-jre
ARG PROJECT_VERSION=0.1.0
RUN mkdir -p /home/app
WORKDIR /home/app

ENV SPRING_PROFILES_ACTIVE dev

COPY . .
# Cambiamos al JAR correspondiente de order-service
# ADD target/order-service-v${PROJECT_VERSION}.jar order-service.jar
ADD target/*.jar order-service.jar

# Puerto de exposición del microservicio
EXPOSE 8300
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "order-service.jar"]
