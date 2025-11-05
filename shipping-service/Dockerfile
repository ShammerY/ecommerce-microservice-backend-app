# shipping-service Dockerfile
FROM eclipse-temurin:11-jre
ARG PROJECT_VERSION=0.1.0
RUN mkdir -p /home/app
WORKDIR /home/app

ENV SPRING_PROFILES_ACTIVE dev

COPY . .
# JAR del microservicio
ADD target/shipping-service-v${PROJECT_VERSION}.jar shipping-service.jar

# Puerto de exposición
EXPOSE 8600

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "shipping-service.jar"]
